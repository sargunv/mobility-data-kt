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

internal class ProtoMessageSchema(val fields: Map<Int, ProtoFieldSchema>) {
  private val byNumber =
    arrayOfNulls<ProtoFieldSchema>((fields.keys.maxOrNull() ?: -1) + 1).also {
      for ((number, field) in fields) it[number] = field
    }

  fun field(number: Int): ProtoFieldSchema? = byNumber.getOrNull(number)
}

internal sealed class ProtoFieldSchema(val wireTypes: Set<Int>) {
  private val wireTypeMask = wireTypes.fold(0) { mask, wireType -> mask or (1 shl wireType) }

  fun accepts(wireType: Int): Boolean = wireTypeMask and (1 shl wireType) != 0

  class Scalar(wireTypes: Set<Int>) : ProtoFieldSchema(wireTypes)

  class Enum(val numbers: Set<Int>) : ProtoFieldSchema(setOf(VARINT)) {
    private val known =
      BooleanArray((numbers.maxOrNull() ?: -1) + 1).also {
        for (number in numbers) it[number] = true
      }

    fun isKnown(number: Int): Boolean = known.getOrElse(number) { false }
  }

  class Message(val schema: ProtoMessageSchema) : ProtoFieldSchema(setOf(LEN))
}

/**
 * Drops the field occurrences a conformant proto2 parser treats as unknown but kotlinx rejects:
 * unrecognized enum numbers, known fields arriving with a wire type that doesn't match the schema,
 * and groups, which no GTFS Realtime field uses. Malformed input still fails with a
 * [SerializationException]. The rewrite is local to the byte array and shares no decode session.
 *
 * Most feeds have nothing to drop, so a first pass checks for that without allocating and returns
 * [bytes] itself when it finds nothing.
 *
 * This was the production decode path before [GtfsRealtimeDecoder]. It now backs [oracleDecode],
 * which the tests compare that decoder against.
 */
internal object Proto2UnknownFieldStripper {
  fun strip(
    bytes: ByteArray,
    schema: ProtoMessageSchema = GtfsRealtimeWireSchema.feedMessage,
  ): ByteArray {
    if (keepsEverything(WireReader(bytes, 0, bytes.size), bytes.size, schema)) return bytes
    return copyMessage(WireReader(bytes, 0, bytes.size), schema)
  }

  /**
   * Whether [copyMessage] would keep every field occurrence up to [end]. Returns false at the first
   * occurrence it would drop, and at malformed input the reader doesn't reject itself, so that
   * [copyMessage] reports it.
   */
  private fun keepsEverything(reader: WireReader, end: Int, schema: ProtoMessageSchema): Boolean {
    while (reader.position < end) {
      val tag = reader.readVarint()
      val field = schema.field(fieldNumber(tag))
      val wireType = (tag and 7L).toInt()
      if (field != null && !field.accepts(wireType)) return false
      when (wireType) {
        VARINT -> {
          val value = reader.readVarint()
          if (field is ProtoFieldSchema.Enum && !field.isKnown(value.toInt())) return false
        }
        LEN -> {
          val length = reader.readVarint()
          if (length !in 0..(end - reader.position).toLong()) return false
          val payloadEnd = reader.position + length.toInt()
          if (field is ProtoFieldSchema.Message) {
            if (!keepsEverything(reader, payloadEnd, field.schema)) return false
          } else {
            reader.skip(length.toInt())
          }
        }
        FIXED64 -> reader.skip(8)
        FIXED32 -> reader.skip(4)
        else -> return false
      }
    }
    return reader.position == end
  }

  private fun copyMessage(reader: WireReader, schema: ProtoMessageSchema): ByteArray {
    val out = ByteSink(reader.remaining)
    while (reader.hasMore) {
      val start = reader.position
      val tag = reader.readVarint()
      val field = schema.field(fieldNumber(tag))
      val wireType = (tag and 7L).toInt()
      val expected = field == null || field.accepts(wireType)
      when (wireType) {
        VARINT -> {
          val value = reader.readVarint()
          if (expected && (field !is ProtoFieldSchema.Enum || field.isKnown(value.toInt()))) {
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
        START_GROUP -> reader.skipGroup(fieldNumber(tag))
        else -> unexpectedWireType(wireType)
      }
    }
    return out.toByteArray()
  }

  /**
   * Like kotlinx and protobuf-java, only the low 32 bits of an overlong tag varint count. This used
   * to read bits 3 to 34, so a tag above 32 bits could be kept as unknown here and then decoded by
   * kotlinx as a known field, without the stripping above.
   */
  private fun fieldNumber(tag: Long): Int = tag.toInt() ushr 3

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

    fun skipGroup(number: Int) {
      val open = mutableListOf(number)
      while (open.isNotEmpty()) {
        val tag = readVarint()
        when (val wireType = (tag and 7L).toInt()) {
          VARINT -> readVarint()
          FIXED64 -> skip(8)
          LEN -> readLengthDelimited()
          FIXED32 -> skip(4)
          START_GROUP -> open.add(fieldNumber(tag))
          END_GROUP ->
            if (open.removeAt(open.lastIndex) != fieldNumber(tag))
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
          descriptor
            .getElementAnnotations(index)
            .filterIsInstance<ProtoType>()
            .singleOrNull()
            ?.type,
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
