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
    "ttl": 86400,
    "version": "3.0",
    "data": {
      "regions": [
        {
          "name": [
            {
              "text": "North",
              "language": "en"
            }
          ],
          "region_id": "3"
        },
        {
          "name": [
            {
              "text": "East",
              "language": "en"
            }
          ],
          "region_id": "4"
        },
        {
          "name": [
            {
              "text": "South",
              "language": "en"
            }
          ],
          "region_id": "5"
        },
        {
          "name": [
            {
              "text": "West",
              "language": "en"
            }
          ],
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
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 86400.seconds,
    version = "3.0",
    data =
      SystemRegions(
        regions =
          listOf(
            Region(regionId = "3", name = mapOf("en" to "North")),
            Region(regionId = "4", name = mapOf("en" to "East")),
            Region(regionId = "5", name = mapOf("en" to "South")),
            Region(regionId = "6", name = mapOf("en" to "West")),
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
