package dev.sargunv.mobilitydata.utils.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SecondsSerializerTest {
  @Serializable
  private data class TestData(@Serializable(with = SecondsSerializer::class) val duration: Duration)

  private val json = Json

  @Test
  fun testSerializeDuration() {
    val testData = TestData(86400.seconds)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val durationValue = jsonElement.jsonObject["duration"]!!.jsonPrimitive

    assertEquals(86400.0, durationValue.double)
  }

  @Test
  fun testDeserializeDuration() {
    val result = json.decodeFromString(TestData.serializer(), """{"duration":86400}""")

    assertEquals(86400.seconds, result.duration)
  }

  @Test
  fun testFractionalSecondsRoundTrip() {
    val original = TestData(90.5.seconds)

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
}
