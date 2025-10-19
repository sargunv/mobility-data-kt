package dev.sargunv.mobilitydata.gbfs.v2.serialization

import dev.sargunv.mobilitydata.gbfs.v2.GbfsJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AbbreviatedWeekdaySerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = AbbreviatedWeekdaySerializer::class) val day: DayOfWeek
  )

  @Test
  fun testSerializeMonday() {
    val testData = TestData(DayOfWeek.MONDAY)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val dayValue = json.jsonObject["day"]!!.jsonPrimitive

    assertEquals("mon", dayValue.content)
  }

  @Test
  fun testSerializeSaturday() {
    val testData = TestData(DayOfWeek.SATURDAY)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val dayValue = json.jsonObject["day"]!!.jsonPrimitive

    assertEquals("sat", dayValue.content)
  }

  @Test
  fun testDeserializeWednesday() {
    val jsonString = """{"day":"wed"}"""

    val result = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(DayOfWeek.WEDNESDAY, result.day)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(DayOfWeek.FRIDAY)

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), original)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testAllDaysOfWeek() {
    val days =
      listOf(
        DayOfWeek.MONDAY to "mon",
        DayOfWeek.TUESDAY to "tue",
        DayOfWeek.WEDNESDAY to "wed",
        DayOfWeek.THURSDAY to "thu",
        DayOfWeek.FRIDAY to "fri",
        DayOfWeek.SATURDAY to "sat",
        DayOfWeek.SUNDAY to "sun",
      )

    for ((day, abbreviation) in days) {
      val testData = TestData(day)
      val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
      val dayValue = json.jsonObject["day"]!!.jsonPrimitive

      assertEquals(abbreviation, dayValue.content, "$day should serialize to '$abbreviation'")

      val jsonString = """{"day":"$abbreviation"}"""
      val result = GbfsJson.decodeFromString(TestData.serializer(), jsonString)
      assertEquals(day, result.day, "'$abbreviation' should deserialize to $day")
    }
  }

  @Test
  fun testInvalidAbbreviation() {
    val jsonString = """{"day":"invalid"}"""

    assertFailsWith<IllegalArgumentException> {
      GbfsJson.decodeFromString(TestData.serializer(), jsonString)
    }
  }

  @Test
  fun testFullDayNameNotAccepted() {
    val jsonString = """{"day":"monday"}"""

    assertFailsWith<IllegalArgumentException> {
      GbfsJson.decodeFromString(TestData.serializer(), jsonString)
    }
  }
}
