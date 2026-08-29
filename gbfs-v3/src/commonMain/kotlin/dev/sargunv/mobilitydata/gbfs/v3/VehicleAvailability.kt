package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.ExperimentalMobilityDataApi
import dev.sargunv.mobilitydata.utils.Timestamp
import dev.sargunv.mobilitydata.utils.serialization.DatetimeSerializer
import kotlin.time.ExperimentalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the future availability of each vehicle.
 *
 * OPTIONAL for station based (docked) vehicles. Not supported for free floating (dockless)
 * vehicles. Useful for systems that allow vehicles to be reserved in advance. Data returned SHOULD
 * be as close to realtime as possible, but in no case should it be more than 5 minutes out-of-date.
 *
 * GBFS 3.1-RC.
 */
@ExperimentalMobilityDataApi
@Serializable
public data class VehicleAvailability(
  /** Array that contains one object per vehicle. */
  public val vehicles: List<VehicleAvailabilityEntry>
) : GbfsFeedData, List<VehicleAvailabilityEntry> by vehicles

/** A vehicle and the time slots during which it is available. */
@ExperimentalMobilityDataApi
@Serializable
public data class VehicleAvailabilityEntry(

  /**
   * Identifier of a vehicle.
   *
   * The vehicle_id identifier MUST be rotated to a random string after each trip to protect user
   * privacy. Use of persistent vehicle IDs poses a threat to user privacy. The vehicle_id
   * identifier SHOULD only be rotated once per trip. The vehicle_id SHOULD be the same as in
   * vehicle_status.json if that file is defined and the vehicle is currently available.
   */
  @SerialName("vehicle_id") public val vehicleId: String,

  /**
   * Unique identifier of a vehicle type as defined in vehicle_types.json.
   *
   * REQUIRED if the vehicle_types.json file is defined.
   */
  @SerialName("vehicle_type_id") public val vehicleTypeId: String? = null,

  /**
   * Identifier referencing the station_id field in station_information.json.
   *
   * The station where this vehicle is located when available.
   */
  @SerialName("station_id") public val stationId: String,

  /**
   * The plan_id of the pricing plan this vehicle is eligible for as described in
   * system_pricing_plans.json.
   *
   * If this field is defined it supersedes default_pricing_plan_id in vehicle_types.json. This
   * field SHOULD be used to override default_pricing_plan_id in vehicle_types.json to define
   * pricing plans for individual vehicles when necessary.
   */
  @SerialName("pricing_plan_id") public val pricingPlanId: String? = null,

  /**
   * List of vehicle equipment provided by the operator in addition to the accessories already
   * provided in the vehicle (field vehicle_accessories of vehicle_types.json) but subject to more
   * frequent updates.
   */
  @SerialName("vehicle_equipment") public val vehicleEquipment: List<VehicleEquipment>? = null,

  /** Array of time slots during which the specified vehicle is available. */
  public val availabilities: List<VehicleAvailabilitySlot>,
)

/** A time slot during which a vehicle is available. */
@ExperimentalMobilityDataApi
@OptIn(ExperimentalTime::class)
@Serializable
public data class VehicleAvailabilitySlot(
  /** Start date and time of the available time slot. */
  @Serializable(with = DatetimeSerializer::class) public val from: Timestamp,

  /**
   * End date and time of the available time slot.
   *
   * If this field is empty, the vehicle is available all the time from the date in the from field.
   */
  @Serializable(with = DatetimeSerializer::class) public val until: Timestamp? = null,
)
