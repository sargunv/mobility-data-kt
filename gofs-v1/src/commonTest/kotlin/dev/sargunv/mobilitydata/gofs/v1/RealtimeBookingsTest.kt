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
    "ttl": 300,
    "version": "1.0",
    "data": {
      "realtime_booking": [
        {
          "brand_id": "regular_ride",
          "wait_time": 300,
          "travel_time": 300,
          "travel_cost": 30.1,
          "travel_cost_currency": "CAD",
          "booking_detail": {
              "service_name": "Taxi",
              "android_uri": "https://www.example.com/app?service_type=REG&platform=android",
              "ios_uri": "https://www.example.com/app?service_type=REG&platform=ios",
              "web_uri": "https://www.example.com/app?service_type=REG",
              "phone_number": "+18005551234"
          }
        },
        {
          "brand_id": "large_ride",
          "wait_time": 450,
          "travel_time": 300,
          "travel_cost": 45.1,
          "travel_cost_currency": "CAD",
          "booking_detail": {
              "service_name": "Taxi Van",
              "android_uri": "https://www.example.com/app?service_type=XL&platform=android",
              "ios_uri": "https://www.example.com/app?service_type=XL&platform=ios",
              "web_uri": "https://www.example.com/app?service_type=XL",
              "phone_number": "+18005551234"
          }
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
    ttl = 300.seconds,
    version = "1.0",
    data =
      RealtimeBookings(
        realtimeBookings =
          listOf(
            RealtimeBooking(
              brandId = "regular_ride",
              waitTime = 300.seconds,
              travelTime = 300.seconds,
              travelCost = 30.1,
              travelCostCurrency = "CAD",
              bookingDetail =
                BookingDetail(
                  serviceName = "Taxi",
                  androidUri = "https://www.example.com/app?service_type=REG&platform=android",
                  iosUri = "https://www.example.com/app?service_type=REG&platform=ios",
                  webUri = "https://www.example.com/app?service_type=REG",
                  phoneNumber = "+18005551234",
                ),
            ),
            RealtimeBooking(
              brandId = "large_ride",
              waitTime = 450.seconds,
              travelTime = 300.seconds,
              travelCost = 45.1,
              travelCostCurrency = "CAD",
              bookingDetail =
                BookingDetail(
                  serviceName = "Taxi Van",
                  androidUri = "https://www.example.com/app?service_type=XL&platform=android",
                  iosUri = "https://www.example.com/app?service_type=XL&platform=ios",
                  webUri = "https://www.example.com/app?service_type=XL",
                  phoneNumber = "+18005551234",
                ),
            ),
          )
      ),
  )

class RealtimeBookingsTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<RealtimeBookings>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
