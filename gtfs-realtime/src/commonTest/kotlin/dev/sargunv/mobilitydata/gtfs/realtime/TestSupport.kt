package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.assertEquals

internal fun assertFeedRoundTrips(feedMessage: FeedMessage) {
  val encoded = GtfsRealtimeProto.encodeFeedMessage(feedMessage)
  val decoded = GtfsRealtimeProto.decodeFeedMessage(encoded)

  assertEquals(feedMessage, decoded)
}

/** Minimal protobuf writer for constructing nonconforming and malformed payloads in tests. */
internal object ProtoWire {
  const val VARINT = 0
  const val FIXED64 = 1
  const val LEN = 2
  const val FIXED32 = 5

  fun concat(vararg parts: ByteArray): ByteArray {
    val out = ByteArray(parts.sumOf { it.size })
    var offset = 0
    for (part in parts) {
      part.copyInto(out, offset)
      offset += part.size
    }
    return out
  }

  fun varintField(field: Int, value: Long): ByteArray = concat(tag(field, VARINT), varint(value))

  fun stringField(field: Int, value: String): ByteArray =
    bytesField(field, value.encodeToByteArray())

  fun messageField(field: Int, value: ByteArray): ByteArray = bytesField(field, value)

  fun packedVarintField(field: Int, vararg values: Long): ByteArray =
    bytesField(field, concat(*values.map { varint(it) }.toTypedArray()))

  fun fixed32Field(field: Int, value: Int): ByteArray =
    concat(tag(field, FIXED32), littleEndian(value.toLong(), 4))

  fun fixed64Field(field: Int, value: Long): ByteArray =
    concat(tag(field, FIXED64), littleEndian(value, 8))

  private fun bytesField(field: Int, value: ByteArray): ByteArray =
    concat(tag(field, LEN), varint(value.size.toLong()), value)

  fun tag(field: Int, wireType: Int): ByteArray = varint(((field shl 3) or wireType).toLong())

  private fun littleEndian(value: Long, size: Int): ByteArray =
    ByteArray(size) { (value ushr (8 * it)).toByte() }

  fun varint(value: Long): ByteArray {
    var remaining = value
    val bytes = mutableListOf<Byte>()
    while (remaining ushr 7 != 0L) {
      bytes.add(((remaining and 0x7F) or 0x80).toByte())
      remaining = remaining ushr 7
    }
    bytes.add((remaining and 0x7F).toByte())
    return bytes.toByteArray()
  }
}

internal fun feedHeaderBytes(version: String = "2.0"): ByteArray = ProtoWire.stringField(1, version)

internal fun feedMessageBytes(vararg entities: ByteArray): ByteArray =
  ProtoWire.concat(
    ProtoWire.messageField(1, feedHeaderBytes()),
    *entities.map { ProtoWire.messageField(2, it) }.toTypedArray(),
  )
