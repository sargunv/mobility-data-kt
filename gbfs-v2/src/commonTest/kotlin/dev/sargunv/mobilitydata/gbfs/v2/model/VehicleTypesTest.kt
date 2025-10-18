package dev.sargunv.mobilitydata.gbfs.v2.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
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
          "vehicle_types": [
              {
                  "vehicle_type_id": "abc123",
                  "form_factor": "bicycle",
                  "propulsion_type": "human",
                  "name": "Example Basic Bike",
                  "wheel_count": 2,
                  "default_reserve_time": 30,
                  "return_constraint": "any_station",
                  "vehicle_assets": {
                      "icon_url": "https://www.example.com/assets/icon_bicycle.svg",
                      "icon_url_dark": "https://www.example.com/assets/icon_bicycle_dark.svg",
                      "icon_last_modified": "2021-06-15"
                  },
                  "default_pricing_plan_id": "bike_plan_1",
                  "pricing_plan_ids": [
                      "bike_plan_1",
                      "bike_plan_2",
                      "bike_plan_3"
                  ]
              },
              {
                  "vehicle_type_id": "cargo123",
                  "form_factor": "cargo_bicycle",
                  "propulsion_type": "human",
                  "name": "Example Cargo Bike",
                  "wheel_count": 3,
                  "default_reserve_time": 30,
                  "return_constraint": "roundtrip_station",
                  "vehicle_assets": {
                      "icon_url": "https://www.example.com/assets/icon_cargobicycle.svg",
                      "icon_url_dark": "https://www.example.com/assets/icon_cargobicycle_dark.svg",
                      "icon_last_modified": "2021-06-15"
                  },
                  "default_pricing_plan_id": "cargo_plan_1",
                  "pricing_plan_ids": [
                      "cargo_plan_1",
                      "cargo_plan_2",
                      "cargo_plan_3"
                  ]
              },
              {
                  "vehicle_type_id": "def456",
                  "form_factor": "scooter_standing",
                  "propulsion_type": "electric",
                  "name": "Example E-scooter V2",
                  "wheel_count": 2,
                  "max_permitted_speed": 25,
                  "rated_power": 350,
                  "default_reserve_time": 30,
                  "max_range_meters": 12345.1,
                  "return_constraint": "free_floating",
                  "vehicle_assets": {
                      "icon_url": "https://www.example.com/assets/icon_escooter.svg",
                      "icon_url_dark": "https://www.example.com/assets/icon_escooter_dark.svg",
                      "icon_last_modified": "2021-06-15"
                  },
                  "default_pricing_plan_id": "scooter_plan_1"
              },
              {
                  "vehicle_type_id": "car1",
                  "form_factor": "car",
                  "rider_capacity": 5,
                  "cargo_volume_capacity": 200,
                  "propulsion_type": "combustion_diesel",
                  "eco_label": [
                      {
                          "country_code": "FR",
                          "eco_sticker": "critair_1"
                      },
                      {
                          "country_code": "DE",
                          "eco_sticker": "euro_2"
                      }
                  ],
                  "name": "Four-door Sedan",
                  "wheel_count": 4,
                  "default_reserve_time": 0,
                  "max_range_meters": 523992.1,
                  "return_constraint": "roundtrip_station",
                  "vehicle_accessories": [
                      "doors_4",
                      "automatic",
                      "cruise_control"
                  ],
                  "g_CO2_km": 120,
                  "vehicle_image": "https://www.example.com/assets/renault-clio.jpg",
                  "make": "Renault",
                  "model": "Clio",
                  "color": "white",
                  "vehicle_assets": {
                      "icon_url": "https://www.example.com/assets/icon_car.svg",
                      "icon_url_dark": "https://www.example.com/assets/icon_car_dark.svg",
                      "icon_last_modified": "2021-06-15"
                  },
                  "default_pricing_plan_id": "car_plan_1"
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
      VehicleTypes(
        vehicleTypes =
          listOf(
            VehicleType(
              vehicleTypeId = "abc123",
              formFactor = VehicleFormFactor.Bicycle,
              propulsionType = VehiclePropulsionType.Human,
              name = "Example Basic Bike",
              wheelCount = 2,
              defaultReserveTime = 30.minutes,
              returnConstraint = VehicleReturnConstraint.AnyStation,
              vehicleAssets =
                VehicleAssets(
                  iconUrl = "https://www.example.com/assets/icon_bicycle.svg",
                  iconUrlDark = "https://www.example.com/assets/icon_bicycle_dark.svg",
                  iconLastModified = LocalDate(2021, 6, 15),
                ),
              defaultPricingPlanId = "bike_plan_1",
              pricingPlanIds = listOf("bike_plan_1", "bike_plan_2", "bike_plan_3"),
            ),
            VehicleType(
              vehicleTypeId = "cargo123",
              formFactor = VehicleFormFactor.CargoBicycle,
              propulsionType = VehiclePropulsionType.Human,
              name = "Example Cargo Bike",
              wheelCount = 3,
              defaultReserveTime = 30.minutes,
              returnConstraint = VehicleReturnConstraint.RoundtripStation,
              vehicleAssets =
                VehicleAssets(
                  iconUrl = "https://www.example.com/assets/icon_cargobicycle.svg",
                  iconUrlDark = "https://www.example.com/assets/icon_cargobicycle_dark.svg",
                  iconLastModified = LocalDate(2021, 6, 15),
                ),
              defaultPricingPlanId = "cargo_plan_1",
              pricingPlanIds = listOf("cargo_plan_1", "cargo_plan_2", "cargo_plan_3"),
            ),
            VehicleType(
              vehicleTypeId = "def456",
              formFactor = VehicleFormFactor.ScooterStanding,
              propulsionType = VehiclePropulsionType.Electric,
              name = "Example E-scooter V2",
              wheelCount = 2,
              maxPermittedSpeed = 25,
              ratedPower = 350,
              defaultReserveTime = 30.minutes,
              maxRangeMeters = 12345.1,
              returnConstraint = VehicleReturnConstraint.FreeFloating,
              vehicleAssets =
                VehicleAssets(
                  iconUrl = "https://www.example.com/assets/icon_escooter.svg",
                  iconUrlDark = "https://www.example.com/assets/icon_escooter_dark.svg",
                  iconLastModified = LocalDate(2021, 6, 15),
                ),
              defaultPricingPlanId = "scooter_plan_1",
            ),
            VehicleType(
              vehicleTypeId = "car1",
              formFactor = VehicleFormFactor.Car,
              riderCapacity = 5,
              cargoVolumeCapacity = 200,
              propulsionType = VehiclePropulsionType.CombustionDiesel,
              ecoLabel = listOf(EcoLabel("FR", "critair_1"), EcoLabel("DE", "euro_2")),
              name = "Four-door Sedan",
              wheelCount = 4,
              defaultReserveTime = 0.minutes,
              maxRangeMeters = 523992.1,
              returnConstraint = VehicleReturnConstraint.RoundtripStation,
              vehicleAccessories =
                listOf(
                  VehicleAccessory.Doors4,
                  VehicleAccessory.Automatic,
                  VehicleAccessory.CruiseControl,
                ),
              gCO2km = 120,
              vehicleImage = "https://www.example.com/assets/renault-clio.jpg",
              make = "Renault",
              model = "Clio",
              color = "white",
              vehicleAssets =
                VehicleAssets(
                  iconUrl = "https://www.example.com/assets/icon_car.svg",
                  iconUrlDark = "https://www.example.com/assets/icon_car_dark.svg",
                  iconLastModified = LocalDate(2021, 6, 15),
                ),
              defaultPricingPlanId = "car_plan_1",
            ),
          )
      ),
  )

class VehicleTypesTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GbfsJson.decodeFromString<GbfsFeedResponse<VehicleTypes>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
