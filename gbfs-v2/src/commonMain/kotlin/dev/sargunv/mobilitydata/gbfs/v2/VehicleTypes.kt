package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.gbfs.v2.serialization.WholeMinutesSerializer
import kotlin.time.Duration
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the types of vehicles available in the system.
 *
 * REQUIRED of systems that include information about vehicle types in the free_bike_status.json
 * file. This file SHOULD be published by systems offering multiple vehicle types for rental.
 */
@Serializable
public data class VehicleTypes(
  /** Array that contains one object per vehicle type in the system. */
  @SerialName("vehicle_types") public val vehicleTypes: List<VehicleType>
) : GbfsFeedData, List<VehicleType> by vehicleTypes

/** Information about a vehicle type available in the system. */
@Serializable
public data class VehicleType(
  /** Unique identifier of a vehicle type. */
  @SerialName("vehicle_type_id") public val vehicleTypeId: Id<VehicleType>,

  /** The vehicle's general form factor. */
  @SerialName("form_factor") public val formFactor: VehicleFormFactor,

  /** The number of riders (driver included) the vehicle can legally accommodate. */
  @SerialName("rider_capacity") public val riderCapacity: Int? = null,

  /**
   * Cargo volume available in the vehicle, expressed in liters.
   *
   * For cars, it corresponds to the space between the boot floor to the rear shelf in the trunk.
   */
  @SerialName("cargo_volume_capacity") public val cargoVolumeCapacity: Int? = null,

  /** The capacity of the vehicle cargo space (excluding passengers), expressed in kilograms. */
  @SerialName("cargo_load_capacity") public val cargoLoadCapacity: Int? = null,

  /** The primary propulsion type of the vehicle. */
  @SerialName("propulsion_type") public val propulsionType: VehiclePropulsionType,

  /**
   * Vehicle air quality certificate.
   *
   * Official anti-pollution certificate, based on the information on the vehicle's registration
   * certificate, attesting to its level of pollutant emissions.
   */
  @SerialName("eco_label") public val ecoLabel: List<EcoLabel>? = null,

  /**
   * The furthest distance in meters that the vehicle can travel without recharging or refueling.
   *
   * REQUIRED if the vehicle has a motor. This represents the maximum amount of energy potential
   * (for example, a full battery or full tank of gas).
   */
  @SerialName("max_range_meters") public val maxRangeMeters: Double? = null,

  /** The public name of this vehicle type. */
  public val name: String? = null,

  /**
   * Description of accessories available in the vehicle.
   *
   * These accessories are part of the vehicle and are not supposed to change frequently.
   */
  @SerialName("vehicle_accessories") public val vehicleAccessories: List<VehicleAccessory>? = null,

  /** Maximum quantity of CO2, in grams, emitted per kilometer, according to the WLTP. */
  @SerialName("g_CO2_km") public val gCO2km: Int? = null,

  /**
   * URL to an image that would assist the user in identifying the vehicle.
   *
   * Allowed formats: JPEG, PNG.
   */
  @SerialName("vehicle_image") public val vehicleImage: String? = null,

  /** The name of the vehicle manufacturer. */
  public val make: String? = null,

  /** The name of the vehicle model. */
  public val model: String? = null,

  /** The color of the vehicle. */
  public val color: String? = null,

  /** Number of wheels this vehicle type has. */
  @SerialName("wheel_count") public val wheelCount: Int? = null,

  /**
   * The maximum speed in kilometers per hour this vehicle is permitted to reach in accordance with
   * local permit and regulations.
   */
  @SerialName("max_permitted_speed") public val maxPermittedSpeed: Int? = null,

  /** The rated power of the motor for this vehicle type in watts. */
  @SerialName("rated_power") public val ratedPower: Int? = null,

  /**
   * Maximum time that a vehicle can be reserved before a rental begins.
   *
   * When a vehicle is reserved by a user, the vehicle remains locked until the rental begins. If
   * the value of default_reserve_time elapses without a rental beginning, the vehicle status MUST
   * change to is_reserved = false. If set to 0, the vehicle type cannot be reserved.
   */
  @Serializable(with = WholeMinutesSerializer::class)
  @SerialName("default_reserve_time")
  public val defaultReserveTime: Duration? = null,

  /** The conditions for returning the vehicle at the end of the rental. */
  @SerialName("return_constraint") public val returnConstraint: VehicleReturnConstraint? = null,

  /** Icon images and metadata for representing this vehicle type. */
  @SerialName("vehicle_assets") public val vehicleAssets: VehicleAssets? = null,

  /**
   * A plan_id that identifies a default pricing plan for this vehicle to be used by trip planning
   * applications.
   *
   * This default pricing plan is superseded by pricing_plan_id when pricing_plan_id is defined in
   * free_bike_status.json.
   */
  @SerialName("default_pricing_plan_id") public val defaultPricingPlanId: Id<PricingPlan>? = null,

  /**
   * Array of all pricing plan IDs that are applied to this vehicle type.
   *
   * This array SHOULD be published when there are multiple pricing plans defined in
   * system_pricing_plans.json that apply to a single vehicle type.
   */
  @SerialName("pricing_plan_ids") public val pricingPlanIds: List<Id<PricingPlan>>? = null,
)

/** The vehicle's general form factor. */
@Serializable
public enum class VehicleFormFactor {
  /** Standard bicycle. */
  @SerialName("bicycle") Bicycle,

  /** Cargo bicycle. */
  @SerialName("cargo_bicycle") CargoBicycle,

  /** Car. */
  @SerialName("car") Car,

  /** Moped. */
  @SerialName("moped") Moped,

  /** Scooter (will be deprecated in v3.0). */
  @SerialName("scooter") Scooter,

  /** Standing kick scooter. */
  @SerialName("scooter_standing") ScooterStanding,

  /** Kick scooter with a seat (not to be confused with moped). */
  @SerialName("scooter_seated") ScooterSeated,

  /** Other type of vehicle. */
  @SerialName("other") Other,
}

/** The primary propulsion type of the vehicle. */
@Serializable
public enum class VehiclePropulsionType {
  /** Pedal or foot propulsion. */
  @SerialName("human") Human,

  /**
   * Provides electric motor assist only in combination with human propulsion - no throttle mode.
   */
  @SerialName("electric_assist") ElectricAssist,

  /** Powered by battery-powered electric motor with throttle mode. */
  @SerialName("electric") Electric,

  /** Powered by gasoline combustion engine. */
  @SerialName("combustion") Combustion,

  /** Powered by diesel combustion engine. */
  @SerialName("combustion_diesel") CombustionDiesel,

  /** Powered by combined combustion engine and battery-powered motor. */
  @SerialName("hybrid") Hybrid,

  /** Powered by combined combustion engine and battery-powered motor with plug-in charging. */
  @SerialName("plug_in_hybrid") PlugInHybrid,

  /** Powered by hydrogen fuel cell powered electric motor. */
  @SerialName("hydrogen_fuel_cell") HydrogenFuelCell,
}

/** Vehicle air quality certificate for a specific country. */
@Serializable
public data class EcoLabel(
  /** Country where the eco_sticker applies. REQUIRED if eco_label is defined. */
  @SerialName("country_code") public val countryCode: CountryCode,

  /**
   * Name of the eco label.
   *
   * REQUIRED if eco_label is defined. The name must be written in lowercase, separated by an
   * underscore.
   */
  @SerialName("eco_sticker") public val ecoSticker: String,
)

/** Accessories available in the vehicle. */
@Serializable
public enum class VehicleAccessory {
  /** Vehicle has air conditioning. */
  @SerialName("air_conditioning") AirConditioning,

  /** Automatic gear switch. */
  @SerialName("automatic") Automatic,

  /** Manual gear switch. */
  @SerialName("manual") Manual,

  /** Vehicle is convertible. */
  @SerialName("convertible") Convertible,

  /** Vehicle has a cruise control system ("Tempomat"). */
  @SerialName("cruise_control") CruiseControl,

  /** Vehicle has 2 doors. */
  @SerialName("doors_2") Doors2,

  /** Vehicle has 3 doors. */
  @SerialName("doors_3") Doors3,

  /** Vehicle has 4 doors. */
  @SerialName("doors_4") Doors4,

  /** Vehicle has 5 doors. */
  @SerialName("doors_5") Doors5,

  /** Vehicle has a built-in navigation system. */
  @SerialName("navigation") Navigation,
}

/** The conditions for returning the vehicle at the end of the rental. */
@Serializable
public enum class VehicleReturnConstraint {
  /**
   * The vehicle can be returned anywhere permitted within the service area.
   *
   * Note that this field is subject to rules in geofencing_zones.json if defined.
   */
  @SerialName("free_floating") FreeFloating,

  /**
   * The vehicle has to be returned to the same station from which it was initially rented.
   *
   * Note that a specific station can be assigned to the vehicle in free_bike_status.json using
   * home_station.
   */
  @SerialName("roundtrip_station") RoundtripStation,

  /** The vehicle has to be returned to any station within the service area. */
  @SerialName("any_station") AnyStation,

  /**
   * The vehicle can be returned to any station, or anywhere else permitted within the service area.
   *
   * Note that the vehicle is subject to rules in geofencing_zones.json if defined.
   */
  @SerialName("hybrid") Hybrid,
}

/** Icon images and metadata for representing a vehicle type. */
@Serializable
public data class VehicleAssets(
  /**
   * A fully qualified URL pointing to the location of a graphic icon file.
   *
   * REQUIRED if vehicle_assets is defined. File MUST be in SVG V1.1 format and MUST be either
   * square or round.
   */
  @SerialName("icon_url") public val iconUrl: Url,

  /**
   * A fully qualified URL pointing to the location of a graphic icon file for use in dark mode.
   *
   * File MUST be in SVG V1.1 format and MUST be either square or round.
   */
  @SerialName("icon_url_dark") public val iconUrlDark: Url? = null,

  /**
   * Date that indicates the last time any included vehicle icon images were modified or updated.
   *
   * REQUIRED if vehicle_assets is defined.
   */
  @SerialName("icon_last_modified") public val iconLastModified: LocalDate,
)
