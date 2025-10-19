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
      "ttl": 0,
      "version": "2.3",
      "data": {
          "en": {
              "feeds": [
                  {
                      "name": "system_information",
                      "url": "https://www.example.com/gbfs/1/en/system_information"
                  },
                  {
                      "name": "station_information",
                      "url": "https://www.example.com/gbfs/1/en/station_information"
                  }
              ]
          },
          "fr": {
              "feeds": [
                  {
                      "name": "system_information",
                      "url": "https://www.example.com/gbfs/1/fr/system_information"
                  },
                  {
                      "name": "station_information",
                      "url": "https://www.example.com/gbfs/1/fr/station_information"
                  }
              ]
          }
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
      GbfsManifest(
        "en" to
          Service(
            FeedType.SystemInformation to "https://www.example.com/gbfs/1/en/system_information",
            FeedType.StationInformation to "https://www.example.com/gbfs/1/en/station_information",
          ),
        "fr" to
          Service(
            FeedType.SystemInformation to "https://www.example.com/gbfs/1/fr/system_information",
            FeedType.StationInformation to "https://www.example.com/gbfs/1/fr/station_information",
          ),
      ),
  )

class ManifestTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<GbfsManifest>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
