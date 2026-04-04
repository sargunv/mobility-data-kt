package dev.sargunv.mobilitydata.gbfs.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent = // language=JSON
  """
  {
      "last_updated": 1640887163,
      "ttl": 1800,
      "version": "2.3",
      "data": {
          "system_id": "example_cityname",
          "language": "en",
          "name": "Example Bike Rental",
          "short_name": "Example Bike",
          "operator": "Example Sharing, Inc",
          "url": "https://www.example.com",
          "purchase_url": "https://www.example.com",
          "start_date": "2010-06-10",
          "phone_number": "1-800-555-1234",
          "email": "customerservice@example.com",
          "feed_contact_email": "datafeed@example.com",
          "timezone": "America/Chicago",
          "license_url": "https://www.example.com/data-license.html",
          "rental_apps": {
              "android": {
                  "discovery_uri": "com.example.android://",
                  "store_uri": "https://play.google.com/store/apps/details?id=com.example.android"
              },
              "ios": {
                  "store_uri": "https://apps.apple.com/app/apple-store/id123456789",
                  "discovery_uri": "com.example.ios://"
              }
          }
      }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GbfsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 1800.seconds,
    version = "2.3",
    data =
      SystemInformation(
        systemId = "example_cityname",
        language = "en",
        name = "Example Bike Rental",
        shortName = "Example Bike",
        operator = "Example Sharing, Inc",
        url = "https://www.example.com",
        purchaseUrl = "https://www.example.com",
        startDate = LocalDate(2010, 6, 10),
        phoneNumber = "1-800-555-1234",
        email = "customerservice@example.com",
        feedContactEmail = "datafeed@example.com",
        timezone = TimeZone.of("America/Chicago"),
        licenseUrl = "https://www.example.com/data-license.html",
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
