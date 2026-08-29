package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.ExperimentalMobilityDataApi
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

private val jsonContent30 = // language=JSON
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl": 0,
    "version": "3.0",
    "data": {
      "datasets": [
        {
          "system_id": "example_berlin",
          "versions": [
            {
              "version": "2.0",
              "url": "https://berlin.example.com/gbfs/2/gbfs"
            },
            {
              "version": "3.0",
              "url": "https://berlin.example.com/gbfs/3/gbfs"
            }
          ]
        },
        {
          "system_id": "example_paris",
          "versions": [
            {
              "version": "2.0",
              "url": "https://paris.example.com/gbfs/2/gbfs"
            },
            {
              "version": "3.0",
              "url": "https://paris.example.com/gbfs/3/gbfs"
            }
          ]
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse30 =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 0.seconds,
    version = "3.0",
    data =
      DatasetManifest(
        datasets =
          listOf(
            Dataset(
              systemId = "example_berlin",
              versions =
                listOf(
                  VersionInfo(version = "2.0", url = "https://berlin.example.com/gbfs/2/gbfs"),
                  VersionInfo(version = "3.0", url = "https://berlin.example.com/gbfs/3/gbfs"),
                ),
            ),
            Dataset(
              systemId = "example_paris",
              versions =
                listOf(
                  VersionInfo(version = "2.0", url = "https://paris.example.com/gbfs/2/gbfs"),
                  VersionInfo(version = "3.0", url = "https://paris.example.com/gbfs/3/gbfs"),
                ),
            ),
          )
      ),
  )

private val jsonContent31 = // language=JSON
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl": 0,
    "version": "3.1-RC",
    "data": {
      "datasets": [
        {
          "system_id": "example_berlin",
          "versions": [
            {
              "version": "3.1-RC",
              "url": "https://berlin.example.com/gbfs/3.1-RC/gbfs"
            }
          ],
          "area": {
            "type": "MultiPolygon",
            "coordinates": [
              [
                [
                  [13.10821, 52.58563],
                  [13.29743, 52.67046],
                  [13.48451, 52.43458],
                  [13.08165, 52.38793],
                  [13.10821, 52.58563]
                ]
              ]
            ]
          },
          "country_code": "DE"
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalMobilityDataApi::class, ExperimentalTime::class)
private val expectedResponse31 =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 0.seconds,
    version = "3.1-RC",
    data =
      DatasetManifest(
        datasets =
          listOf(
            Dataset(
              systemId = "example_berlin",
              versions =
                listOf(
                  VersionInfo(
                    version = "3.1-RC",
                    url = "https://berlin.example.com/gbfs/3.1-RC/gbfs",
                  )
                ),
              area =
                MultiPolygon(
                  coordinates =
                    listOf(
                      listOf(
                        listOf(
                          Position(13.10821, 52.58563),
                          Position(13.29743, 52.67046),
                          Position(13.48451, 52.43458),
                          Position(13.08165, 52.38793),
                          Position(13.10821, 52.58563),
                        )
                      )
                    )
                ),
              countryCode = "DE",
            )
          )
      ),
  )

class DatasetManifestTest {
  @Test
  fun encode30() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent30)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse30)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode30() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<DatasetManifest>>(jsonContent30)
    assertEquals(expectedResponse30, decodedResponse)
  }

  @Test
  fun encode31() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent31)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse31)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode31() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<DatasetManifest>>(jsonContent31)
    assertEquals(expectedResponse31, decodedResponse)
  }
}
