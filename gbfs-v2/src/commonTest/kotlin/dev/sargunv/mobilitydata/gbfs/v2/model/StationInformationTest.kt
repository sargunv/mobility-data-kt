package dev.sargunv.mobilitydata.gbfs.v2.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.maplibre.spatialk.geojson.MultiPolygon
import org.maplibre.spatialk.geojson.Position

private val physicalJsonContent = // language=JSON
  """
  {
      "last_updated": 1640887163,
      "ttl": 0,
      "version": "2.3",
      "data": {
          "stations": [
              {
                  "station_id": "pga",
                  "name": "Parking garage A",
                  "lat": 12.345678,
                  "lon": 45.678901,
                  "parking_type": "underground_parking",
                  "parking_hoop": false,
                  "contact_phone": "+33109874321",
                  "is_charging_station": true,
                  "vehicle_type_capacity": {
                      "abc123": 7,
                      "def456": 9
                  }
              }
          ]
      }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val physicalExpectedResponse =
  GbfsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 0.seconds,
    version = "2.3",
    data =
      StationInformation(
        stations =
          listOf(
            Station(
              stationId = "pga",
              name = "Parking garage A",
              lat = 12.345678,
              lon = 45.678901,
              parkingType = ParkingType.UndergroundParking,
              parkingHoop = false,
              contactPhone = "+33109874321",
              isChargingStation = true,
              vehicleTypeCapacity = mapOf("abc123" to 7, "def456" to 9),
            )
          )
      ),
  )

private val virtualJsonContent = // language=json
  """
  {
      "last_updated": 1640887163,
      "ttl": 0,
      "version": "2.3",
      "data": {
          "stations": [
              {
                  "station_id": "station12",
                  "name": "SE Belmont & SE 10 th",
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
                  "vehicle_capacity": {
                      "abc123": 8,
                      "def456": 8,
                      "ghi789": 16
                  }
              }
          ]
      }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val virtualExpectedResponse =
  GbfsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 0.seconds,
    version = "2.3",
    data =
      StationInformation(
        stations =
          listOf(
            Station(
              stationId = "station12",
              name = "SE Belmont & SE 10 th",
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
              vehicleCapacity = mapOf("abc123" to 8, "def456" to 8, "ghi789" to 16),
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
