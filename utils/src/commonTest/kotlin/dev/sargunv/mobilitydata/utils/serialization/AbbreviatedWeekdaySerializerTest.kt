package dev.sargunv.mobilitydata.utils.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AbbreviatedWeekdaySerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = AbbreviatedWeekdaySerializer::class) val day: DayOfWeek
  )

  private val json = Json

  @Test
  fun testSerializeMonday() {
    val testData = TestData(DayOfWeek.MONDAY)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val dayValue = jsonElement.jsonObject["day"]!!.jsonPrimitive

    assertEquals("mon", dayValue.content)
  }

  @Test
  fun testSerializeSaturday() {
    val testData = TestData(DayOfWeek.SATURDAY)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val dayValue = jsonElement.jsonObject["day"]!!.jsonPrimitive

    assertEquals("sat", dayValue.content)
  }

  @Test
  fun testDeserializeWednesday() {
    val jsonString = """{"day":"wed"}"""

    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(DayOfWeek.WEDNESDAY, result.day)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(DayOfWeek.FRIDAY)

    val jsonString = json.encodeToString(TestData.serializer(), original)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

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
      val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
      val dayValue = jsonElement.jsonObject["day"]!!.jsonPrimitive

      assertEquals(abbreviation, dayValue.content, "$day should serialize to '$abbreviation'")

      val jsonString = """{"day":"$abbreviation"}"""
      val result = json.decodeFromString(TestData.serializer(), jsonString)
      assertEquals(day, result.day, "'$abbreviation' should deserialize to $day")
    }
  }

  @Test
  fun testInvalidAbbreviation() {
    val jsonString = """{"day":"invalid"}"""

    assertFailsWith<IllegalArgumentException> {
      json.decodeFromString(TestData.serializer(), jsonString)
    }
  }

  @Test
  fun testFullDayNameNotAccepted() {
    val jsonString = """{"day":"monday"}"""

    assertFailsWith<IllegalArgumentException> {
      json.decodeFromString(TestData.serializer(), jsonString)
    }
  }
}
