package dev.sargunv.mobilitydata.gofs.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DayOfWeek.*
import kotlinx.datetime.LocalDate
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
      "calendars": [
        {
          "calendar_id": "weekday",
          "days": [
            "mon",
            "tue",
            "wed",
            "thu",
            "fri"
          ],
          "start_date": "20210901",
          "end_date": "20211031",
          "excepted_dates": [
            "20210906"
          ]
        },
        {
          "calendar_id": "weekend",
          "days": [
            "sat",
            "sun"
          ],
          "start_date": "20210901",
          "end_date": "20211031"
        },
        {
          "calendar_id": "labor_day",
          "start_date": "20210906",
          "end_date": "20210906"
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
      Calendars(
        calendars =
          listOf(
            Calendar(
              calendarId = "weekday",
              days = listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY),
              startDate = LocalDate.parse("20210901", LocalDate.Formats.ISO_BASIC),
              endDate = LocalDate.parse("20211031", LocalDate.Formats.ISO_BASIC),
              exceptedDates = listOf(LocalDate.parse("20210906", LocalDate.Formats.ISO_BASIC)),
            ),
            Calendar(
              calendarId = "weekend",
              days = listOf(SATURDAY, SUNDAY),
              startDate = LocalDate.parse("20210901", LocalDate.Formats.ISO_BASIC),
              endDate = LocalDate.parse("20211031", LocalDate.Formats.ISO_BASIC),
            ),
            Calendar(
              calendarId = "labor_day",
              startDate = LocalDate.parse("20210906", LocalDate.Formats.ISO_BASIC),
              endDate = LocalDate.parse("20210906", LocalDate.Formats.ISO_BASIC),
            ),
          )
      ),
  )

class CalendarsTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<Calendars>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
