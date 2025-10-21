package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.ServiceTime
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
)

/**
 * Indicates pickup or drop off method.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#stop_timestxt)
 */
@Serializable
public enum class PickupDropOffType {
  /** Regularly scheduled pickup/drop off. */
  @SerialName("0") REGULAR,

  /** No pickup/drop off available. */
  @SerialName("1") NONE,

  /** Must phone agency to arrange pickup/drop off. */
  @SerialName("2") PHONE_AGENCY,

  /** Must coordinate with driver to arrange pickup/drop off. */
  @SerialName("3") COORDINATE_WITH_DRIVER,
}

/**
 * Indicates whether a rider can board the transit vehicle at this stop.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#stop_timestxt)
 */
@Serializable
public enum class Timepoint {
  /** Times are considered approximate. */
  @SerialName("0") APPROXIMATE,

  /** Times are considered exact. */
  @SerialName("1") EXACT,
}
