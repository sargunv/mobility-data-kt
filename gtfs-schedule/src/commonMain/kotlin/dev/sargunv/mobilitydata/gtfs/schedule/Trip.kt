package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Trips for each route. A trip is a sequence of two or more stops that occur during a specific time
 * period.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#tripstxt)
 */
@Serializable
public data class Trip(
  /** Uniquely identifies a trip. */
  @SerialName("trip_id") public val tripId: Id<Trip>,

  /** Identifies a route. */
  @SerialName("route_id") public val routeId: Id<Route>,

  /** Identifies a set of dates when service is available for one or more routes. */
  @SerialName("service_id") public val serviceId: Id<ServiceCalendar>,

  /** Text that appears on signage identifying the trip's destination to riders. */
  @SerialName("trip_headsign") public val tripHeadsign: String? = null,

  /** Public facing text used to identify the trip to riders. */
  @SerialName("trip_short_name") public val tripShortName: String? = null,

  /** Indicates the direction of travel for a trip. */
  @SerialName("direction_id") public val directionId: DirectionId? = null,

  /** Identifies the block to which the trip belongs. */
  @SerialName("block_id") public val blockId: String? = null,

  /** Identifies a geospatial shape describing the vehicle travel path for a trip. */
  @SerialName("shape_id") public val shapeId: String? = null,

  /** Indicates wheelchair accessibility. */
  @SerialName("wheelchair_accessible")
  public val wheelchairAccessible: WheelchairAccessibility? = null,

  /** Indicates whether bikes are allowed. */
  @SerialName("bikes_allowed") public val bikesAllowed: BikesAllowed? = null,
)

/**
 * Indicates the direction of travel for a trip.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#tripstxt)
 */
@Serializable
public enum class DirectionId {
  /** Travel in one direction (e.g., outbound travel). */
  @SerialName("0") OUTBOUND,

  /** Travel in the opposite direction (e.g., inbound travel). */
  @SerialName("1") INBOUND,
}

/**
 * Indicates wheelchair accessibility.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#tripstxt)
 */
@Serializable
public enum class WheelchairAccessibility {
  /** No accessibility information for the trip. */
  @SerialName("0") NO_INFO,

  /** Vehicle being used on this trip can accommodate at least one rider in a wheelchair. */
  @SerialName("1") ACCESSIBLE,

  /** No riders in wheelchairs can be accommodated on this trip. */
  @SerialName("2") NOT_ACCESSIBLE,
}

/**
 * Indicates whether bikes are allowed.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#tripstxt)
 */
@Serializable
public enum class BikesAllowed {
  /** No bike information for the trip. */
  @SerialName("0") NO_INFO,

  /** Vehicle being used on this trip can accommodate at least one bicycle. */
  @SerialName("1") ALLOWED,

  /** No bicycles are allowed on this trip. */
  @SerialName("2") NOT_ALLOWED,
}

/** Placeholder for Block entity. */
public class Block

/** Placeholder for Shape entity. */
public class Shape
