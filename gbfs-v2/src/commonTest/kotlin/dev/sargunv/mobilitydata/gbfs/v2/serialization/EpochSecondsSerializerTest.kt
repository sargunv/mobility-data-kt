package dev.sargunv.mobilitydata.gbfs.v2.serialization

import dev.sargunv.mobilitydata.gbfs.v2.GbfsJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

@OptIn(ExperimentalTime::class)
class EpochSecondsSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = EpochSecondsSerializer::class) val timestamp: Instant
  )

  @Test
  fun testSerializeInstant() {
    val instant = Instant.fromEpochSeconds(1640887163)
    val testData = TestData(instant)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val timestamp = json.jsonObject["timestamp"]!!.jsonPrimitive

    assertEquals(1640887163L, timestamp.long)
  }

  @Test
  fun testDeserializeInstant() {
    val jsonString = """{"timestamp":1640887163}"""

    val result = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(Instant.fromEpochSeconds(1640887163), result.timestamp)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(Instant.fromEpochSeconds(1609459200)) // 2021-01-01 00:00:00 UTC

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), original)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testNegativeEpochSeconds() {
    val instant = Instant.fromEpochSeconds(-86400) // 1969-12-31
    val testData = TestData(instant)

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), testData)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }
}
