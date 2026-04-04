package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.TimeZone

private val csvContent = // language=CSV
  """
  agency_id,agency_name,agency_url,agency_timezone
  DTA,Demo Transit Authority,https://google.com,America/Los_Angeles
  """
    .trimIndent()

private val expected =
  listOf(
    Agency(
      agencyId = "DTA",
      agencyName = "Demo Transit Authority",
      agencyUrl = "https://google.com",
      agencyTimezone = TimeZone.of("America/Los_Angeles"),
    )
  )

class AgencyTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Agency>(csvContent)
    assertEquals(expected, decoded)
  }
}
