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
    "ttl": 0,
    "version": "3.0",
    "data": {
      "feeds": [
        {
          "name": "system_information",
          "url": "https://www.example.com/gbfs/1/system_information"
        },
        {
          "name": "station_information",
          "url": "https://www.example.com/gbfs/1/station_information"
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
    ttl = 0.seconds,
    version = "3.0",
    data =
      ServiceManifest(
        FeedType.SystemInformation to "https://www.example.com/gbfs/1/system_information",
        FeedType.StationInformation to "https://www.example.com/gbfs/1/station_information",
      ),
  )

class SystemManifestTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<ServiceManifest>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
