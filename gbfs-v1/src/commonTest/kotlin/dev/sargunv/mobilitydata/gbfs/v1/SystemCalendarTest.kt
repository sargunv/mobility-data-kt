package dev.sargunv.mobilitydata.gbfs.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.Month
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent = // language=JSON
  """
  {
      "last_updated": 1640887163,
      "ttl": 86400,
      "version": "2.3",
      "data": {
          "calendars": [
              {
                  "start_month": 4,
                  "start_day": 1,
                  "start_year": 2020,
                  "end_month": 11,
                  "end_day": 5,
                  "end_year": 2020
              }
          ]
      }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GbfsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 86400.seconds,
    version = "2.3",
    data =
      SystemCalendar(
        calendars =
          listOf(
            SystemCalendarEntry(
              startMonth = Month.APRIL,
              startDay = 1,
              startYear = 2020,
              endMonth = Month.NOVEMBER,
              endDay = 5,
              endYear = 2020,
            )
          )
      ),
  )

class SystemCalendarTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<SystemCalendar>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
