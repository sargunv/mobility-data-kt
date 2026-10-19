@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.random.Random
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.protobuf.ProtoNumber

/** A field of a GTFS Realtime message, read from the serial descriptor of its data class. */
internal class ProtoField(
  val name: String,
  val number: Int,
  /** The field's type, or its element type when [repeated]. */
  val descriptor: SerialDescriptor,
  val repeated: Boolean,
  /** Whether the property has no default, so decoding fails without it. */
  val required: Boolean,
) {
  val kind: SerialKind
    get() = descriptor.kind

  val wireType: Int
    get() =
      when (kind) {
        PrimitiveKind.STRING,
        StructureKind.CLASS -> ProtoWire.LEN
        PrimitiveKind.FLOAT -> ProtoWire.FIXED32
        PrimitiveKind.DOUBLE -> ProtoWire.FIXED64
        else -> ProtoWire.VARINT
      }

  override fun toString(): String = "$name (#$number)"
}

/** The fields of the message this descriptor describes, by `@ProtoNumber`. */
internal fun SerialDescriptor.protoFields(): List<ProtoField> =
  (0 until elementsCount).map { index ->
    val element = getElementDescriptor(index).unwrapInline()
    val repeated = element.kind == StructureKind.LIST
    ProtoField(
      name = "${serialName.removeSuffix("?")}.${getElementName(index)}",
      number = protoNumber(index),
      descriptor = if (repeated) element.getElementDescriptor(0).unwrapInline() else element,
      repeated = repeated,
      required = !isElementOptional(index),
    )
  }

/** The `@ProtoNumber`s of the enum this descriptor describes. */
internal fun SerialDescriptor.enumNumbers(): List<Int> =
  (0 until elementsCount).map { index -> protoNumber(index) }

private fun SerialDescriptor.protoNumber(index: Int): Int =
  getElementAnnotations(index).filterIsInstance<ProtoNumber>().singleOrNull()?.number ?: index

private fun SerialDescriptor.unwrapInline(): SerialDescriptor {
  var current = this
  while (current.isInline && current.elementsCount == 1) current = current.getElementDescriptor(0)
  return current
}

/**
 * Writes protobuf for the GTFS Realtime messages from their serial descriptors, so it covers every
 * field the data classes declare without knowing them.
 */
internal object DescriptorWriter {
  /** A message with only its required fields set, followed by [extra]. */
  fun requiredOnly(message: SerialDescriptor, vararg extra: ByteArray): ByteArray =
    ProtoWire.concat(
      *message
        .protoFields()
        .filter { it.required }
        .map { field(it, requiredValue(it)) }
        .toTypedArray(),
      *extra,
    )

  /** One occurrence of [field] with [value], the raw varint, fixed, or length-delimited payload. */
  fun field(field: ProtoField, value: ByteArray): ByteArray =
    when (field.wireType) {
      ProtoWire.LEN -> ProtoWire.messageField(field.number, value)
      else -> ProtoWire.concat(ProtoWire.tag(field.number, field.wireType), value)
    }

  /** Distinct values for [field] that between them differ from any default it could have. */
  fun sampleValues(field: ProtoField): List<ByteArray> =
    when (field.kind) {
      PrimitiveKind.STRING -> listOf("a", "b").map { it.encodeToByteArray() }
      PrimitiveKind.BOOLEAN -> listOf(0L, 1L).map { ProtoWire.varint(it) }
      PrimitiveKind.INT,
      PrimitiveKind.LONG -> listOf(1L, 2L).map { ProtoWire.varint(it) }
      PrimitiveKind.FLOAT -> listOf(1.5f, 2.5f).map { fixed32(it.toRawBits()) }
      PrimitiveKind.DOUBLE -> listOf(1.5, 2.5).map { fixed64(it.toRawBits()) }
      SerialKind.ENUM -> field.descriptor.enumNumbers().map { ProtoWire.varint(it.toLong()) }
      StructureKind.CLASS -> listOf(requiredOnly(field.descriptor))
      else -> error("Unsupported field kind for $field: ${field.kind}")
    }

  private fun requiredValue(field: ProtoField): ByteArray =
    when (field.kind) {
      StructureKind.CLASS -> requiredOnly(field.descriptor)
      else -> sampleValues(field).first()
    }

  fun fixed32(value: Int): ByteArray = ByteArray(4) { (value ushr (8 * it)).toByte() }

  fun fixed64(value: Long): ByteArray = ByteArray(8) { (value ushr (8 * it)).toByte() }
}

/**
 * Writes random feeds from the serial descriptors: every field may appear zero or more times in any
 * order, with edge-case values, unrecognized enum numbers, invalid UTF-8, unknown fields, groups,
 * and occasional mismatched wire types or missing required fields.
 */
internal class RandomFeedWriter(private val random: Random) {
  fun feedMessage(): ByteArray = message(FeedMessage.serializer().descriptor, depth = 0)

  private fun message(descriptor: SerialDescriptor, depth: Int): ByteArray {
    val parts = mutableListOf<ByteArray>()
    for (field in descriptor.protoFields()) {
      repeat(occurrences(field, depth)) { parts += occurrence(field, depth) }
    }
    if (random.nextInt(8) == 0) parts += noise()
    parts.shuffle(random)
    return ProtoWire.concat(*parts.toTypedArray())
  }

  private fun occurrences(field: ProtoField, depth: Int): Int =
    when {
      field.required -> if (random.nextInt(200) == 0) 0 else if (random.nextInt(8) == 0) 2 else 1
      field.kind == StructureKind.CLASS && random.nextInt(depth + 2) > 1 -> 0
      field.repeated -> if (depth == 0) random.nextInt(6) else random.nextInt(4)
      else -> listOf(0, 0, 1, 1, 1, 2).random(random)
    }

  private fun occurrence(field: ProtoField, depth: Int): ByteArray {
    if (random.nextInt(100) == 0) {
      // A known field with the wrong wire type.
      val wireTypes = listOf(ProtoWire.VARINT, ProtoWire.FIXED64, ProtoWire.LEN, ProtoWire.FIXED32)
      return when (wireTypes.filter { it != field.wireType }.random(random)) {
        ProtoWire.VARINT -> ProtoWire.varintField(field.number, random.nextLong(0, 300))
        ProtoWire.FIXED64 -> ProtoWire.fixed64Field(field.number, random.nextLong())
        ProtoWire.LEN -> ProtoWire.stringField(field.number, "mismatched")
        else -> ProtoWire.fixed32Field(field.number, random.nextInt())
      }
    }
    return DescriptorWriter.field(field, value(field, depth))
  }

  private fun value(field: ProtoField, depth: Int): ByteArray =
    when (field.kind) {
      PrimitiveKind.STRING -> string()
      PrimitiveKind.BOOLEAN ->
        ProtoWire.varint(
          if (random.nextInt(40) == 0) listOf(2L, 1L shl 32, (1L shl 32) + 1).random(random)
          else random.nextLong(0, 2),
        )
      PrimitiveKind.INT -> ProtoWire.varint(int())
      PrimitiveKind.LONG -> ProtoWire.varint(long())
      PrimitiveKind.FLOAT -> DescriptorWriter.fixed32(float().toRawBits())
      PrimitiveKind.DOUBLE -> DescriptorWriter.fixed64(float().toDouble().toRawBits())
      SerialKind.ENUM -> ProtoWire.varint(enumNumber(field.descriptor))
      StructureKind.CLASS -> message(field.descriptor, depth + 1)
      else -> error("Unsupported field kind for $field: ${field.kind}")
    }

  private fun string(): ByteArray {
    val text = buildString {
      repeat(random.nextInt(8)) {
        append(listOf("a", "Z", "7", " ", "é", "日", "🚌").random(random))
      }
    }
    val bytes = text.encodeToByteArray()
    if (random.nextInt(20) != 0) return bytes
    // Invalid UTF-8: a stray continuation byte, an invalid byte, or a truncated sequence.
    val invalid =
      listOf(
        byteArrayOf(0x80.toByte()),
        byteArrayOf(0xFF.toByte()),
        byteArrayOf(0xE6.toByte(), 0x97.toByte()),
      )
    return ProtoWire.concat(bytes, invalid.random(random))
  }

  private fun int(): Long =
    when (random.nextInt(6)) {
      0 -> random.nextLong(-1000, 1000)
      1 -> random.nextInt().toLong()
      2 -> listOf(0L, -1L, Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).random(random)
      3 -> random.nextLong()
      4 -> (1L shl 32) + random.nextLong(0, 10)
      else -> random.nextLong(0, 100)
    }

  private fun long(): Long =
    when (random.nextInt(4)) {
      0 -> 1_700_000_000L + random.nextLong(0, 100_000_000)
      1 -> random.nextLong()
      2 -> listOf(0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE).random(random)
      else -> random.nextLong(-1000, 1000)
    }

  private fun float(): Float =
    when (random.nextInt(4)) {
      0 -> Float.fromBits(random.nextInt())
      1 -> listOf(0f, -0f, Float.NaN, Float.POSITIVE_INFINITY).random(random)
      else -> random.nextFloat() * 360 - 180
    }

  private fun enumNumber(descriptor: SerialDescriptor): Long =
    if (random.nextInt(10) == 0) listOf(-1L, 99L, 1L shl 40, random.nextLong()).random(random)
    else descriptor.enumNumbers().random(random).toLong()

  /** An unknown field, or a group, which proto2 parsers skip. */
  private fun noise(): ByteArray {
    val number = 1000 + random.nextInt(100)
    return when (random.nextInt(5)) {
      0 -> ProtoWire.varintField(number, random.nextLong())
      1 -> ProtoWire.fixed32Field(number, random.nextInt())
      2 -> ProtoWire.fixed64Field(number, random.nextLong())
      3 -> ProtoWire.stringField(number, "unknown")
      else ->
        ProtoWire.concat(
          ProtoWire.tag(number, ProtoWire.START_GROUP),
          ProtoWire.varintField(1, 4),
          ProtoWire.tag(2, ProtoWire.START_GROUP),
          ProtoWire.stringField(1, "nested"),
          ProtoWire.tag(2, ProtoWire.END_GROUP),
          ProtoWire.tag(number, ProtoWire.END_GROUP),
        )
    }
  }
}

/** Damages [bytes] the way corrupted or truncated feeds are, splicing from [donor] at times. */
internal fun mutate(bytes: ByteArray, random: Random, donor: ByteArray = bytes): ByteArray {
  if (bytes.isEmpty()) return byteArrayOf(random.nextInt().toByte())
  val at = random.nextInt(bytes.size)
  fun insert(chunk: ByteArray, into: Int = at) =
    ProtoWire.concat(bytes.copyOfRange(0, into), chunk, bytes.copyOfRange(into, bytes.size))
  return when (random.nextInt(7)) {
    0 -> bytes.copyOf().also { it[at] = (it[at].toInt() xor (1 shl random.nextInt(8))).toByte() }
    1 -> bytes.copyOf().also { it[at] = random.nextInt().toByte() }
    2 -> bytes.copyOf(at)
    3 ->
      ProtoWire.concat(
        bytes.copyOfRange(0, at),
        bytes.copyOfRange(minOf(bytes.size, at + 1 + random.nextInt(16)), bytes.size),
      )
    4 -> insert(random.nextBytes(1 + random.nextInt(4)))
    5 ->
      insert(
        bytes.copyOfRange(at, minOf(bytes.size, at + 1 + random.nextInt(32))),
        into = random.nextInt(bytes.size),
      )
    else -> {
      val from = random.nextInt(donor.size.coerceAtLeast(1))
      insert(donor.copyOfRange(from, minOf(donor.size, from + 1 + random.nextInt(64))))
    }
  }
}
