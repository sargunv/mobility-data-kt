package dev.sargunv.mobilitydata.utils.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class WholeMinutesSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = WholeMinutesSerializer::class) val duration: Duration
  )

  private val json = Json

  @Test
  fun testSerializeDuration() {
    val duration = 1440.minutes // 1 day
    val testData = TestData(duration)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val durationValue = jsonElement.jsonObject["duration"]!!.jsonPrimitive

    assertEquals(1440L, durationValue.long)
  }

  @Test
  fun testDeserializeDuration() {
    val jsonString = """{"duration":1440}"""

    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(1440.minutes, result.duration)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(60.minutes) // 1 hour

    val jsonString = json.encodeToString(TestData.serializer(), original)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testZeroDuration() {
    val testData = TestData(0.minutes)

    val jsonString = json.encodeToString(TestData.serializer(), testData)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }

  @Test
  fun testFractionalMinutesRoundDown() {
    val duration = 90.5.minutes // 90.5 minutes
    val testData = TestData(duration)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val durationValue = jsonElement.jsonObject["duration"]!!.jsonPrimitive

    // Should be serialized as whole minutes (rounded down)
    assertEquals(90L, durationValue.long)
  }
}
