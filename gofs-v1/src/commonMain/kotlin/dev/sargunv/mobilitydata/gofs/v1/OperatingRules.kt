package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class OperatingRules(
  @SerialName("operating_rules") public val operatingRules: List<OperatingRule>
) : GofsFeedData, List<OperatingRule> by operatingRules

@Serializable
public data class OperatingRule(
  @SerialName("from_zone_id") public val fromZoneId: Id<Zone>,
  @SerialName("to_zone_id") public val toZoneId: Id<Zone>,
  @SerialName("start_pickup_window") public val startPickupWindow: ServiceTime? = null,
  @SerialName("end_pickup_window") public val endPickupWindow: ServiceTime? = null,
  @SerialName("end_dropoff_window") public val endDropoffWindow: ServiceTime? = null,
  public val calendars: List<Id<Calendar>>,
  @SerialName("brand_id") public val brandId: Id<Brand>? = null,
  @SerialName("vehicle_type_id") public val vehicleTypeIds: List<Id<VehicleType>>? = null,
  @SerialName("fare_id") public val fareId: Id<Fare>? = null,
)
