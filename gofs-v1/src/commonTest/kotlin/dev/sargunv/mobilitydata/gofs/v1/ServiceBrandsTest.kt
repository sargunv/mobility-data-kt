package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.RgbColor
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
      "service_brands": [
        {
          "brand_id": "regular_ride",
          "brand_name": "Regular Ride",
          "brand_color": "1C7F49",
          "brand_text_color": "FFFFFF"
        },
        {
          "brand_id": "large_ride",
          "brand_name": "Large Ride",
          "brand_color": "1C7F49",
          "brand_text_color": "FFFFFF"
        },
        {
          "brand_id": "shared_ride",
          "brand_name": "Shared Ride",
          "brand_color": "1C7F49",
          "brand_text_color": "FFFFFF"
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
      ServiceBrands(
        serviceBrands =
          listOf(
            Brand(
              brandId = "regular_ride",
              brandName = "Regular Ride",
              brandColor = RgbColor(0x1C7F49),
              brandTextColor = RgbColor(0xFFFFFF),
            ),
            Brand(
              brandId = "large_ride",
              brandName = "Large Ride",
              brandColor = RgbColor(0x1C7F49),
              brandTextColor = RgbColor(0xFFFFFF),
            ),
            Brand(
              brandId = "shared_ride",
              brandName = "Shared Ride",
              brandColor = RgbColor(0x1C7F49),
              brandTextColor = RgbColor(0xFFFFFF),
            ),
          )
      ),
  )

class ServiceBrandsTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<ServiceBrands>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
