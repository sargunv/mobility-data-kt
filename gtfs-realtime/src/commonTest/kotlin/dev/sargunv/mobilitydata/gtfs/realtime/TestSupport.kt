package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.assertEquals

internal fun assertFeedRoundTrips(feedMessage: FeedMessage) {
  val encoded = GtfsRealtimeProto.encodeFeedMessage(feedMessage)
  val decoded = GtfsRealtimeProto.decodeFeedMessage(encoded)

  assertEquals(feedMessage, decoded)
}

/** Minimal protobuf writer for constructing unknown-enum payloads in tests. */
internal object ProtoWire {
  private const val VARINT = 0
  private const val LEN = 2

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

  private fun bytesField(field: Int, value: ByteArray): ByteArray =
    concat(tag(field, LEN), varint(value.size.toLong()), value)

  private fun tag(field: Int, wireType: Int): ByteArray =
    varint(((field shl 3) or wireType).toLong())

  private fun varint(value: Long): ByteArray {
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
