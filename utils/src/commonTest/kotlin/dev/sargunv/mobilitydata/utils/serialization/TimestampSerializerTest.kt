package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.UtcOffset
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalTime::class)
class TimestampSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = TimestampSerializer::class) val timestamp: Timestamp
  )

  private val json = Json

  @Test
  fun testSerializeTimestamp() {
    val instant = Instant.parse("2021-12-31T12:34:23Z")
    val offset = UtcOffset.ZERO
    val timestamp = Timestamp(instant, offset)
    val testData = TestData(timestamp)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val timestampValue = jsonElement.jsonObject["timestamp"]!!.jsonPrimitive

    assertEquals("2021-12-31T12:34:23Z", timestampValue.content)
  }

  @Test
  fun testDeserializeTimestamp() {
    val jsonString = """{"timestamp":"2021-12-31T12:34:23Z"}"""

    val result = json.decodeFromString(TestData.serializer(), jsonString)

    val expectedInstant = Instant.parse("2021-12-31T12:34:23Z")
    val expectedOffset = UtcOffset.ZERO
    assertEquals(Timestamp(expectedInstant, expectedOffset), result.timestamp)
  }

  @Test
  fun testRoundTrip() {
    val instant = Instant.parse("2021-01-01T00:00:00Z")
    val offset = UtcOffset.ZERO
    val original = TestData(Timestamp(instant, offset))

    val jsonString = json.encodeToString(TestData.serializer(), original)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testTimestampWithOffset() {
    val instant = Instant.parse("2021-12-31T17:34:23Z")
    val offset = UtcOffset(hours = -5)
    val timestamp = Timestamp(instant, offset)
    val testData = TestData(timestamp)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val timestampValue = jsonElement.jsonObject["timestamp"]!!.jsonPrimitive

    assertEquals("2021-12-31T12:34:23-05:00", timestampValue.content)

    val jsonString = json.encodeToString(TestData.serializer(), testData)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }

  @Test
  fun testPositiveOffset() {
    val instant = Instant.parse("2021-12-31T10:34:23Z")
    val offset = UtcOffset(hours = 2)
    val timestamp = Timestamp(instant, offset)
    val testData = TestData(timestamp)

    val jsonString = json.encodeToString(TestData.serializer(), testData)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }
}
