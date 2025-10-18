package dev.sargunv.mobilitydata.gbfs.v2.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val carsharingJsonContent = // language=JSON
  """
  {
      "last_updated": 1640887163,
      "ttl": 0,
      "version": "2.3",
      "data": {
          "bikes": [
              {
                  "bike_id": "45bd3fb7-a2d5-4def-9de1-c645844ba962",
                  "last_reported": 1609866109,
                  "lat": 12.345678,
                  "lon": 56.789012,
                  "is_reserved": false,
                  "is_disabled": false,
                  "vehicle_type_id": "abc123",
                  "current_range_meters": 400000.1,
                  "available_until": "2021-05-17T15:00:00Z",
                  "home_station_id": "station1",
                  "vehicle_equipment": ["child_seat_a", "winter_tires"]
              },
              {
                  "bike_id": "d4521def-7922-4e46-8e1d-8ac397239bd0",
                  "last_reported": 1609866204,
                  "is_reserved": false,
                  "is_disabled": false,
                  "vehicle_type_id": "def456",
                  "current_fuel_percent": 0.7,
                  "current_range_meters": 6543.1,
                  "station_id": "86",
                  "pricing_plan_id": "plan3",
                  "home_station_id": "146",
                  "vehicle_equipment": ["child_seat_a"]
              }
          ]
      }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val carshareExpectedResponse =
  GbfsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 0.seconds,
    version = "2.3",
    data =
      FreeBikeStatus(
        bikes =
          listOf(
            Bike(
              bikeId = "45bd3fb7-a2d5-4def-9de1-c645844ba962",
              lastReported = Instant.fromEpochSeconds(1609866109),
              lat = 12.345678,
              lon = 56.789012,
              isReserved = false,
              isDisabled = false,
              vehicleTypeId = "abc123",
              currentRangeMeters = 400000.1,
              availableUntil = Instant.parse("2021-05-17T15:00:00Z"),
              homeStationId = "station1",
              vehicleEquipment = listOf(VehicleEquipment.ChildSeatA, VehicleEquipment.WinterTires),
            ),
            Bike(
              bikeId = "d4521def-7922-4e46-8e1d-8ac397239bd0",
              lastReported = Instant.fromEpochSeconds(1609866204),
              isReserved = false,
              isDisabled = false,
              vehicleTypeId = "def456",
              currentFuelPercent = 0.7,
              currentRangeMeters = 6543.1,
              stationId = "86",
              pricingPlanId = "plan3",
              homeStationId = "146",
              vehicleEquipment = listOf(VehicleEquipment.ChildSeatA),
            ),
          )
      ),
  )

private val micromobilityJsonContent = // language=json
  """
  {
      "last_updated": 1640887163,
      "ttl": 0,
      "version": "2.3",
      "data": {
          "bikes": [
              {
                  "bike_id": "973a5c94-c288-4a2b-afa6-de8aeb6ae2e5",
                  "last_reported": 1609866109,
                  "lat": 12.34,
                  "lon": 56.78,
                  "is_reserved": false,
                  "is_disabled": false,
                  "vehicle_type_id": "abc123",
                  "rental_uris": {
                      "android": "https://www.example.com/app?bike_id=973a5c94-c288-4a2b-afa6-de8aeb6ae2e5&platform=android&",
                      "ios": "https://www.example.com/app?bike_id=973a5c94-c288-4a2b-afa6-de8aeb6ae2e5&platform=ios"
                  }
              },
              {
                  "bike_id": "987fd100-b822-4347-86a4-b3eef8ca8b53",
                  "last_reported": 1609866204,
                  "is_reserved": false,
                  "is_disabled": false,
                  "vehicle_type_id": "def456",
                  "current_range_meters": 6543.1,
                  "station_id": "86",
                  "pricing_plan_id": "plan3"
              }
          ]
      }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val micromobilityExpectedResponse =
  GbfsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 0.seconds,
    version = "2.3",
    data =
      FreeBikeStatus(
        bikes =
          listOf(
            Bike(
              bikeId = "973a5c94-c288-4a2b-afa6-de8aeb6ae2e5",
              lastReported = Instant.fromEpochSeconds(1609866109),
              lat = 12.34,
              lon = 56.78,
              isReserved = false,
              isDisabled = false,
              vehicleTypeId = "abc123",
              rentalUris =
                RentalUris(
                  android =
                    "https://www.example.com/app?bike_id=973a5c94-c288-4a2b-afa6-de8aeb6ae2e5&platform=android&",
                  ios =
                    "https://www.example.com/app?bike_id=973a5c94-c288-4a2b-afa6-de8aeb6ae2e5&platform=ios",
                ),
            ),
            Bike(
              bikeId = "987fd100-b822-4347-86a4-b3eef8ca8b53",
              lastReported = Instant.fromEpochSeconds(1609866204),
              isReserved = false,
              isDisabled = false,
              vehicleTypeId = "def456",
              currentRangeMeters = 6543.1,
              stationId = "86",
              pricingPlanId = "plan3",
            ),
          )
      ),
  )

class FreeBikeStatusTest {
  @Test
  fun encodeCarshare() {
    val expectedJson = Json.decodeFromString<JsonElement>(carsharingJsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(carshareExpectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decodeCarshare() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<FreeBikeStatus>>(carsharingJsonContent)
    assertEquals(carshareExpectedResponse, decodedResponse)
  }

  @Test
  fun encodeMicromobility() {
    val expectedJson = Json.decodeFromString<JsonElement>(micromobilityJsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(micromobilityExpectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decodeMicromobility() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<FreeBikeStatus>>(micromobilityJsonContent)
    assertEquals(micromobilityExpectedResponse, decodedResponse)
  }
}
