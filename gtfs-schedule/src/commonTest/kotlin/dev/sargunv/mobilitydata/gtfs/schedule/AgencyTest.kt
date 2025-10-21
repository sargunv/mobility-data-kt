package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.TimeZone

private val csvContent = // language=CSV
  """
agency_id,agency_name,agency_url,agency_timezone,agency_lang,agency_phone,agency_fare_url,agency_email
DTA,Demo Transit Authority,http://google.com,America/Los_Angeles,en,555-1234,http://google.com/fares,info@demo.com
"""
    .trimIndent() + "\n"

private val expectedAgency =
  Agency(
    agencyId = "DTA",
    agencyName = "Demo Transit Authority",
    agencyUrl = "http://google.com",
    agencyTimezone = TimeZone.of("America/Los_Angeles"),
    agencyLang = "en",
    agencyPhone = "555-1234",
    agencyFareUrl = "http://google.com/fares",
    agencyEmail = "info@demo.com",
  )

class AgencyTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Agency>(csvContent)
    assertEquals(listOf(expectedAgency), decoded)
  }
}
