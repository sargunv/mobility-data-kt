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
          "bikes": [
              {
                  "bike_id": "45bd3fb7-a2d5-4def-9de1-c645844ba962",
                  "lat": 12.345678,
                  "lon": 56.789012,
                  "is_reserved": 0,
                  "is_disabled": 0
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
      FreeBikeStatus(
        bikes =
          listOf(
            Bike(
              bikeId = "45bd3fb7-a2d5-4def-9de1-c645844ba962",
              lat = 12.345678,
              lon = 56.789012,
              isReserved = false,
              isDisabled = false,
            )
          )
      ),
  )

class FreeBikeStatusTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<FreeBikeStatus>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
