package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

private val csvContent = // language=CSV
  """
  service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
  FULLW,1,1,1,1,1,1,1,20070101,20101231
  WE,0,0,0,0,0,1,1,20070101,20101231
  """
    .trimIndent()

private val expected =
  listOf(
    ServiceCalendar(
      serviceId = "FULLW",
      monday = true,
      tuesday = true,
      wednesday = true,
      thursday = true,
      friday = true,
      saturday = true,
      sunday = true,
      startDate = LocalDate(2007, 1, 1),
      endDate = LocalDate(2010, 12, 31),
    ),
    ServiceCalendar(
      serviceId = "WE",
      monday = false,
      tuesday = false,
      wednesday = false,
      thursday = false,
      friday = false,
      saturday = true,
      sunday = true,
      startDate = LocalDate(2007, 1, 1),
      endDate = LocalDate(2010, 12, 31),
    ),
  )

class ServiceCalendarTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<ServiceCalendar>(csvContent)
    assertEquals(expected, decoded)
  }
}
