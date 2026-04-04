package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DayOfWeek
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
          "rental_hours": [
              {
                  "user_types": ["member"],
                  "days": ["sat", "sun"],
                  "start_time": "00:00:00",
                  "end_time": "23:59:59"
              },
              {
                  "user_types": ["nonmember"],
                  "days": ["sat", "sun"],
                  "start_time": "05:00:00",
                  "end_time": "23:59:59"
              },
              {
                  "user_types": ["member", "nonmember"],
                  "days": ["mon", "tue", "wed", "thu", "fri"],
                  "start_time": "00:00:00",
                  "end_time": "23:59:59"
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
      SystemHours(
        rentalHours =
          listOf(
            SystemHoursEntry(
              userTypes = listOf(UserType.Member),
              days = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
              startTime = ServiceTime(0, 0, 0),
              endTime = ServiceTime(23, 59, 59),
            ),
            SystemHoursEntry(
              userTypes = listOf(UserType.NonMember),
              days = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
              startTime = ServiceTime(5, 0, 0),
              endTime = ServiceTime(23, 59, 59),
            ),
            SystemHoursEntry(
              userTypes = listOf(UserType.Member, UserType.NonMember),
              days =
                listOf(
                  DayOfWeek.MONDAY,
                  DayOfWeek.TUESDAY,
                  DayOfWeek.WEDNESDAY,
                  DayOfWeek.THURSDAY,
                  DayOfWeek.FRIDAY,
                ),
              startTime = ServiceTime(0, 0, 0),
              endTime = ServiceTime(23, 59, 59),
            ),
          )
      ),
  )

class SystemHoursTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<SystemHours>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
