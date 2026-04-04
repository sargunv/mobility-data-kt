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
    "ttl": 0,
    "version": "1.0",
    "data": {
      "versions": [
        {
          "version": "1.0",
          "url": "https://www.example.com/gofs/2/gofs"
        },
        {
          "version": "X.Y",
          "url": "https://www.example.com/gofs/3/gofs"
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
    ttl = 0.seconds,
    version = "1.0",
    data =
      VersionManifest(
        versions =
          listOf(
            VersionInfo(version = "1.0", url = "https://www.example.com/gofs/2/gofs"),
            VersionInfo(version = "X.Y", url = "https://www.example.com/gofs/3/gofs"),
          )
      ),
  )

class VersionManifestTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<VersionManifest>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
