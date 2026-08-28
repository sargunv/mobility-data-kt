package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.toTimeZoneOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
      agencyTimezone = "America/Los_Angeles",
    )
  )

class AgencyTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Agency>(csvContent)
    assertEquals(expected, decoded)
  }

  @Test
  fun unknownTimezoneIdIsPreserved() {
    val csv = // language=CSV
      """
      agency_id,agency_name,agency_url,agency_timezone
      DTA,Demo Transit Authority,https://google.com,Not/AZone
      """
        .trimIndent()

    val decoded = GtfsCsv.decodeFromString<Agency>(csv).single()
    assertEquals("Not/AZone", decoded.agencyTimezone)
    assertNull(decoded.agencyTimezone.toTimeZoneOrNull())
  }
}
