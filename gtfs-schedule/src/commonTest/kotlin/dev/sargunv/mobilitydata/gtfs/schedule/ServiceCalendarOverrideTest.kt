package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

private val csvContent = // language=CSV
  """
  service_id,date,exception_type
  FULLW,20070604,2
  """
    .trimIndent()

private val expected =
  listOf(
    ServiceCalendarOverride(
      serviceId = "FULLW",
      date = LocalDate(2007, 6, 4),
      overrideType = OverrideType.ServiceRemoved,
    )
  )

class ServiceCalendarOverrideTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<ServiceCalendarOverride>(csvContent)
    assertEquals(expected, decoded)
  }
}
