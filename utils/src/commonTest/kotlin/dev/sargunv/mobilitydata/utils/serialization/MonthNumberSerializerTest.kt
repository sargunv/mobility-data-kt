package dev.sargunv.mobilitydata.utils.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Month
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MonthNumberSerializerTest {
  @Serializable
  private data class TestData(@Serializable(with = MonthNumberSerializer::class) val month: Month)

  private val json = Json

  @Test
  fun testSerializeJanuary() {
    val testData = TestData(Month.JANUARY)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val monthValue = jsonElement.jsonObject["month"]!!.jsonPrimitive

    assertEquals(1, monthValue.int)
  }

  @Test
  fun testSerializeDecember() {
    val testData = TestData(Month.DECEMBER)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val monthValue = jsonElement.jsonObject["month"]!!.jsonPrimitive

    assertEquals(12, monthValue.int)
  }

  @Test
  fun testDeserializeApril() {
    val jsonString = """{"month":4}"""

    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(Month.APRIL, result.month)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(Month.JULY)

    val jsonString = json.encodeToString(TestData.serializer(), original)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }
}
