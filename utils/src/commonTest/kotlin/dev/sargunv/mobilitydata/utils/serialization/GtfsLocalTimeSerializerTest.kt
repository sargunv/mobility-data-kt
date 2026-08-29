package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.GtfsLocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GtfsLocalTimeSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = GtfsLocalTimeSerializer::class) val time: GtfsLocalTime
  )

  private val json = Json

  @Test
  fun serializePadsComponents() {
    val jsonElement =
      json.encodeToJsonElement(TestData.serializer(), TestData(GtfsLocalTime(7, 5, 9)))
    assertEquals("07:05:09", jsonElement.jsonObject["time"]!!.jsonPrimitive.content)
  }

  @Test
  fun deserializeAcceptsUnpaddedHours() {
    val result = json.decodeFromString(TestData.serializer(), """{"time":"7:00:00"}""")
    assertEquals(GtfsLocalTime(7, 0, 0), result.time)
  }

  @Test
  fun deserializeAccepts240000() {
    val result = json.decodeFromString(TestData.serializer(), """{"time":"24:00:00"}""")
    assertEquals(GtfsLocalTime(24, 0, 0), result.time)
  }

  @Test
  fun deserializeRejects250000() {
    assertFailsWith<IllegalArgumentException> {
      json.decodeFromString(TestData.serializer(), """{"time":"25:00:00"}""")
    }
  }

  @Test
  fun deserializeRejectsInvalidFormat() {
    assertFailsWith<IllegalArgumentException> {
      json.decodeFromString(TestData.serializer(), """{"time":"07:00"}""")
    }
  }

  @Test
  fun roundTrip() {
    val original = TestData(GtfsLocalTime(23, 59, 59))
    val encoded = json.encodeToString(TestData.serializer(), original)
    assertEquals(original, json.decodeFromString(TestData.serializer(), encoded))
  }
}
