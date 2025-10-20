package dev.sargunv.mobilitydata.gbfs.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent = // language=JSON
  """
  {
      "last_updated": 1604519393,
      "ttl": 60,
      "version": "2.3",
      "data": {
          "alerts": [
              {
                  "alert_id": "21",
                  "type": "STATION_CLOSURE",
                  "station_ids": ["123", "456", "789"],
                  "times": [
                      {
                          "start": 1604448000,
                          "end": 1604674800
                      }
                  ],
                  "url": "https://example.com/more-info",
                  "summary": "Disruption of Service",
                  "description": "The three stations on Broadway will be out of service from 12:00am Nov 3 to 3:00pm Nov 6th to accommodate road work",
                  "last_updated": 1604198100
              }
          ]
      }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GbfsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1604519393),
    ttl = 60.seconds,
    version = "2.3",
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
                    start = Instant.fromEpochSeconds(1604448000),
                    end = Instant.fromEpochSeconds(1604674800),
                  )
                ),
              url = "https://example.com/more-info",
              summary = "Disruption of Service",
              description =
                "The three stations on Broadway will be out of service from 12:00am Nov 3 to 3:00pm Nov 6th to accommodate road work",
              lastUpdated = Instant.fromEpochSeconds(1604198100),
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
