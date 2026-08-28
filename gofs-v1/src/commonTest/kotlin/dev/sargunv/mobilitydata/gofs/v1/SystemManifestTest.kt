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
      "en": {
        "feeds": [
          {
            "name": "system_information",
            "url": "https://www.example.com/gofs/1/en/system_information"
          },
          {
            "name": "zones",
            "url": "https://www.example.com/gofs/1/en/zones"
          }
        ]
      },
      "fr" : {
        "feeds": [
          {
            "name": "system_information",
            "url": "https://www.example.com/gofs/1/fr/system_information"
          },
          {
            "name": "zones",
            "url": "https://www.example.com/gofs/1/fr/zones"
          }
        ]
      }
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
      SystemManifest(
        "en" to
          Service(
            FeedType.SystemInformation to "https://www.example.com/gofs/1/en/system_information",
            FeedType.Zones to "https://www.example.com/gofs/1/en/zones",
          ),
        "fr" to
          Service(
            FeedType.SystemInformation to "https://www.example.com/gofs/1/fr/system_information",
            FeedType.Zones to "https://www.example.com/gofs/1/fr/zones",
          ),
      ),
  )

class SystemManifestTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<SystemManifest>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }

  @Test
  fun decodeWaitTimesDiscoveryAlias() {
    val json = // language=JSON
      """
      {
        "last_updated": 1609866247,
        "ttl": 0,
        "version": "1.0",
        "data": {
          "en": {
            "feeds": [
              {
                "name": "wait_times",
                "url": "https://www.example.com/gofs/1/en/wait_times"
              }
            ]
          }
        }
      }
      """
        .trimIndent()

    val decoded = GofsJson.decodeFromString<GofsFeedResponse<SystemManifest>>(json)
    assertEquals(
      "https://www.example.com/gofs/1/en/wait_times",
      decoded.data.getService("en").feeds.getValue(FeedType.WaitTimes),
    )
  }

  @Test
  fun encodeWaitTimesUsesSpecName() {
    val response =
      GofsFeedResponse(
        lastUpdated = Instant.fromEpochSeconds(1609866247),
        ttl = 0.seconds,
        version = "1.0",
        data =
          SystemManifest(
            "en" to Service(FeedType.WaitTimes to "https://www.example.com/gofs/1/en/wait_time")
          ),
      )

    val encoded = GofsJson.encodeToJsonElement(response)
    val expected =
      Json.decodeFromString<JsonElement>(
        // language=JSON
        """
        {
          "last_updated": 1609866247,
          "ttl": 0,
          "version": "1.0",
          "data": {
            "en": {
              "feeds": [
                {
                  "name": "wait_time",
                  "url": "https://www.example.com/gofs/1/en/wait_time"
                }
              ]
            }
          }
        }
        """
          .trimIndent()
      )
    assertEquals(expected, encoded)
  }
}
