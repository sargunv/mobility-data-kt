package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent = // language=JSON
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl": 60,
    "version": "3.0",
    "data": {
      "alerts": [
        {
          "alert_id": "21",
          "type": "station_closure",
          "station_ids": ["123", "456", "789"],
          "times": [
            {
              "start": "2020-11-04T00:00:00+02:00",
              "end": "2020-11-06T15:00:00+02:00"
            }
          ],
          "url": [
            {
              "text": "https://example.com/more-info",
              "language": "en"
            }
          ],
          "summary": [
            {
              "text": "Disruption of Service",
              "language": "en"
            }
          ],
          "description": [
            {
              "text": "The three stations on Broadway will be out of service from 12:00am Nov 3 to 3:00pm Nov 6th to accommodate road work",
              "language": "en"
            }
          ],
          "last_updated": "2020-11-01T06:35:00+02:00"
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 60.seconds,
    version = "3.0",
    data =
      SystemAlerts(
        alerts =
          listOf(
            Alert(
              alertId = "21",
              type = AlertType.StationClosure,
              stationIds = listOf("123", "456", "789"),
              times =
                listOf(
                  AlertTime(
                    start = Timestamp.parse("2020-11-04T00:00:00+02:00"),
                    end = Timestamp.parse("2020-11-06T15:00:00+02:00"),
                  )
                ),
              url = mapOf("en" to "https://example.com/more-info"),
              summary = mapOf("en" to "Disruption of Service"),
              description =
                mapOf(
                  "en" to
                    "The three stations on Broadway will be out of service from 12:00am Nov 3 to 3:00pm Nov 6th to accommodate road work"
                ),
              lastUpdated = Timestamp.parse("2020-11-01T06:35:00+02:00"),
            )
          )
      ),
  )

class SystemAlertsTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<SystemAlerts>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
