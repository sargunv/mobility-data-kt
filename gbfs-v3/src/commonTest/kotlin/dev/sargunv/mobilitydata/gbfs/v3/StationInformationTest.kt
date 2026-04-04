package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.maplibre.spatialk.geojson.MultiPolygon
import org.maplibre.spatialk.geojson.Position

private val physicalJsonContent = // language=JSON
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl": 0,
    "version": "3.0",
    "data": {
      "stations": [
        {
          "station_id": "pga",
          "name": [
            {
              "text": "Parking garage A",
              "language": "en"
            }
          ],
          "lat": 12.345678,
          "lon": 45.678901,
          "parking_type": "underground_parking",
          "parking_hoop": false,
          "contact_phone": "+33109874321",
          "is_charging_station": true,
          "vehicle_docks_capacity": [
            {
              "vehicle_type_ids": ["abc123"],
              "count": 7
            }
          ]
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val physicalExpectedResponse =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 0.seconds,
    version = "3.0",
    data =
      StationInformation(
        stations =
          listOf(
            Station(
              stationId = "pga",
              name = mapOf("en" to "Parking garage A"),
              lat = 12.345678,
              lon = 45.678901,
              parkingType = ParkingType.UndergroundParking,
              parkingHoop = false,
              contactPhone = "+33109874321",
              isChargingStation = true,
              vehicleDocksCapacity =
                listOf(CountByMultipleVehicleTypes(vehicleTypeIds = listOf("abc123"), count = 7)),
            )
          )
      ),
  )

private val virtualJsonContent = // language=json
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl": 0,
    "version": "3.0",
    "data": {
      "stations": [
        {
          "station_id": "station12",
          "name": [
            {
              "text": "SE Belmont & SE 10th",
              "language": "en"
            }
          ],
          "lat": 45.516445,
          "lon": -122.655775,
          "is_valet_station": false,
          "is_virtual_station": true,
          "is_charging_station": false,
          "station_area": {
            "type": "MultiPolygon",
            "coordinates": [
              [
                [
                  [-122.655775, 45.516445],
                  [-122.655705, 45.516445],
                  [-122.655705, 45.516495],
                  [-122.655775, 45.516495],
                  [-122.655775, 45.516445]
                ]
              ]
            ]
          },
          "capacity": 16,
          "vehicle_types_capacity": [
            {
              "vehicle_type_ids": ["abc123", "def456"],
              "count": 15
            },
            {
              "vehicle_type_ids": ["def456"],
              "count": 1
            }
          ]
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val virtualExpectedResponse =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 0.seconds,
    version = "3.0",
    data =
      StationInformation(
        stations =
          listOf(
            Station(
              stationId = "station12",
              name = mapOf("en" to "SE Belmont & SE 10th"),
              lat = 45.516445,
              lon = -122.655775,
              isValetStation = false,
              isVirtualStation = true,
              isChargingStation = false,
              stationArea =
                MultiPolygon(
                  coordinates =
                    listOf(
                      listOf(
                        listOf(
                          Position(-122.655775, 45.516445),
                          Position(-122.655705, 45.516445),
                          Position(-122.655705, 45.516495),
                          Position(-122.655775, 45.516495),
                          Position(-122.655775, 45.516445),
                        )
                      )
                    )
                ),
              capacity = 16,
              vehicleTypeCapacity =
                listOf(
                  CountByMultipleVehicleTypes(
                    vehicleTypeIds = listOf("abc123", "def456"),
                    count = 15,
                  ),
                  CountByMultipleVehicleTypes(vehicleTypeIds = listOf("def456"), count = 1),
                ),
            )
          )
      ),
  )

class StationInformationTest {
  @Test
  fun encodePhysical() {
    val expectedJson = Json.decodeFromString<JsonElement>(physicalJsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(physicalExpectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decodePhysical() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<StationInformation>>(physicalJsonContent)
    assertEquals(physicalExpectedResponse, decodedResponse)
  }

  @Test
  fun encodeVirtual() {
    val expectedJson = Json.decodeFromString<JsonElement>(virtualJsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(virtualExpectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decodeVirtual() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<StationInformation>>(virtualJsonContent)
    assertEquals(virtualExpectedResponse, decodedResponse)
  }
}
