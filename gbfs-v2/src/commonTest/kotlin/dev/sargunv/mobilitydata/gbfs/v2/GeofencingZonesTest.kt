package dev.sargunv.mobilitydata.gbfs.v2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.maplibre.spatialk.geojson.dsl.addFeature
import org.maplibre.spatialk.geojson.dsl.addPolygon
import org.maplibre.spatialk.geojson.dsl.addRing
import org.maplibre.spatialk.geojson.dsl.buildFeatureCollection
import org.maplibre.spatialk.geojson.dsl.buildMultiPolygon

private val jsonContent = // language=JSON
  """
  {
      "last_updated": 1640887163,
      "ttl": 60,
      "version": "2.3",
      "data": {
          "geofencing_zones": {
              "type": "FeatureCollection",
              "features": [
                  {
                      "type": "Feature",
                      "geometry": {
                          "type": "MultiPolygon",
                          "coordinates": [
                              [
                                  [
                                      [-122.578067, 45.562982],
                                      [-122.661838, 45.562741],
                                      [-122.661151, 45.504542],
                                      [-122.578926, 45.5046625],
                                      [-122.578067, 45.562982]
                                  ]
                              ],
                              [
                                  [
                                      [-122.65068, 45.548197],
                                      [-122.650852, 45.534731],
                                      [-122.630939, 45.535212],
                                      [-122.630424, 45.548197],
                                      [-122.65068, 45.548197]
                                  ]
                              ]
                          ]
                      },
                      "properties": {
                          "name": "NE 24th/NE Knott",
                          "start": 1593878400,
                          "end": 1593907260,
                          "rules": [
                              {
                                  "vehicle_type_id": ["moped1", "car1"],
                                  "ride_allowed": false,
                                  "ride_through_allowed": true,
                                  "maximum_speed_kph": 10,
                                  "station_parking": true
                              }
                          ]
                      }
                  }
              ]
          }
      }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GbfsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 60.seconds,
    version = "2.3",
    data =
      GeofencingZones(
        geofencingZones =
          buildFeatureCollection {
            addFeature(
              geometry =
                buildMultiPolygon {
                  addPolygon {
                    addRing {
                      add(-122.578067, 45.562982)
                      add(-122.661838, 45.562741)
                      add(-122.661151, 45.504542)
                      add(-122.578926, 45.5046625)
                      add(-122.578067, 45.562982)
                    }
                  }
                  addPolygon {
                    addRing {
                      add(-122.65068, 45.548197)
                      add(-122.650852, 45.534731)
                      add(-122.630939, 45.535212)
                      add(-122.630424, 45.548197)
                      add(-122.65068, 45.548197)
                    }
                  }
                },
              properties =
                GeofencingZone(
                  name = "NE 24th/NE Knott",
                  start = Instant.fromEpochSeconds(1593878400),
                  end = Instant.fromEpochSeconds(1593907260),
                  rules =
                    listOf(
                      GeofencingZoneRule(
                        vehicleTypeId = listOf("moped1", "car1"),
                        rideAllowed = false,
                        rideThroughAllowed = true,
                        maximumSpeedKph = 10,
                        stationParking = true,
                      )
                    ),
                ),
            )
          }
      ),
  )

class GeofencingZonesTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<GeofencingZones>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
