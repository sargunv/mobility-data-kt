package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

private val csvContent = // language=CSV
  """
service_id,date,exception_type
WD,20230704,2
"""
    .trimIndent() + "\n"

private val expectedCalendarDate =
  CalendarDate(
    serviceId = "WD",
    date = LocalDate(2023, 7, 4),
    exceptionType = ExceptionType.SERVICE_REMOVED,
  )

class CalendarDateTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<CalendarDate>(csvContent)
    assertEquals(listOf(expectedCalendarDate), decoded)
  }
}
