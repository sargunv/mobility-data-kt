package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.toServiceTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalTime
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
      "operating_rules" : [
        {
          "from_zone_id": "zoneA",
          "to_zone_id": "zoneA",
          "start_pickup_window" : "06:00:00",
          "end_pickup_window": "09:00:00",
          "end_dropoff_window": "09:30:00",
          "calendars": ["weekend", "labor_day"],
          "brand_id": "large_ride",
          "vehicle_type_id": ["large_van"],
          "fare_id": "RegularPrice"
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
      OperatingRules(
        operatingRules =
          listOf(
            OperatingRule(
              fromZoneId = "zoneA",
              toZoneId = "zoneA",
              startPickupWindow = LocalTime.parse("06:00:00").toServiceTime(),
              endPickupWindow = LocalTime.parse("09:00:00").toServiceTime(),
              endDropoffWindow = LocalTime.parse("09:30:00").toServiceTime(),
              calendars = listOf("weekend", "labor_day"),
              brandId = "large_ride",
              vehicleTypeIds = listOf("large_van"),
              fareId = "RegularPrice",
            )
          )
      ),
  )

class OperatingRulesTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<OperatingRules>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
