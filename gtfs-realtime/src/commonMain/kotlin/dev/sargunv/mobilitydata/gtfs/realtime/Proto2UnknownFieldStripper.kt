@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.protobuf.ProtoIntegerType
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.protobuf.ProtoType

private const val VARINT = 0
private const val FIXED64 = 1
private const val LEN = 2
private const val START_GROUP = 3
private const val END_GROUP = 4
private const val FIXED32 = 5

internal class ProtoMessageSchema(val fields: Map<Int, ProtoFieldSchema>)

internal sealed class ProtoFieldSchema(val wireTypes: Set<Int>) {
  class Scalar(wireTypes: Set<Int>) : ProtoFieldSchema(wireTypes)

  class Enum(val numbers: Set<Int>) : ProtoFieldSchema(setOf(VARINT))

  class Message(val schema: ProtoMessageSchema) : ProtoFieldSchema(setOf(LEN))
}

/**
 * Drops the field occurrences a conformant proto2 parser treats as unknown but kotlinx rejects:
 * unrecognized enum numbers, known fields arriving with a wire type that doesn't match the schema,
 * and groups, which no GTFS Realtime field uses. Malformed input still fails with a
 * [SerializationException]. The rewrite is local to the byte array and shares no decode session.
 */
internal object Proto2UnknownFieldStripper {
  fun strip(
    bytes: ByteArray,
    schema: ProtoMessageSchema = GtfsRealtimeWireSchema.feedMessage,
  ): ByteArray = copyMessage(WireReader(bytes, 0, bytes.size), schema)

  private fun copyMessage(reader: WireReader, schema: ProtoMessageSchema): ByteArray {
    val out = ByteSink(reader.remaining)
    while (reader.hasMore) {
      val start = reader.position
      val tag = reader.readVarint()
      val field = schema.fields[(tag ushr 3).toInt()]
      val wireType = (tag and 7L).toInt()
      val expected = field == null || wireType in field.wireTypes
      when (wireType) {
        VARINT -> {
          val value = reader.readVarint()
          if (expected && (field !is ProtoFieldSchema.Enum || value.toInt() in field.numbers)) {
            reader.copySince(start, out)
          }
        }
        LEN -> {
          val payload = reader.readLengthDelimited()
          when {
            field is ProtoFieldSchema.Message -> {
              val rewritten = copyMessage(payload, field.schema)
              out.writeVarint(tag)
              out.writeVarint(rewritten.size.toLong())
              out.write(rewritten, 0, rewritten.size)
            }
            expected -> reader.copySince(start, out)
          }
        }
        FIXED64,
        FIXED32 -> {
          reader.skip(if (wireType == FIXED64) 8 else 4)
          if (expected) reader.copySince(start, out)
        }
        START_GROUP -> reader.skipGroup(tag ushr 3)
        else -> unexpectedWireType(wireType)
      }
    }
    return out.toByteArray()
  }

  private fun malformed(message: String): Nothing = throw SerializationException(message)

  private fun unexpectedWireType(wireType: Int): Nothing =
    malformed("Unexpected protobuf wire type $wireType")

  private class WireReader(private val bytes: ByteArray, start: Int, private val end: Int) {
    var position = start
      private set

    val hasMore: Boolean
      get() = position < end

    val remaining: Int
      get() = end - position

    fun readVarint(): Long {
      var result = 0L
      var shift = 0
      while (true) {
        if (position >= end) malformed("Truncated protobuf varint")
        val byte = bytes[position++].toInt() and 0xFF
        result = result or ((byte and 0x7F).toLong() shl shift)
        if (byte and 0x80 == 0) return result
        shift += 7
        if (shift > 63) malformed("Protobuf varint too long")
      }
    }

    fun readLengthDelimited(): WireReader {
      val length = readVarint()
      if (length !in 0..remaining.toLong()) malformed("Truncated protobuf bytes")
      val start = position
      skip(length.toInt())
      return WireReader(bytes, start, position)
    }

    fun skip(count: Int) {
      if (count > remaining) malformed("Truncated protobuf bytes")
      position += count
    }

    fun skipGroup(number: Long) {
      val open = mutableListOf(number)
      while (open.isNotEmpty()) {
        val tag = readVarint()
        when (val wireType = (tag and 7L).toInt()) {
          VARINT -> readVarint()
          FIXED64 -> skip(8)
          LEN -> readLengthDelimited()
          FIXED32 -> skip(4)
          START_GROUP -> open.add(tag ushr 3)
          END_GROUP ->
            if (open.removeAt(open.lastIndex) != tag ushr 3)
              malformed("Mismatched protobuf end group")
          else -> unexpectedWireType(wireType)
        }
      }
    }

    fun copySince(start: Int, out: ByteSink) {
      out.write(bytes, start, position)
    }
  }

  private class ByteSink(initial: Int) {
    private var buf = ByteArray(initial.coerceAtLeast(16))
    private var size = 0

    fun write(src: ByteArray, from: Int, to: Int) {
      ensure(to - from)
      src.copyInto(buf, size, from, to)
      size += to - from
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

internal object GtfsRealtimeWireSchema {
  val feedMessage: ProtoMessageSchema = messageSchema(unwrap(FeedMessage.serializer().descriptor))

  internal fun messageSchema(descriptor: SerialDescriptor): ProtoMessageSchema {
    val fields = mutableMapOf<Int, ProtoFieldSchema>()
    for (index in 0 until descriptor.elementsCount) {
      val number = protoNumber(descriptor, index) ?: continue
      val field =
        fieldSchema(
          unwrap(descriptor.getElementDescriptor(index)),
          descriptor.getElementAnnotations(index).filterIsInstance<ProtoType>().singleOrNull()?.type,
        ) ?: continue
      fields[number] = field
    }
    return ProtoMessageSchema(fields)
  }

  private fun fieldSchema(
    descriptor: SerialDescriptor,
    integerType: ProtoIntegerType?,
  ): ProtoFieldSchema? =
    when (descriptor.kind) {
      SerialKind.ENUM -> ProtoFieldSchema.Enum(enumNumbers(descriptor))
      StructureKind.CLASS,
      StructureKind.OBJECT -> ProtoFieldSchema.Message(messageSchema(descriptor))
      StructureKind.LIST -> listFieldSchema(unwrap(descriptor.getElementDescriptor(0)), integerType)
      is PrimitiveKind ->
        ProtoFieldSchema.Scalar(setOf(scalarWireType(descriptor.kind, integerType)))
      else -> null
    }

  private fun listFieldSchema(
    element: SerialDescriptor,
    integerType: ProtoIntegerType?,
  ): ProtoFieldSchema? {
    val kind = element.kind
    return when {
      kind == PrimitiveKind.BYTE -> ProtoFieldSchema.Scalar(setOf(LEN))
      kind is PrimitiveKind && kind != PrimitiveKind.STRING ->
        ProtoFieldSchema.Scalar(setOf(scalarWireType(kind, integerType), LEN))
      else -> fieldSchema(element, integerType)
    }
  }

  private fun scalarWireType(kind: SerialKind, integerType: ProtoIntegerType?): Int =
    when (kind) {
      PrimitiveKind.STRING -> LEN
      PrimitiveKind.FLOAT -> FIXED32
      PrimitiveKind.DOUBLE -> FIXED64
      PrimitiveKind.INT if integerType == ProtoIntegerType.FIXED -> FIXED32
      PrimitiveKind.LONG if integerType == ProtoIntegerType.FIXED -> FIXED64
      else -> VARINT
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
