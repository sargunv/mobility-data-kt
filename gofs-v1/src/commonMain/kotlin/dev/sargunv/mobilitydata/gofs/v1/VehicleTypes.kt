package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Id
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines the vehicle types used for operating the on-demand services.
 *
 * This file is required if any vehicle types are referenced in operating_rules.json.
 */
@Serializable
public data class VehicleTypes(
  /** Array that contains one object per vehicle type. */
  @SerialName("vehicle_types") public val vehicleTypes: List<VehicleType>
) : GofsFeedData, List<VehicleType> by vehicleTypes

/** Information about a specific vehicle type used in on-demand services. */
@Serializable
public data class VehicleType(
  /** Unique identifier of the vehicle type. */
  @SerialName("vehicle_type_id") public val vehicleTypeId: Id<VehicleType>,
  /** Maximum number of riders that the vehicle can legally carry. */
  @SerialName("max_capacity") public val maxCapacity: Int? = null,
  /** Possibility for riders with a wheelchair to board the vehicle. */
  @SerialName("wheelchair_boarding") public val wheelchairBoarding: WheelchairBoarding? = null,
)

/** Wheelchair boarding accessibility options for a vehicle. */
@Serializable
@JvmInline
public value class WheelchairBoarding(
  /** The string value representing the wheelchair boarding option. */
  public val value: String
) {
  /** Companion object containing predefined wheelchair boarding constants. */
  public companion object {
    /** The vehicle is accessible to riders with wheelchairs. */
    public val Accessible: WheelchairBoarding = WheelchairBoarding("boarding_accessible")

    /** The vehicle is not accessible to riders with wheelchairs. */
    public val Inaccessible: WheelchairBoarding = WheelchairBoarding("boarding_inaccessible")

    /** The vehicle is accessible to riders with wheelchairs with assistance. */
    public val AccessibleWithAssistance: WheelchairBoarding =
      WheelchairBoarding("boarding_accessible_with_assistance")
  }
}
