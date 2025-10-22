package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Times that a vehicle arrives at and departs from stops for each trip.
 *
 * This class represents a record in the stop_times.txt file.
 */
@Serializable
public data class StopTime(
  /** Identifies a trip. */
  @SerialName("trip_id") public val tripId: Id<Trip>,

  /** Arrival time at a specific stop for a specific trip on a route. */
  @SerialName("arrival_time") public val arrivalTime: ServiceTime? = null,

  /** Departure time from a specific stop for a specific trip on a route. */
  @SerialName("departure_time") public val departureTime: ServiceTime? = null,

  /** Identifies the serviced stop. */
  @SerialName("stop_id") public val stopId: Id<Stop>,

  /**
   * Identifies the serviced location group that indicates groups of stops where riders may request
   * pickup or drop off.
   */
  @SerialName("location_group_id") public val locationGroupId: Id<LocationGroup>? = null,

  /**
   * Identifies the GeoJSON location that corresponds to serviced zone where riders may request
   * pickup or drop off.
   */
  @SerialName("location_id") public val locationId: Id<Nothing>? = null,

  /** Order of stops for a particular trip. */
  @SerialName("stop_sequence") public val stopSequence: Int,

  /** Text that appears on signage identifying the trip's destination to riders. */
  @SerialName("stop_headsign") public val stopHeadsign: String? = null,

  /**
   * Time that on-demand service becomes available in a GeoJSON location, location group, or stop.
   */
  @SerialName("start_pickup_drop_off_window")
  public val startPickupDropOffWindow: ServiceTime? = null,

  /** Time that on-demand service ends in a GeoJSON location, location group, or stop. */
  @SerialName("end_pickup_drop_off_window") public val endPickupDropOffWindow: ServiceTime? = null,

  /** Indicates pickup method. */
  @SerialName("pickup_type") public val pickupType: PickupDropoff? = null,

  /** Indicates drop off method. */
  @SerialName("drop_off_type") public val dropOffType: PickupDropoff? = null,

  /** Indicates continuous pickup behavior. */
  @SerialName("continuous_pickup") public val continuousPickup: ContinuousPickupDropOff? = null,

  /** Indicates continuous drop off behavior. */
  @SerialName("continuous_drop_off") public val continuousDropOff: ContinuousPickupDropOff? = null,

  /** Distance traveled along the shape from the first stop to the stop in this record. */
  @SerialName("shape_dist_traveled") public val shapeDistTraveled: Double? = null,

  /** Indicates whether a rider can board or alight at this stop. */
  @SerialName("timepoint") public val timepoint: Timepoint? = null,

  /** Identifies the boarding booking rule at this stop time. */
  @SerialName("pickup_booking_rule_id") public val pickupBookingRuleId: Id<BookingRule>? = null,

  /** Identifies the alighting booking rule at this stop time. */
  @SerialName("drop_off_booking_rule_id") public val dropOffBookingRuleId: Id<BookingRule>? = null,
)

/** Indicates pickup or drop off method. */
@Serializable
@JvmInline
public value class PickupDropoff
private constructor(
  /** The integer value representing the pickup/drop off type. */
  public val value: Int
) {
  /** Companion object containing predefined pickup/drop off type constants. */
  public companion object Companion {
    /** Regularly scheduled pickup/drop off. */
    public val Regular: PickupDropoff = PickupDropoff(0)

    /** No pickup/drop off available. */
    public val None: PickupDropoff = PickupDropoff(1)

    /** Must phone agency to arrange pickup/drop off. */
    public val PhoneAgency: PickupDropoff = PickupDropoff(2)

    /** Must coordinate with driver to arrange pickup/drop off. */
    public val CoordinateWithDriver: PickupDropoff = PickupDropoff(3)
  }
}

/** Indicates whether a rider can board the transit vehicle at this stop. */
@Serializable
@JvmInline
public value class Timepoint
private constructor(
  /** The integer value representing the timepoint status. */
  public val value: Int
) {
  /** Companion object containing predefined timepoint constants. */
  public companion object {
    /** Times are considered approximate. */
    public val Approximate: Timepoint = Timepoint(0)

    /** Times are considered exact. */
    public val Exact: Timepoint = Timepoint(1)
  }
}
