package dev.sargunv.mobilitydata.utils.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

class IsoBasicLocalDateSerializerTest {
  private val format = LocalDate.Formats.ISO_BASIC

  private fun outcome(block: () -> LocalDate): String =
    try {
      "ok: ${block()}"
    } catch (e: IllegalArgumentException) {
      "error: ${e::class.simpleName}: ${e.message}"
    }

  private fun assertMatchesFormat(value: String) {
    assertEquals(
      outcome { format.parse(value) },
      outcome { parseIsoBasicDate(value, format) },
      "input '$value'",
    )
  }

  @Test
  fun matchesFormatForEveryMonthAndDay() {
    for (year in listOf("0000", "0001", "1900", "2000", "2023", "2024", "9999")) {
      for (month in 0..13) {
        for (day in 0..32) {
          val mm = month.toString().padStart(2, '0')
          val dd = day.toString().padStart(2, '0')
          assertMatchesFormat("$year$mm$dd")
        }
      }
    }
  }

  @Test
  fun matchesFormatForOtherShapes() {
    listOf(
        "",
        "2024011",
        "202401151",
        "+20240115",
        "-20240115",
        "+120240115",
        "2024-01-15",
        "2024011a",
        " 20240115",
        "20240115 ",
        "٢٠٢٤٠١١٥",
      )
      .forEach(::assertMatchesFormat)
  }

  @Test
  fun decodesThroughSerializer() {
    assertEquals(
      LocalDate(2021, 11, 9),
      Json.decodeFromJsonElement(IsoBasicLocalDateSerializer, JsonPrimitive("20211109")),
    )
  }
}
