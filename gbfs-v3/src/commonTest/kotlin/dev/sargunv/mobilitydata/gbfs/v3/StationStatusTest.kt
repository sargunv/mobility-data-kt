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
    "ttl": 0,
    "version": "3.0",
    "data": {
      "stations": [
        {
          "station_id": "station1",
          "is_installed": true,
          "is_renting": true,
          "is_returning": true,
          "last_reported": "2023-07-17T13:34:13+02:00",
          "num_docks_available": 3,
          "num_docks_disabled" : 1,
          "vehicle_docks_available": [
            {
              "vehicle_type_ids": [ "abc123", "def456" ],
              "count": 2
            },
            {
              "vehicle_type_ids": [ "def456" ],
              "count": 1
            }
          ],
          "num_vehicles_available": 1,
          "num_vehicles_disabled": 2,
          "vehicle_types_available": [
            {
              "vehicle_type_id": "abc123",
              "count": 1
            },
            {
              "vehicle_type_id": "def456",
              "count": 0
            }
          ]
        },
        {
          "station_id": "station2",
          "is_installed": true,
          "is_renting": true,
          "is_returning": true,
          "last_reported": "2023-07-17T13:34:13+02:00",
          "num_docks_available": 8,
          "num_docks_disabled" : 1,
          "vehicle_docks_available": [
            {
              "vehicle_type_ids": [ "abc123" ],
              "count": 6
            },
            {
              "vehicle_type_ids": [ "def456" ],
              "count": 2
            }
          ],
          "num_vehicles_available": 6,
          "num_vehicles_disabled": 1,
          "vehicle_types_available": [
            {
              "vehicle_type_id": "abc123",
              "count": 2
            },
            {
              "vehicle_type_id": "def456",
              "count": 4
            }
          ]
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
    ttl = 0.seconds,
    version = "3.0",
    data =
      StationStatus(
        stations =
          listOf(
            StationStatusEntry(
              stationId = "station1",
              isInstalled = true,
              isRenting = true,
              isReturning = true,
              lastReported = Timestamp.parse("2023-07-17T13:34:13+02:00"),
              numDocksAvailable = 3,
              numDocksDisabled = 1,
              vehicleDocksAvailable =
                listOf(
                  CountByMultipleVehicleTypes(
                    vehicleTypeIds = listOf("abc123", "def456"),
                    count = 2,
                  ),
                  CountByMultipleVehicleTypes(vehicleTypeIds = listOf("def456"), count = 1),
                ),
              numVehiclesAvailable = 1,
              numVehiclesDisabled = 2,
              vehicleTypesAvailable =
                listOf(
                  CountBySingleVehicleType(vehicleTypeId = "abc123", count = 1),
                  CountBySingleVehicleType(vehicleTypeId = "def456", count = 0),
                ),
            ),
            StationStatusEntry(
              stationId = "station2",
              isInstalled = true,
              isRenting = true,
              isReturning = true,
              lastReported = Timestamp.parse("2023-07-17T13:34:13+02:00"),
              numDocksAvailable = 8,
              numDocksDisabled = 1,
              vehicleDocksAvailable =
                listOf(
                  CountByMultipleVehicleTypes(vehicleTypeIds = listOf("abc123"), count = 6),
                  CountByMultipleVehicleTypes(vehicleTypeIds = listOf("def456"), count = 2),
                ),
              numVehiclesAvailable = 6,
              numVehiclesDisabled = 1,
              vehicleTypesAvailable =
                listOf(
                  CountBySingleVehicleType(vehicleTypeId = "abc123", count = 2),
                  CountBySingleVehicleType(vehicleTypeId = "def456", count = 4),
                ),
            ),
          )
      ),
  )

class StationStatusTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<StationStatus>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
