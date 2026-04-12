package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.RgbColorCode
import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent = // language=JSON
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl": 1800,
    "version": "3.0",
    "data": {
      "system_id": "example_cityname",
      "languages": ["en"],
      "name": [
        {
          "text": "Example Bike Rental",
          "language": "en"
        }
      ],
      "short_name": [
        {
          "text": "Example Bike",
          "language": "en"
        }
      ],
      "operator": [
        {
          "text": "Example Sharing, Inc",
          "language": "en"
        }
      ],
      "opening_hours": "Apr 1-Nov 3 00:00-24:00",
      "start_date": "2010-06-10",
      "url": "https://www.example.com",
      "purchase_url": "https://www.example.com",
      "phone_number": "+18005551234",
      "email": "customerservice@example.com",
      "feed_contact_email": "datafeed@example.com",
      "timezone": "America/Chicago",
      "license_url": "https://www.example.com/data-license.html",
      "terms_url": [
        {
           "text": "https://www.example.com/en/terms",
           "language": "en"
        }
      ],
      "terms_last_updated": "2021-06-21",
      "privacy_url": [
        {
           "text": "https://www.example.com/en/privacy-policy",
           "language": "en"
        }
      ],
      "privacy_last_updated": "2019-01-13",
      "rental_apps": {
        "android": {
          "discovery_uri": "com.example.android://",
          "store_uri": "https://play.google.com/store/apps/details?id=com.example.android"
        },
        "ios": {
          "store_uri": "https://apps.apple.com/app/apple-store/id123456789",
          "discovery_uri": "com.example.ios://"
        }
      },
      "brand_assets": {
          "brand_last_modified": "2021-06-15",
          "brand_image_url": "https://www.example.com/assets/brand_image.svg",
          "brand_image_url_dark": "https://www.example.com/assets/brand_image_dark.svg",
          "color": "#C2D32C",
          "brand_terms_url": "https://www.example.com/assets/brand.pdf"
        }
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 1800.seconds,
    version = "3.0",
    data =
      SystemInformation(
        systemId = "example_cityname",
        languages = listOf("en"),
        name = mapOf("en" to "Example Bike Rental"),
        openingHours = "Apr 1-Nov 3 00:00-24:00",
        shortName = mapOf("en" to "Example Bike"),
        operator = mapOf("en" to "Example Sharing, Inc"),
        url = "https://www.example.com",
        purchaseUrl = "https://www.example.com",
        startDate = LocalDate(2010, 6, 10),
        phoneNumber = "+18005551234",
        email = "customerservice@example.com",
        feedContactEmail = "datafeed@example.com",
        timezone = TimeZone.of("America/Chicago"),
        licenseUrl = "https://www.example.com/data-license.html",
        termsUrl = mapOf("en" to "https://www.example.com/en/terms"),
        termsLastUpdated = LocalDate(2021, 6, 21),
        privacyUrl = mapOf("en" to "https://www.example.com/en/privacy-policy"),
        privacyLastUpdated = LocalDate(2019, 1, 13),
        rentalApps =
          RentalAppUris(
            android =
              RentalAppPlatformUris(
                discoveryUri = "com.example.android://",
                storeUri = "https://play.google.com/store/apps/details?id=com.example.android",
              ),
            ios =
              RentalAppPlatformUris(
                storeUri = "https://apps.apple.com/app/apple-store/id123456789",
                discoveryUri = "com.example.ios://",
              ),
          ),
        brandAssets =
          BrandAssets(
            brandLastModified = LocalDate(2021, 6, 15),
            brandImageUrl = "https://www.example.com/assets/brand_image.svg",
            brandImageUrlDark = "https://www.example.com/assets/brand_image_dark.svg",
            color = RgbColorCode(0xC2D32C),
            brandTermsUrl = "https://www.example.com/assets/brand.pdf",
          ),
      ),
  )

class SystemInformationTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<SystemInformation>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
