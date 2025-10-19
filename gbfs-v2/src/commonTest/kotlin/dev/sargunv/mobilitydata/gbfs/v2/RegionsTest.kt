package dev.sargunv.mobilitydata.gbfs.v2

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
      "ttl": 86400,
      "version": "2.3",
      "data": {
          "regions": [
              {
                  "name": "North",
                  "region_id": "3"
              },
              {
                  "name": "East",
                  "region_id": "4"
              },
              {
                  "name": "South",
                  "region_id": "5"
              },
              {
                  "name": "West",
                  "region_id": "6"
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
      SystemRegions(
        regions =
          listOf(
            Region(regionId = "3", name = "North"),
            Region(regionId = "4", name = "East"),
            Region(regionId = "5", name = "South"),
            Region(regionId = "6", name = "West"),
          )
      ),
  )

class RegionsTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<SystemRegions>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
