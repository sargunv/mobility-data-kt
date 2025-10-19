package dev.sargunv.mobilitydata.gofs.v1

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
    "last_updated": 1609866247,
    "ttl": 0,
    "version": "1.0",
    "data": {
      "vehicle_types": [
        {
          "vehicle_type_id": "large_van",
          "max_capacity": 7,
          "wheelchair_boarding": "boarding_accessible"
        }
      ]
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
      VehicleTypes(
        vehicleTypes =
          listOf(
            VehicleType(
              vehicleTypeId = "large_van",
              maxCapacity = 7,
              wheelchairBoarding = WheelchairBoarding.Accessible,
            )
          )
      ),
  )

class VehicleTypesTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<VehicleTypes>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
