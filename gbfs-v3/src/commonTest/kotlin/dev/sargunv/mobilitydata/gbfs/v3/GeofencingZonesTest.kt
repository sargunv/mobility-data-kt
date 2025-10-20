package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
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
      "last_updated": "2023-07-17T13:34:13+02:00",
      "ttl": 60,
      "version": "3.0",
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
                          "name": [
                            {
                              "text": "NE 24th/NE Knott",
                              "language": "en"
                            }
                          ],
                          "start": "2020-07-04T12:00:00+02:00",
                          "end": "2020-07-04T20:00:00+02:00",
                          "rules": [
                              {
                                  "vehicle_type_ids": ["moped1", "car1"],
                                  "ride_start_allowed": false,
                                  "ride_end_allowed": false,
                                  "ride_through_allowed": true,
                                  "maximum_speed_kph": 10,
                                  "station_parking": true
                              }
                          ]
                      }
                  }
              ]
          },
          "global_rules": [
            {
              "ride_start_allowed": false,
              "ride_end_allowed": false,
              "ride_through_allowed": true
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
    ttl = 60.seconds,
    version = "3.0",
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
                  name = mapOf("en" to "NE 24th/NE Knott"),
                  start = Timestamp.parse("2020-07-04T12:00:00+02:00"),
                  end = Timestamp.parse("2020-07-04T20:00:00+02:00"),
                  rules =
                    listOf(
                      GeofencingZoneRule(
                        vehicleTypeIds = listOf("moped1", "car1"),
                        rideStartAllowed = false,
                        rideEndAllowed = false,
                        rideThroughAllowed = true,
                        maximumSpeedKph = 10,
                        stationParking = true,
                      )
                    ),
                ),
            )
          },
        globalRules =
          listOf(
            GeofencingZoneRule(
              rideStartAllowed = false,
              rideEndAllowed = false,
              rideThroughAllowed = true,
            )
          ),
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
