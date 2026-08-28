@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Drops unrecognized proto2 enum occurrences, and unexpected wire types on enum fields, so they
 * behave like unknown fields. The rewrite is local to the byte array and shares no decode session.
 */
internal object Proto2UnknownEnumStripper {
  private const val VARINT = 0
  private const val FIXED64 = 1
  private const val LEN = 2
  private const val FIXED32 = 5

  internal class MessageSchema(
    val messages: Map<Int, MessageSchema> = emptyMap(),
    val enums: Map<Int, Set<Int>> = emptyMap(),
  ) {
    internal fun hasEnums(): Boolean = enums.isNotEmpty() || messages.values.any { it.hasEnums() }
  }

  fun strip(
    bytes: ByteArray,
    schema: MessageSchema = GtfsRealtimeEnumSchema.feedMessage,
  ): ByteArray = copyMessage(WireReader(bytes), schema)

  private fun copyMessage(reader: WireReader, schema: MessageSchema): ByteArray {
    val out = ByteSink(reader.remaining)
    while (reader.hasMore) {
      val tag = reader.readVarint()
      val field = (tag ushr 3).toInt()
      val wireType = (tag and 7L).toInt()
      val allowed = schema.enums[field]
      when (wireType) {
        VARINT -> {
          val value = reader.readVarint()
          if (allowed == null || value.toInt() in allowed) {
            out.writeVarint(tag)
            out.writeVarint(value)
          }
        }
        LEN -> {
          val payload = reader.readBytes(reader.readVarint().toInt())
          val child = schema.messages[field]
          when {
            child != null -> {
              val rewritten = copyMessage(WireReader(payload), child)
              out.writeVarint(tag)
              out.writeVarint(rewritten.size.toLong())
              out.write(rewritten)
            }
            allowed != null -> Unit
            else -> {
              out.writeVarint(tag)
              out.writeVarint(payload.size.toLong())
              out.write(payload)
            }
          }
        }
        FIXED64 -> {
          val payload = reader.readBytes(8)
          if (allowed == null) {
            out.writeVarint(tag)
            out.write(payload)
          }
        }
        FIXED32 -> {
          val payload = reader.readBytes(4)
          if (allowed == null) {
            out.writeVarint(tag)
            out.write(payload)
          }
        }
        else -> error("Unsupported protobuf wire type $wireType")
      }
    }
    return out.toByteArray()
  }

  private class WireReader(private val bytes: ByteArray) {
    private var index = 0
    val hasMore: Boolean
      get() = index < bytes.size

    val remaining: Int
      get() = bytes.size - index

    fun readVarint(): Long {
      var result = 0L
      var shift = 0
      while (true) {
        check(index < bytes.size) { "Truncated protobuf varint" }
        val byte = bytes[index++].toInt() and 0xFF
        result = result or ((byte and 0x7F).toLong() shl shift)
        if (byte and 0x80 == 0) return result
        shift += 7
        check(shift <= 63) { "Protobuf varint too long" }
      }
    }

    fun readBytes(count: Int): ByteArray {
      check(count >= 0 && index + count <= bytes.size) { "Truncated protobuf bytes" }
      val slice = bytes.copyOfRange(index, index + count)
      index += count
      return slice
    }
  }

  private class ByteSink(initial: Int) {
    private var buf = ByteArray(initial.coerceAtLeast(16))
    private var size = 0

    fun write(src: ByteArray) {
      ensure(src.size)
      src.copyInto(buf, size)
      size += src.size
    }

    fun writeVarint(value: Long) {
      var remaining = value
      while (remaining ushr 7 != 0L) {
        writeByte(((remaining and 0x7F) or 0x80).toByte())
        remaining = remaining ushr 7
      }
      writeByte((remaining and 0x7F).toByte())
    }

    fun toByteArray(): ByteArray = buf.copyOf(size)

    private fun writeByte(byte: Byte) {
      ensure(1)
      buf[size++] = byte
    }

    private fun ensure(extra: Int) {
      val needed = size + extra
      if (needed <= buf.size) return
      var cap = buf.size
      while (cap < needed) cap *= 2
      buf = buf.copyOf(cap)
    }
  }
}

internal object GtfsRealtimeEnumSchema {
  val feedMessage: Proto2UnknownEnumStripper.MessageSchema =
    schemaFrom(FeedMessage.serializer().descriptor)

  internal fun schemaFrom(descriptor: SerialDescriptor): Proto2UnknownEnumStripper.MessageSchema {
    val current = unwrap(descriptor)
    return when (current.kind) {
      StructureKind.LIST -> schemaFrom(current.getElementDescriptor(0))
      SerialKind.ENUM -> Proto2UnknownEnumStripper.MessageSchema()
      else -> messageSchema(current)
    }
  }

  private fun messageSchema(descriptor: SerialDescriptor): Proto2UnknownEnumStripper.MessageSchema {
    val messages = mutableMapOf<Int, Proto2UnknownEnumStripper.MessageSchema>()
    val enums = mutableMapOf<Int, Set<Int>>()
    for (index in 0 until descriptor.elementsCount) {
      val number = protoNumber(descriptor, index) ?: continue
      val child = unwrap(descriptor.getElementDescriptor(index))
      when (child.kind) {
        SerialKind.ENUM -> enums[number] = enumNumbers(child)
        StructureKind.LIST -> {
          val element = unwrap(child.getElementDescriptor(0))
          when (element.kind) {
            SerialKind.ENUM -> enums[number] = enumNumbers(element)
            StructureKind.CLASS,
            StructureKind.OBJECT -> {
              val nested = schemaFrom(element)
              if (nested.hasEnums()) messages[number] = nested
            }
            else -> Unit
          }
        }
        StructureKind.CLASS,
        StructureKind.OBJECT -> {
          val nested = schemaFrom(child)
          if (nested.hasEnums()) messages[number] = nested
        }
        else -> Unit
      }
    }
    return Proto2UnknownEnumStripper.MessageSchema(messages, enums)
  }

  private fun unwrap(descriptor: SerialDescriptor): SerialDescriptor {
    var current = descriptor
    while (current.isInline && current.elementsCount == 1) {
      current = current.getElementDescriptor(0)
    }
    return current
  }

  private fun protoNumber(descriptor: SerialDescriptor, index: Int): Int? =
    descriptor.getElementAnnotations(index).filterIsInstance<ProtoNumber>().singleOrNull()?.number

  private fun enumNumbers(descriptor: SerialDescriptor): Set<Int> = buildSet {
    for (index in 0 until descriptor.elementsCount) {
      add(protoNumber(descriptor, index) ?: index)
    }
  }
}
