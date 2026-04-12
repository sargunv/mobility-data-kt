package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Contains operating rules enabling on-demand services between zones or within the same zone.
 *
 * This file contains operating rules according to time windows and calendars. At least one
 * operating rule must be defined. If start_pickup_window, end_pickup_window, and end_dropoff_window
 * are not provided, it is assumed that the on-demand service is available at any hours of the day.
 */
@Serializable
public data class OperatingRules(
  /** Array that contains one object per operating rule. */
  @SerialName("operating_rules") public val operatingRules: List<OperatingRule>
) : GofsFeedData, List<OperatingRule> by operatingRules

/** A single operating rule defining when and where on-demand services are available. */
@Serializable
public data class OperatingRule(
  /** ID from a zone defined in zones.json representing the boarding zone for the current rule. */
  @SerialName("from_zone_id") public val fromZoneId: String,
  /**
   * ID from a zone defined in zones.json representing the alighting zone for the current rule.
   * from_zone_id and to_zone_id may reference the same zone.
   */
  @SerialName("to_zone_id") public val toZoneId: String,
  /**
   * Time at which the pickup starts being available in from_zone_id defined in this array. If
   * start_pickup_window is provided, either end_pickup_window or end_dropoff_window must also be
   * provided.
   */
  @SerialName("start_pickup_window") public val startPickupWindow: ServiceTime? = null,
  /**
   * Time at which the pickup stops being available in from_zone_id defined in this array. If
   * end_pickup_window is provided, start_pickup_window must be provided.
   */
  @SerialName("end_pickup_window") public val endPickupWindow: ServiceTime? = null,
  /**
   * Time at which the drop off stops being available in to_zone_id defined in this array. Some
   * services differ the end of the pickup time and the end of the drop off time (e.g.: The pickup
   * time ends at 10PM in the origin zone but it is still possible to be dropped off in the
   * destination zone until 10:30PM). If end_dropoff_window is provided, start_pickup_window must be
   * provided.
   */
  @SerialName("end_dropoff_window") public val endDropoffWindow: ServiceTime? = null,
  /**
   * Array of calendar IDs from calendars.json defining the dates and days when the pickup and drop
   * off occur.
   */
  public val calendars: List<String>,
  /**
   * ID from a service brand defined in service_brands.json. If this field is not provided, the
   * operating rule applies to every service brand defined in service_brands.json.
   */
  @SerialName("brand_id") public val brandId: String? = null,
  /** Array of vehicle types used for delivering the on-demand service. */
  @SerialName("vehicle_type_id") public val vehicleTypeIds: List<String>,
  /** Unique identifier of a fare. Used to determine the price of the on-demand service. */
  @SerialName("fare_id") public val fareId: String? = null,
)
