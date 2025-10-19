package dev.sargunv.mobilitydata.gofs.v1

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
    "last_updated": 1609866247,
    "ttl": 86400,
    "version": "1.0",
    "data": {
      "wait_times": [
        {
          "brand_id": "regular_ride",
          "wait_time": 300
        },
        {
          "brand_id": "large_ride",
          "wait_time": 600
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
      WaitTimes(
        waitTimes =
          listOf(
            WaitTime(brandId = "regular_ride", waitTime = 300.seconds),
            WaitTime(brandId = "large_ride", waitTime = 600.seconds),
          )
      ),
  )

class WaitTimesTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<WaitTimes>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
