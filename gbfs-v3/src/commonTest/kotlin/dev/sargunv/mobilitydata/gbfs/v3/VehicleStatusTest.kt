package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val carsharingJsonContent = // language=JSON
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl":0,
    "version":"3.0",
    "data":{
      "vehicles":[
        {
          "vehicle_id":"45bd3fb7-a2d5-4def-9de1-c645844ba962",
          "last_reported": "2023-07-17T13:34:13+02:00",
          "lat":12.345678,
          "lon":56.789012,
          "is_reserved":false,
          "is_disabled":false,
          "vehicle_type_id":"abc123",
          "current_range_meters":400000.1,
          "available_until":"2021-05-17T15:00:00Z",
          "home_station_id":"station1",
          "vehicle_equipment":[
            "child_seat_a",
            "winter_tires"
          ]
        },
        {
          "vehicle_id":"d4521def-7922-4e46-8e1d-8ac397239bd0",
          "last_reported": "2023-07-17T13:34:13+02:00",
          "is_reserved":false,
          "is_disabled":false,
          "vehicle_type_id":"def456",
          "current_fuel_percent":0.7,
          "current_range_meters":6543.1,
          "station_id":"86",
          "pricing_plan_id":"plan3",
          "home_station_id":"146",
          "vehicle_equipment":[
            "child_seat_a"
          ]
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val carshareExpectedResponse =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 0.seconds,
    version = "3.0",
    data =
      VehicleStatus(
        vehicles =
          listOf(
            Vehicle(
              vehicleId = "45bd3fb7-a2d5-4def-9de1-c645844ba962",
              lastReported = Timestamp.parse("2023-07-17T13:34:13+02:00"),
              lat = 12.345678,
              lon = 56.789012,
              isReserved = false,
              isDisabled = false,
              vehicleTypeId = "abc123",
              currentRangeMeters = 400000.1,
              availableUntil = Timestamp.parse("2021-05-17T15:00:00Z"),
              homeStationId = "station1",
              vehicleEquipment = listOf(VehicleEquipment.ChildSeatA, VehicleEquipment.WinterTires),
            ),
            Vehicle(
              vehicleId = "d4521def-7922-4e46-8e1d-8ac397239bd0",
              lastReported = Timestamp.parse("2023-07-17T13:34:13+02:00"),
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
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl":0,
    "version":"3.0",
    "data":{
      "vehicles":[
        {
          "vehicle_id":"973a5c94-c288-4a2b-afa6-de8aeb6ae2e5",
          "last_reported": "2023-07-17T13:34:13+02:00",
          "lat":12.345678,
          "lon":56.789012,
          "is_reserved":false,
          "is_disabled":false,
          "vehicle_type_id":"abc123",
          "rental_uris": {
            "android": "https://www.example.com/app?vehicle_id=973a5c94-c288-4a2b-afa6-de8aeb6ae2e5&platform=android&",
            "ios": "https://www.example.com/app?vehicle_id=973a5c94-c288-4a2b-afa6-de8aeb6ae2e5&platform=ios"
          }
        },
        {
          "vehicle_id":"987fd100-b822-4347-86a4-b3eef8ca8b53",
          "last_reported": "2023-07-17T13:34:13+02:00",
          "is_reserved":false,
          "is_disabled":false,
          "vehicle_type_id":"def456",
          "current_range_meters":6543.1,
          "station_id":"86",
          "pricing_plan_id":"plan3"
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val micromobilityExpectedResponse =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 0.seconds,
    version = "3.0",
    data =
      VehicleStatus(
        vehicles =
          listOf(
            Vehicle(
              vehicleId = "973a5c94-c288-4a2b-afa6-de8aeb6ae2e5",
              lastReported = Timestamp.parse("2023-07-17T13:34:13+02:00"),
              lat = 12.345678,
              lon = 56.789012,
              isReserved = false,
              isDisabled = false,
              vehicleTypeId = "abc123",
              rentalUris =
                RentalUris(
                  android =
                    "https://www.example.com/app?vehicle_id=973a5c94-c288-4a2b-afa6-de8aeb6ae2e5&platform=android&",
                  ios =
                    "https://www.example.com/app?vehicle_id=973a5c94-c288-4a2b-afa6-de8aeb6ae2e5&platform=ios",
                ),
            ),
            Vehicle(
              vehicleId = "987fd100-b822-4347-86a4-b3eef8ca8b53",
              lastReported = Timestamp.parse("2023-07-17T13:34:13+02:00"),
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

class VehicleStatusTest {
  @Test
  fun encodeCarshare() {
    val expectedJson = Json.decodeFromString<JsonElement>(carsharingJsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(carshareExpectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decodeCarshare() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<VehicleStatus>>(carsharingJsonContent)
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
      GbfsJson.decodeFromString<GbfsFeedResponse<VehicleStatus>>(micromobilityJsonContent)
    assertEquals(micromobilityExpectedResponse, decodedResponse)
  }
}
