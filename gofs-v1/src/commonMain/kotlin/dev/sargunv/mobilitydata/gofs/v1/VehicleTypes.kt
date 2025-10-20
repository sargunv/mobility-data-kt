package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Id
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
public enum class WheelchairBoarding {
  /** The vehicle is accessible to riders with wheelchairs. */
  @SerialName("boarding_accessible") Accessible,
  /** The vehicle is not accessible to riders with wheelchairs. */
  @SerialName("boarding_inaccessible") Inaccessible,
  /** The vehicle is accessible to riders with wheelchairs with assistance. */
  @SerialName("boarding_accessible_with_assistance") AccessibleWithAssistance,
}
