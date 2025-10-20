package dev.sargunv.mobilitydata.gbfs.v3

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
      "last_updated": 1640887163,
      "ttl": 0,
      "version": "2.3",
      "data": {
          "stations": [
              {
                  "station_id": "station1",
                  "is_installed": true,
                  "is_renting": true,
                  "is_returning": true,
                  "last_reported": 1609866125,
                  "num_docks_available": 3,
                  "num_docks_disabled": 1,
                  "vehicle_docks_available": [
                      {
                          "vehicle_type_ids": ["abc123", "def456"],
                          "count": 2
                      },
                      {
                          "vehicle_type_ids": ["def456"],
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
                  "last_reported": 1609866106,
                  "num_docks_available": 8,
                  "num_docks_disabled": 1,
                  "vehicle_docks_available": [
                      {
                          "vehicle_type_ids": ["abc123"],
                          "count": 6
                      },
                      {
                          "vehicle_type_ids": ["def456"],
                          "count": 2
                      }
                  ],
                  "num_bikes_available": 6,
                  "num_bikes_disabled": 1,
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
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 0.seconds,
    version = "2.3",
    data =
      StationStatus(
        stations =
          listOf(
            StationStatusEntry(
              stationId = "station1",
              isInstalled = true,
              isRenting = true,
              isReturning = true,
              lastReported = Instant.fromEpochSeconds(1609866125),
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
              lastReported = Instant.fromEpochSeconds(1609866106),
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
