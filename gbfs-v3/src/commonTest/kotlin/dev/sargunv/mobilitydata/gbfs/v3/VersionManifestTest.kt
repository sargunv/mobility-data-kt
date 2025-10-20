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
      "versions": [
        {
          "version": "2.0",
          "url": "https://www.example.com/gbfs/2/gbfs"
        },
        {
          "version": "3.0",
          "url": "https://www.example.com/gbfs/3/gbfs"
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
      VersionManifest(
        versions =
          listOf(
            VersionInfo(version = "2.0", url = "https://www.example.com/gbfs/2/gbfs"),
            VersionInfo(version = "3.0", url = "https://www.example.com/gbfs/3/gbfs"),
          )
      ),
  )

class VersionManifestTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<VersionManifest>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
