package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class VehicleTypes(
  @SerialName("vehicle_types") public val vehicleTypes: List<VehicleType>
) : GofsFeedData, List<VehicleType> by vehicleTypes

@Serializable
public data class VehicleType(
  @SerialName("vehicle_type_id") public val vehicleTypeId: Id<VehicleType>,
  @SerialName("max_capacity") public val maxCapacity: Int? = null,
  @SerialName("wheelchair_boarding") public val wheelchairBoarding: WheelchairBoarding? = null,
)

@Serializable
public enum class WheelchairBoarding {
  @SerialName("boarding_accessible") Accessible,
  @SerialName("boarding_inaccessible") Inaccessible,
  @SerialName("boarding_accessible_with_assistance") AccessibleWithAssistance,
}
