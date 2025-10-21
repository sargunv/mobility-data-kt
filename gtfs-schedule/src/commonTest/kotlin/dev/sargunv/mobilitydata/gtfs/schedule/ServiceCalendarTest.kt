package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

private val csvContent = // language=CSV
  """
service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
WD,1,1,1,1,1,0,0,20230101,20231231
"""
    .trimIndent() + "\n"

private val expectedServiceCalendar =
  ServiceCalendar(
    serviceId = "WD",
    monday = true,
    tuesday = true,
    wednesday = true,
    thursday = true,
    friday = true,
    saturday = false,
    sunday = false,
    startDate = LocalDate(2023, 1, 1),
    endDate = LocalDate(2023, 12, 31),
  )

class ServiceCalendarTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<ServiceCalendar>(csvContent)
    assertEquals(listOf(expectedServiceCalendar), decoded)
  }
}
