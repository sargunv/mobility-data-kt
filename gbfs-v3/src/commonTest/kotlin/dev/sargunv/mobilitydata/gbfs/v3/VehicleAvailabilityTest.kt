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

private val jsonContent = // language=JSON
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl": 0,
    "version": "3.1-RC2",
    "data": {
      "vehicles": [
        {
          "vehicle_id": "45bd3fb7-a2d5-4def-9de1-c645844ba962",
          "vehicle_type_id": "abc123",
          "station_id": "station1",
          "pricing_plan_id": "plan3",
          "vehicle_equipment": [
            "child_seat_a"
          ],
          "availabilities": [
            {
              "from": "2024-12-24T08:15:00Z",
              "until": "2024-12-24T09:15:00Z"
            },
            {
              "from": "2024-12-25T10:30:00Z"
            }
          ]
        },
        {
          "vehicle_id": "987fd100-b822-4347-86a4-b3eef8ca8b53",
          "vehicle_type_id": "def456",
          "station_id": "86",
          "availabilities": [
            {
              "from": "2024-12-24T08:45:00Z"
            }
          ]
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalMobilityDataApi::class, ExperimentalTime::class)
private val expectedResponse =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 0.seconds,
    version = "3.1-RC2",
    data =
      VehicleAvailability(
        vehicles =
          listOf(
            VehicleAvailabilityEntry(
              vehicleId = "45bd3fb7-a2d5-4def-9de1-c645844ba962",
              vehicleTypeId = "abc123",
              stationId = "station1",
              pricingPlanId = "plan3",
              vehicleEquipment = listOf(VehicleEquipment.ChildSeatA),
              availabilities =
                listOf(
                  VehicleAvailabilitySlot(
                    from = Timestamp.parse("2024-12-24T08:15:00Z"),
                    until = Timestamp.parse("2024-12-24T09:15:00Z"),
                  ),
                  VehicleAvailabilitySlot(from = Timestamp.parse("2024-12-25T10:30:00Z")),
                ),
            ),
            VehicleAvailabilityEntry(
              vehicleId = "987fd100-b822-4347-86a4-b3eef8ca8b53",
              vehicleTypeId = "def456",
              stationId = "86",
              availabilities =
                listOf(VehicleAvailabilitySlot(from = Timestamp.parse("2024-12-24T08:45:00Z"))),
            ),
          )
      ),
  )

@OptIn(ExperimentalMobilityDataApi::class)
class VehicleAvailabilityTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<VehicleAvailability>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
