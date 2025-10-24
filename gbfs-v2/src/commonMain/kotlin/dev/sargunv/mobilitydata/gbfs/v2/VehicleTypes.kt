package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.utils.CountryCode
import dev.sargunv.mobilitydata.utils.ExtendedLocalDate
import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.WholeMinutes
import kotlin.jvm.JvmInline
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
  @SerialName("vehicle_type_id") public val vehicleTypeId: String,

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
  @SerialName("vehicle_image") public val vehicleImage: Url? = null,

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
  @SerialName("default_reserve_time") public val defaultReserveTime: WholeMinutes? = null,

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
  @SerialName("default_pricing_plan_id") public val defaultPricingPlanId: String? = null,

  /**
   * Array of all pricing plan IDs that are applied to this vehicle type.
   *
   * This array SHOULD be published when there are multiple pricing plans defined in
   * system_pricing_plans.json that apply to a single vehicle type.
   */
  @SerialName("pricing_plan_ids") public val pricingPlanIds: List<String>? = null,
)

/** The vehicle's general form factor. */
@Serializable
@JvmInline
public value class VehicleFormFactor(
  /** The string value representing the vehicle form factor. */
  public val value: String
) {
  /** Companion object containing predefined vehicle form factor constants. */
  public companion object {
    /** Standard bicycle. */
    public val Bicycle: VehicleFormFactor = VehicleFormFactor("bicycle")

    /** Cargo bicycle. */
    public val CargoBicycle: VehicleFormFactor = VehicleFormFactor("cargo_bicycle")

    /** Car. */
    public val Car: VehicleFormFactor = VehicleFormFactor("car")

    /** Moped. */
    public val Moped: VehicleFormFactor = VehicleFormFactor("moped")

    /** Scooter (will be deprecated in v3.0). */
    public val Scooter: VehicleFormFactor = VehicleFormFactor("scooter")

    /** Standing kick scooter. */
    public val ScooterStanding: VehicleFormFactor = VehicleFormFactor("scooter_standing")

    /** Kick scooter with a seat (not to be confused with moped). */
    public val ScooterSeated: VehicleFormFactor = VehicleFormFactor("scooter_seated")

    /** Other type of vehicle. */
    public val Other: VehicleFormFactor = VehicleFormFactor("other")
  }
}

/** The primary propulsion type of the vehicle. */
@Serializable
@JvmInline
public value class VehiclePropulsionType(
  /** The string value representing the vehicle propulsion type. */
  public val value: String
) {
  /** Companion object containing predefined vehicle propulsion type constants. */
  public companion object {
    /** Pedal or foot propulsion. */
    public val Human: VehiclePropulsionType = VehiclePropulsionType("human")

    /**
     * Provides electric motor assist only in combination with human propulsion - no throttle mode.
     */
    public val ElectricAssist: VehiclePropulsionType = VehiclePropulsionType("electric_assist")

    /** Powered by battery-powered electric motor with throttle mode. */
    public val Electric: VehiclePropulsionType = VehiclePropulsionType("electric")

    /** Powered by gasoline combustion engine. */
    public val Combustion: VehiclePropulsionType = VehiclePropulsionType("combustion")

    /** Powered by diesel combustion engine. */
    public val CombustionDiesel: VehiclePropulsionType = VehiclePropulsionType("combustion_diesel")

    /** Powered by combined combustion engine and battery-powered motor. */
    public val Hybrid: VehiclePropulsionType = VehiclePropulsionType("hybrid")

    /** Powered by combined combustion engine and battery-powered motor with plug-in charging. */
    public val PlugInHybrid: VehiclePropulsionType = VehiclePropulsionType("plug_in_hybrid")

    /** Powered by hydrogen fuel cell powered electric motor. */
    public val HydrogenFuelCell: VehiclePropulsionType = VehiclePropulsionType("hydrogen_fuel_cell")
  }
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
@JvmInline
public value class VehicleAccessory(
  /** The string value representing the vehicle accessory. */
  public val value: String
) {
  /** Companion object containing predefined vehicle accessory constants. */
  public companion object {
    /** Vehicle has air conditioning. */
    public val AirConditioning: VehicleAccessory = VehicleAccessory("air_conditioning")

    /** Automatic gear switch. */
    public val Automatic: VehicleAccessory = VehicleAccessory("automatic")

    /** Manual gear switch. */
    public val Manual: VehicleAccessory = VehicleAccessory("manual")

    /** Vehicle is convertible. */
    public val Convertible: VehicleAccessory = VehicleAccessory("convertible")

    /** Vehicle has a cruise control system ("Tempomat"). */
    public val CruiseControl: VehicleAccessory = VehicleAccessory("cruise_control")

    /** Vehicle has 2 doors. */
    public val Doors2: VehicleAccessory = VehicleAccessory("doors_2")

    /** Vehicle has 3 doors. */
    public val Doors3: VehicleAccessory = VehicleAccessory("doors_3")

    /** Vehicle has 4 doors. */
    public val Doors4: VehicleAccessory = VehicleAccessory("doors_4")

    /** Vehicle has 5 doors. */
    public val Doors5: VehicleAccessory = VehicleAccessory("doors_5")

    /** Vehicle has a built-in navigation system. */
    public val Navigation: VehicleAccessory = VehicleAccessory("navigation")
  }
}

/** The conditions for returning the vehicle at the end of the rental. */
@Serializable
@JvmInline
public value class VehicleReturnConstraint(
  /** The string value representing the vehicle return constraint. */
  public val value: String
) {
  /** Companion object containing predefined vehicle return constraint constants. */
  public companion object {
    /**
     * The vehicle can be returned anywhere permitted within the service area.
     *
     * Note that this field is subject to rules in geofencing_zones.json if defined.
     */
    public val FreeFloating: VehicleReturnConstraint = VehicleReturnConstraint("free_floating")

    /**
     * The vehicle has to be returned to the same station from which it was initially rented.
     *
     * Note that a specific station can be assigned to the vehicle in free_bike_status.json using
     * home_station.
     */
    public val RoundtripStation: VehicleReturnConstraint =
      VehicleReturnConstraint("roundtrip_station")

    /** The vehicle has to be returned to any station within the service area. */
    public val AnyStation: VehicleReturnConstraint = VehicleReturnConstraint("any_station")

    /**
     * The vehicle can be returned to any station, or anywhere else permitted within the service
     * area.
     *
     * Note that the vehicle is subject to rules in geofencing_zones.json if defined.
     */
    public val Hybrid: VehicleReturnConstraint = VehicleReturnConstraint("hybrid")
  }
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
  @SerialName("icon_last_modified") public val iconLastModified: ExtendedLocalDate,
)
