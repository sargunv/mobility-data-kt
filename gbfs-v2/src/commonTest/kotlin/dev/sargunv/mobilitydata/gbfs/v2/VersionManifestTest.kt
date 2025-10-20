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
          "versions": [
              {
                  "version": "2.0",
                  "url": "https://www.example.com/gbfs/2/gbfs"
              },
              {
                  "version": "2.3",
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
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 0.seconds,
    version = "2.3",
    data =
      VersionManifest(
        versions =
          listOf(
            VersionInfo(version = "2.0", url = "https://www.example.com/gbfs/2/gbfs"),
            VersionInfo(version = "2.3", url = "https://www.example.com/gbfs/3/gbfs"),
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
