package dev.sargunv.mobilitydata.gbfs.v2.serialization

import dev.sargunv.mobilitydata.gbfs.v2.GbfsJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Month
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MonthNumberSerializerTest {
  @Serializable
  private data class TestData(@Serializable(with = MonthNumberSerializer::class) val month: Month)

  @Test
  fun testSerializeJanuary() {
    val testData = TestData(Month.JANUARY)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val monthValue = json.jsonObject["month"]!!.jsonPrimitive

    assertEquals(1, monthValue.int)
  }

  @Test
  fun testSerializeDecember() {
    val testData = TestData(Month.DECEMBER)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val monthValue = json.jsonObject["month"]!!.jsonPrimitive

    assertEquals(12, monthValue.int)
  }

  @Test
  fun testDeserializeApril() {
    val jsonString = """{"month":4}"""

    val result = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(Month.APRIL, result.month)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(Month.JULY)

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), original)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }
}
