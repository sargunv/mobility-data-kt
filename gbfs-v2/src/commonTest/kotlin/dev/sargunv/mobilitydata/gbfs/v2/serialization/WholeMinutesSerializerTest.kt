package dev.sargunv.mobilitydata.gbfs.v2.serialization

import dev.sargunv.mobilitydata.gbfs.v2.model.GbfsJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class WholeMinutesSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = WholeMinutesSerializer::class) val duration: Duration
  )

  @Test
  fun testSerializeDuration() {
    val duration = 1440.minutes // 1 day
    val testData = TestData(duration)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val durationValue = json.jsonObject["duration"]!!.jsonPrimitive

    assertEquals(1440L, durationValue.long)
  }

  @Test
  fun testDeserializeDuration() {
    val jsonString = """{"duration":1440}"""

    val result = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(1440.minutes, result.duration)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(60.minutes) // 1 hour

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), original)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testZeroDuration() {
    val testData = TestData(0.minutes)

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), testData)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }

  @Test
  fun testFractionalMinutesRoundDown() {
    val duration = 90.5.minutes // 90.5 minutes
    val testData = TestData(duration)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val durationValue = json.jsonObject["duration"]!!.jsonPrimitive

    // Should be serialized as whole minutes (rounded down)
    assertEquals(90L, durationValue.long)
  }
}
