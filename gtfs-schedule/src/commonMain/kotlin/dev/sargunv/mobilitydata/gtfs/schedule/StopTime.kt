package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Times that a vehicle arrives at and departs from stops for each trip.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#stop_timestxt)
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

  /** Order of stops for a particular trip. */
  @SerialName("stop_sequence") public val stopSequence: Int,

  /** Text that appears on signage identifying the trip's destination to riders. */
  @SerialName("stop_headsign") public val stopHeadsign: String? = null,

  /** Indicates pickup method. */
  @SerialName("pickup_type") public val pickupType: PickupDropOffType? = null,

  /** Indicates drop off method. */
  @SerialName("drop_off_type") public val dropOffType: PickupDropOffType? = null,

  /** Indicates continuous pickup behavior. */
  @SerialName("continuous_pickup") public val continuousPickup: ContinuousPickupDropOff? = null,

  /** Indicates continuous drop off behavior. */
  @SerialName("continuous_drop_off") public val continuousDropOff: ContinuousPickupDropOff? = null,

  /** Distance traveled along the shape from the first stop to the stop in this record. */
  @SerialName("shape_dist_traveled") public val shapeDistTraveled: Double? = null,

  /** Indicates whether a rider can board or alight at this stop. */
  @SerialName("timepoint") public val timepoint: Timepoint? = null,

  /** Buffer time in seconds before scheduled departure. */
  @SerialName("departure_buffer") public val departureBuffer: Int? = null,
)

/**
 * Indicates pickup or drop off method.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#stop_timestxt)
 */
@Serializable
@JvmInline
public value class PickupDropOffType(
  /** The integer value representing the pickup/drop off type. */
  public val value: Int
) {
  /** Companion object containing predefined pickup/drop off type constants. */
  public companion object {
    /** Regularly scheduled pickup/drop off. */
    public val Regular: PickupDropOffType = PickupDropOffType(0)

    /** No pickup/drop off available. */
    public val None: PickupDropOffType = PickupDropOffType(1)

    /** Must phone agency to arrange pickup/drop off. */
    public val PhoneAgency: PickupDropOffType = PickupDropOffType(2)

    /** Must coordinate with driver to arrange pickup/drop off. */
    public val CoordinateWithDriver: PickupDropOffType = PickupDropOffType(3)
  }
}

/**
 * Indicates whether a rider can board the transit vehicle at this stop.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#stop_timestxt)
 */
@Serializable
@JvmInline
public value class Timepoint(
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
