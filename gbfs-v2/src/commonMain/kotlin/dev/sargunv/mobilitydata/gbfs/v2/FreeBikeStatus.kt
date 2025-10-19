package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.gbfs.v2.serialization.EpochSecondsSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes all vehicles that are not currently in active rental.
 *
 * REQUIRED for free floating (dockless) vehicles. OPTIONAL for station based (docked) vehicles.
 * Data returned SHOULD be as close to realtime as possible, but in no case should it be more than 5
 * minutes out-of-date.
 */
@Serializable
public data class FreeBikeStatus(
  /** Array that contains one object per vehicle that is currently not part of an active rental. */
  public val bikes: List<Bike>
) : GbfsFeedData, List<Bike> by bikes

/** Represents a vehicle that is currently not part of an active rental. */
@OptIn(ExperimentalTime::class)
@Serializable
public data class Bike(

  /**
   * Identifier of a vehicle.
   *
   * The bike_id identifier MUST be rotated to a random string after each trip to protect user
   * privacy. Use of persistent vehicle IDs poses a threat to user privacy. The bike_id identifier
   * SHOULD only be rotated once per trip.
   */
  @SerialName("bike_id") public val bikeId: Id<Bike>,

  /**
   * Latitude of the vehicle in decimal degrees.
   *
   * REQUIRED if station_id is not provided for this vehicle (free floating). This field SHOULD have
   * a precision of 6 decimal places (0.000001).
   */
  public val lat: Double? = null,

  /**
   * Longitude of the vehicle in decimal degrees.
   *
   * REQUIRED if station_id is not provided for this vehicle (free floating). This field SHOULD have
   * a precision of 6 decimal places (0.000001).
   */
  public val lon: Double? = null,

  /**
   * Is the vehicle currently reserved?
   * - `true` - Vehicle is currently reserved.
   * - `false` - Vehicle is not currently reserved.
   */
  @SerialName("is_reserved") public val isReserved: Boolean,

  /**
   * Is the vehicle currently disabled?
   * - `true` - Vehicle is currently disabled.
   * - `false` - Vehicle is not currently disabled.
   *
   * This field is used to indicate vehicles that are in the field but not available for rental.
   * This may be due to a mechanical issue, low battery, etc.
   */
  @SerialName("is_disabled") public val isDisabled: Boolean,

  /**
   * JSON object that contains rental URIs for Android, iOS, and web in the android, ios, and web
   * fields.
   */
  @SerialName("rental_uris") public val rentalUris: RentalUris? = null,

  /**
   * The vehicle_type_id of this vehicle, as described in vehicle_types.json.
   *
   * REQUIRED if the vehicle_types.json file is defined.
   */
  @SerialName("vehicle_type_id") public val vehicleTypeId: Id<VehicleType>? = null,

  /** The last time this vehicle reported its status to the operator's backend. */
  @SerialName("last_reported")
  @Serializable(with = EpochSecondsSerializer::class)
  public val lastReported: Instant,

  /**
   * The furthest distance in meters that the vehicle can travel with the vehicle's current charge
   * or fuel (without recharging or refueling).
   *
   * REQUIRED if the corresponding vehicle_type definition for this vehicle has a motor. Note that
   * in the case of carsharing, the given range is indicative and can be different from the one
   * displayed on the vehicle's dashboard.
   */
  @SerialName("current_range_meters") public val currentRangeMeters: Double? = null,

  /**
   * The current percentage, expressed from 0 to 1, of fuel or battery power remaining in the
   * vehicle.
   */
  @SerialName("current_fuel_percent") public val currentFuelPercent: Double? = null,

  /**
   * Identifier referencing the station_id field in station_information.json.
   *
   * REQUIRED if the vehicle is currently at a station and the vehicle_types.json file has been
   * defined.
   */
  @SerialName("station_id") public val stationId: Id<Station>? = null,

  /**
   * The station_id of the station this vehicle must be returned to as defined in
   * station_information.json.
   */
  @SerialName("home_station_id") public val homeStationId: Id<Station>? = null,

  /**
   * The plan_id of the pricing plan this vehicle is eligible for as described in
   * system_pricing_plans.json.
   *
   * If this field is defined it supersedes default_pricing_plan_id in vehicle_types.json. This
   * field SHOULD be used to override default_pricing_plan_id in vehicle_types.json to define
   * pricing plans for individual vehicles when necessary.
   */
  @SerialName("pricing_plan_id") public val pricingPlanId: Id<PricingPlan>? = null,

  /**
   * List of vehicle equipment provided by the operator in addition to the accessories already
   * provided in the vehicle (field vehicle_accessories of vehicle_types.json) but subject to more
   * frequent updates.
   */
  @SerialName("vehicle_equipment") public val vehicleEquipment: List<VehicleEquipment>? = null,

  /**
   * The date and time when any rental of the vehicle must be completed.
   *
   * The vehicle must be returned and made available for the next user by this time. If this field
   * is empty, it indicates that the vehicle is available indefinitely. This field SHOULD be
   * published by carsharing or other mobility systems where vehicles can be booked in advance for
   * future travel.
   */
  @SerialName("available_until") public val availableUntil: Instant? = null,
)

/**
 * Vehicle equipment provided by the operator in addition to the accessories already provided in the
 * vehicle.
 */
@Serializable
public enum class VehicleEquipment {

  /** Baby seat (0-10kg). */
  @SerialName("child_seat_a") ChildSeatA,

  /** Seat or seat extension for small children (9-18 kg). */
  @SerialName("child_seat_b") ChildSeatB,

  /** Seat or seat extension for older children (15-36 kg). */
  @SerialName("child_seat_c") ChildSeatC,

  /** Vehicle has tires for winter weather. */
  @SerialName("winter_tires") WinterTires,

  /** Snow chains. */
  @SerialName("snow_chains") SnowChains,
}
