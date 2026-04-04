package dev.sargunv.mobilitydata.gofs.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.maplibre.spatialk.geojson.dsl.addFeature
import org.maplibre.spatialk.geojson.dsl.addRing
import org.maplibre.spatialk.geojson.dsl.buildFeatureCollection
import org.maplibre.spatialk.geojson.dsl.buildPolygon

private val jsonContent = // language=JSON
  """
  {
    "last_updated": 1609866247,
    "ttl": 0,
    "version": "1.0",
    "data": {
      "zones": {
        "type": "FeatureCollection",
        "features": [
          {
            "type": "Feature",
            "properties": {
              "name": "Montréal Area"
            },
            "geometry": {
              "type": "Polygon",
              "coordinates": [
                [
                  [
                    -74.1,
                    45.35
                  ],
                  [
                    -73.3,
                    45.35
                  ],
                  [
                    -73.3,
                    45.75
                  ],
                  [
                    -74.1,
                    45.75
                  ],
                  [
                    -74.1,
                    45.35
                  ]
                ],
                [
                  [
                    -73.6,
                    45.55
                  ],
                  [
                    -73.6,
                    45.65
                  ],
                  [
                   -73.5,
                    45.65
                  ],
                  [
                    -73.5,
                    45.55
                  ],
                  [
                    -73.6,
                    45.55
                  ]
                ]
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
  GofsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1609866247),
    ttl = 0.seconds,
    version = "1.0",
    data =
      Zones(
        zones =
          buildFeatureCollection {
            addFeature(
              geometry =
                buildPolygon {
                  addRing {
                    add(-74.10, 45.35)
                    add(-73.30, 45.35)
                    add(-73.30, 45.75)
                    add(-74.10, 45.75)
                    add(-74.10, 45.35)
                  }
                  addRing {
                    add(-73.60, 45.55)
                    add(-73.60, 45.65)
                    add(-73.50, 45.65)
                    add(-73.50, 45.55)
                    add(-73.60, 45.55)
                  }
                },
              properties = Zone(name = "Montréal Area"),
            )
          }
      ),
  )

class ZonesTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<Zones>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
