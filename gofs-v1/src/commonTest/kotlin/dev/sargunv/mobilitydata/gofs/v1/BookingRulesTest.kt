package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.toServiceTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent = // language=JSON
  """
  {
    "last_updated": 1609866247,
    "ttl": 86400,
    "version": "1.0",
    "data": {
      "booking_rules": [
        {
          "from_zone_ids": ["zoneA"],
          "to_zone_ids": null,
          "booking_type": 1,
          "prior_notice_duration_min": 30,
          "prior_notice_duration_max": 180
        },
        {
          "from_zone_ids": ["zoneA"],
          "to_zone_ids": ["zoneB"],
          "booking_type": 2,
          "prior_notice_start_day": 2,
          "prior_notice_last_time": "17:00:00"
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GofsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1609866247),
    ttl = 86400.seconds,
    version = "1.0",
    data =
      BookingRules(
        bookingRules =
          listOf(
            BookingRule(
              fromZoneIds = listOf("zoneA"),
              toZoneIds = null,
              bookingType = BookingType.AdvanceSameDay,
              priorNoticeDurationMin = 30.minutes,
              priorNoticeDurationMax = 180.minutes,
            ),
            BookingRule(
              fromZoneIds = listOf("zoneA"),
              toZoneIds = listOf("zoneB"),
              bookingType = BookingType.AdvancePriorDay,
              priorNoticeStartDay = 2,
              priorNoticeLastTime = LocalTime.parse("17:00:00").toServiceTime(),
            ),
          )
      ),
  )

class BookingRulesTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<BookingRules>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
