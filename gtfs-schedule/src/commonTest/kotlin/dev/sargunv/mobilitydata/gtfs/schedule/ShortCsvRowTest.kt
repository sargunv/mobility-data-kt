package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlinx.io.Buffer
import kotlinx.io.writeString

class ShortCsvRowTest {
  @Test
  fun decodeAcceptsOmittedTrailingOptionalFields() {
    val csv = // language=CSV
      """
      agency_id,agency_name,agency_url,agency_timezone,agency_lang,agency_phone,agency_fare_url,agency_email,cemv_support
      DTA,Demo Transit Authority,https://google.com,America/Los_Angeles
      """
        .trimIndent()

    val decoded = GtfsCsv.decodeFromString<Agency>(csv).single()
    assertEquals("DTA", decoded.agencyId)
    assertEquals("Demo Transit Authority", decoded.agencyName)
    assertEquals("https://google.com", decoded.agencyUrl)
    assertEquals("America/Los_Angeles", decoded.agencyTimezone)
    assertNull(decoded.agencyLang)
    assertNull(decoded.agencyPhone)
    assertNull(decoded.agencyFareUrl)
    assertNull(decoded.agencyEmail)
    assertNull(decoded.cemvSupport)
  }

  @Test
  fun decodeFromSourceAcceptsOmittedTrailingOptionalFields() {
    val csv = // language=CSV
      """
      feed_publisher_name,feed_publisher_url,feed_lang,default_lang,feed_start_date,feed_end_date,feed_version
      Demo Publisher,https://example.com,en
      """
        .trimIndent()

    val source = Buffer().apply { writeString(csv) }
    val decoded = GtfsCsv.decodeFromSource<FeedInfo>(source).single()
    assertEquals(GtfsCsv.decodeFromString<FeedInfo>(csv).single(), decoded)
    assertEquals("Demo Publisher", decoded.feedPublisherName)
    assertEquals("https://example.com", decoded.feedPublisherUrl)
    assertEquals("en", decoded.feedLang)
    assertNull(decoded.defaultLang)
    assertNull(decoded.feedStartDate)
    assertNull(decoded.feedEndDate)
    assertNull(decoded.feedVersion)
  }

  @Test
  fun decodeAcceptsQuotedFieldThenOmittedTrailingOptionals() {
    val csv = // language=CSV
      """
      agency_id,agency_name,agency_url,agency_timezone,agency_phone
      DTA,"Authority, Inc",https://google.com,America/Los_Angeles
      """
        .trimIndent()

    val decoded = GtfsCsv.decodeFromString<Agency>(csv).single()
    assertEquals("Authority, Inc", decoded.agencyName)
    assertNull(decoded.agencyPhone)
  }

  @Test
  fun decodeSkipsEmptyLinesAmongShortRows() {
    val csv = // language=CSV
      """
      agency_id,agency_name,agency_url,agency_timezone,agency_phone
      DTA,Demo Transit Authority,https://google.com,America/Los_Angeles

      OTA,Other Transit,https://example.org,America/New_York
      """
        .trimIndent()

    val decoded = GtfsCsv.decodeFromString<Agency>(csv)
    assertEquals(2, decoded.size)
    assertEquals("DTA", decoded[0].agencyId)
    assertEquals("OTA", decoded[1].agencyId)
    assertNull(decoded[0].agencyPhone)
    assertNull(decoded[1].agencyPhone)
  }

  @Test
  fun decodeRejectsOmittedTrailingRequiredFields() {
    val csv = // language=CSV
      """
      service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
      FULLW,1,1,1,1,1,1,1
      """
        .trimIndent()

    assertFails { GtfsCsv.decodeFromString<ServiceCalendar>(csv) }
  }

  @Test
  fun decodeIgnoresExtraFieldsPastTheHeader() {
    val csv = // language=CSV
      """
      agency_id,agency_name,agency_url,agency_timezone
      DTA,Demo Transit Authority,https://google.com,America/Los_Angeles,extra
      """
        .trimIndent()

    val decoded = GtfsCsv.decodeFromString<Agency>(csv).single()
    assertEquals("DTA", decoded.agencyId)
    assertEquals("America/Los_Angeles", decoded.agencyTimezone)
  }

  @Test
  fun decodeSkipsLeadingUtf8Bom() {
    val csv = // language=CSV
      """
      agency_id,agency_name,agency_url,agency_timezone
      DTA,Demo Transit Authority,https://google.com,America/Los_Angeles
      """
        .trimIndent()

    val decoded = GtfsCsv.decodeFromString<Agency>("\uFEFF$csv").single()
    assertEquals("DTA", decoded.agencyId)
    assertEquals("Demo Transit Authority", decoded.agencyName)
  }

  @Test
  fun decodeFromSourceSkipsLeadingUtf8Bom() {
    val csv = // language=CSV
      """
      agency_id,agency_name,agency_url,agency_timezone
      DTA,Demo Transit Authority,https://google.com,America/Los_Angeles
      """
        .trimIndent()

    val source = Buffer().apply { writeString("\uFEFF$csv") }
    val decoded = GtfsCsv.decodeFromSource<Agency>(source).single()
    assertEquals("DTA", decoded.agencyId)
    assertEquals("Demo Transit Authority", decoded.agencyName)
  }
}
