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
      "last_updated": 1640887163,
      "ttl": 0,
      "version": "2.3",
      "data": {
          "stations": [
              {
                  "station_id": "station1",
                  "is_installed": 1,
                  "is_renting": 1,
                  "is_returning": 1,
                  "last_reported": 1609866125,
                  "num_docks_available": 3,
                  "num_docks_disabled": 1,
                  "num_bikes_available": 1,
                  "num_bikes_disabled": 2
              },
              {
                  "station_id": "station2",
                  "is_installed": 1,
                  "is_renting": 1,
                  "is_returning": 1,
                  "last_reported": 1609866106,
                  "num_docks_available": 8,
                  "num_docks_disabled": 1,
                  "num_bikes_available": 6,
                  "num_bikes_disabled": 1
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
    ttl = 0.seconds,
    version = "2.3",
    data =
      StationStatus(
        stations =
          listOf(
            StationStatusEntry(
              stationId = "station1",
              isInstalled = true,
              isRenting = true,
              isReturning = true,
              lastReported = Instant.fromEpochSeconds(1609866125),
              numDocksAvailable = 3,
              numDocksDisabled = 1,
              numBikesAvailable = 1,
              numBikesDisabled = 2,
            ),
            StationStatusEntry(
              stationId = "station2",
              isInstalled = true,
              isRenting = true,
              isReturning = true,
              lastReported = Instant.fromEpochSeconds(1609866106),
              numDocksAvailable = 8,
              numDocksDisabled = 1,
              numBikesAvailable = 6,
              numBikesDisabled = 1,
            ),
          )
      ),
  )

class StationStatusTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<StationStatus>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
