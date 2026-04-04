package dev.sargunv.mobilitydata.utils.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

@OptIn(ExperimentalTime::class)
class EpochSecondsSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = EpochSecondsSerializer::class) val timestamp: Instant
  )

  private val json = Json

  @Test
  fun testSerializeInstant() {
    val instant = Instant.fromEpochSeconds(1640887163)
    val testData = TestData(instant)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val timestamp = jsonElement.jsonObject["timestamp"]!!.jsonPrimitive

    assertEquals(1640887163L, timestamp.long)
  }

  @Test
  fun testDeserializeInstant() {
    val jsonString = """{"timestamp":1640887163}"""

    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(Instant.fromEpochSeconds(1640887163), result.timestamp)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(Instant.fromEpochSeconds(1609459200)) // 2021-01-01 00:00:00 UTC

    val jsonString = json.encodeToString(TestData.serializer(), original)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testNegativeEpochSeconds() {
    val instant = Instant.fromEpochSeconds(-86400) // 1969-12-31
    val testData = TestData(instant)

    val jsonString = json.encodeToString(TestData.serializer(), testData)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }
}
