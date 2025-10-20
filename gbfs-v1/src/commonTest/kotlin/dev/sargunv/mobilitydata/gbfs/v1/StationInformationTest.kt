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
                  "station_id": "pga",
                  "name": "Parking garage A",
                  "lat": 12.345678,
                  "lon": 45.678901
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
      StationInformation(
        stations =
          listOf(
            Station(stationId = "pga", name = "Parking garage A", lat = 12.345678, lon = 45.678901)
          )
      ),
  )

class StationInformationTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<StationInformation>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
