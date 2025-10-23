package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

private val csvContent = // language=CSV
  """
  feed_publisher_name,feed_publisher_url,feed_lang,feed_start_date,feed_end_date,feed_version,feed_contact_email,feed_id
  MBTA,http://www.mbta.com,EN,20251014,20251213,"Fall 2025, 2025-10-21T21:42:31+00:00, version D",developer@mbta.com,mbta-ma-us
  """
    .trimIndent()

private val expected =
  listOf(
    FeedInfo(
      feedPublisherName = "MBTA",
      feedPublisherUrl = "http://www.mbta.com",
      feedLang = "EN",
      feedStartDate = LocalDate(2025, 10, 14),
      feedEndDate = LocalDate(2025, 12, 13),
      feedVersion = "Fall 2025, 2025-10-21T21:42:31+00:00, version D",
      feedContactEmail = "developer@mbta.com",
      feedId = "mbta-ma-us",
    )
  )

class FeedInfoTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<FeedInfo>(csvContent)
    assertEquals(expected, decoded)
  }
}
