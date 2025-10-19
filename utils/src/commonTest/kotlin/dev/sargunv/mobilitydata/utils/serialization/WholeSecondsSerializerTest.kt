package dev.sargunv.mobilitydata.utils.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class WholeSecondsSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = WholeSecondsSerializer::class) val duration: Duration
  )

  private val json = Json

  @Test
  fun testSerializeDuration() {
    val duration = 86400.seconds // 1 day
    val testData = TestData(duration)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val durationValue = jsonElement.jsonObject["duration"]!!.jsonPrimitive

    assertEquals(86400L, durationValue.long)
  }

  @Test
  fun testDeserializeDuration() {
    val jsonString = """{"duration":86400}"""

    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(86400.seconds, result.duration)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(3600.seconds) // 1 hour

    val jsonString = json.encodeToString(TestData.serializer(), original)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testZeroDuration() {
    val testData = TestData(0.seconds)

    val jsonString = json.encodeToString(TestData.serializer(), testData)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }

  @Test
  fun testFractionalSecondsRoundDown() {
    val duration = 90.5.seconds // 90.5 seconds
    val testData = TestData(duration)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val durationValue = jsonElement.jsonObject["duration"]!!.jsonPrimitive

    // Should be serialized as whole seconds (rounded down)
    assertEquals(90L, durationValue.long)
  }
}
