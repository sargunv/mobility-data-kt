@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf

internal fun assertFeedRoundTrips(feedMessage: FeedMessage) {
  val encoded = GtfsRealtimeProto.encodeFeedMessage(feedMessage)
  val decoded = decodeMatchingOracle(encoded)

  assertEquals(feedMessage, decoded)
}

private val oracleProtoBuf = ProtoBuf { encodeDefaults = false }

/**
 * The decode path before [GtfsRealtimeDecoder]: strip what kotlinx would reject, then decode with
 * kotlinx.
 */
internal fun oracleDecode(bytes: ByteArray): FeedMessage =
  oracleProtoBuf.decodeFromByteArray(
    FeedMessage.serializer(),
    Proto2UnknownFieldStripper.strip(bytes),
  )

/**
 * Decodes [bytes] with [GtfsRealtimeProto.decodeFeedMessage], asserting it agrees with
 * [oracleDecode], and returns the result or rethrows its exception.
 */
internal fun decodeMatchingOracle(bytes: ByteArray, label: String? = null): FeedMessage {
  val actual = runCatching { GtfsRealtimeProto.decodeFeedMessage(bytes) }
  assertMatchesOracle(bytes, actual, label)
  return actual.getOrThrow()
}

/**
 * Asserts that [actual] is what [oracleDecode] gives for [bytes]: an equal feed, or a
 * [SerializationException] where the oracle throws one (a [MissingFieldException] where the
 * oracle's is one). Returns [actual].
 */
internal fun assertMatchesOracle(
  bytes: ByteArray,
  actual: Result<FeedMessage> = runCatching { GtfsRealtimeProto.decodeFeedMessage(bytes) },
  label: String? = null,
): Result<FeedMessage> {
  val expected = runCatching { oracleDecode(bytes) }
  val context = buildString {
    if (label != null) append("$label: ")
    append("${bytes.size} bytes")
    if (bytes.size <= 512) append(" ${bytes.toHexString()}")
  }
  val expectedError = expected.exceptionOrNull()
  val actualError = actual.exceptionOrNull()
  when {
    expectedError == null && actualError == null ->
      assertEquals(expected.getOrThrow(), actual.getOrThrow(), context)
    expectedError == null -> fail("Oracle decoded but the decoder threw ($context)", actualError)
    expectedError !is SerializationException ->
      fail("Oracle threw a non-SerializationException ($context)", expectedError)
    actualError == null -> fail("Oracle threw but the decoder didn't ($context)", expectedError)
    expectedError is MissingFieldException -> assertIs<MissingFieldException>(actualError, context)
    else -> assertIs<SerializationException>(actualError, context)
  }
  return actual
}

/** Minimal protobuf writer for constructing nonconforming and malformed payloads in tests. */
internal object ProtoWire {
  const val VARINT = 0
  const val FIXED64 = 1
  const val LEN = 2
  const val START_GROUP = 3
  const val END_GROUP = 4
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
