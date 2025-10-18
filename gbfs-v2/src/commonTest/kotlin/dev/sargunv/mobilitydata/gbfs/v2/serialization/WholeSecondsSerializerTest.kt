package dev.sargunv.mobilitydata.gbfs.v2.serialization

import dev.sargunv.mobilitydata.gbfs.v2.model.GbfsJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class WholeSecondsSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = WholeSecondsSerializer::class) val duration: Duration
  )

  @Test
  fun testSerializeDuration() {
    val duration = 86400.seconds // 1 day
    val testData = TestData(duration)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val durationValue = json.jsonObject["duration"]!!.jsonPrimitive

    assertEquals(86400L, durationValue.long)
  }

  @Test
  fun testDeserializeDuration() {
    val jsonString = """{"duration":86400}"""

    val result = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(86400.seconds, result.duration)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(3600.seconds) // 1 hour

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), original)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testZeroDuration() {
    val testData = TestData(0.seconds)

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), testData)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }

  @Test
  fun testFractionalSecondsRoundDown() {
    val duration = 90.5.seconds // 90.5 seconds
    val testData = TestData(duration)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val durationValue = json.jsonObject["duration"]!!.jsonPrimitive

    // Should be serialized as whole seconds (rounded down)
    assertEquals(90L, durationValue.long)
  }
}
