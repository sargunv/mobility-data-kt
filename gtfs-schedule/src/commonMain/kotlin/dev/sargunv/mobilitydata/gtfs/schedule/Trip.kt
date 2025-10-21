package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlin.jvm.JvmInline
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

  /** Minimum advance booking time for demand-responsive transit (in minutes). */
  @SerialName("drt_advance_book_min") public val drtAdvanceBookMin: Double? = null,

  /** Identifies the fare that applies for this trip. */
  @SerialName("fare_id") public val fareId: String? = null,

  /** Indicates whether this trip runs during peak or off-peak periods. */
  @SerialName("peak_offpeak") public val peakOffpeak: String? = null,

  /** Indicates how passengers can board the vehicle. */
  @SerialName("boarding_type") public val boardingType: String? = null,
)

/**
 * Indicates the direction of travel for a trip.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#tripstxt)
 */
@Serializable
@JvmInline
public value class DirectionId(
  /** The integer value representing the direction. */
  public val value: Int
) {
  /** Companion object containing predefined direction constants. */
  public companion object {
    /** Travel in one direction (e.g., outbound travel). */
    public val Outbound: DirectionId = DirectionId(0)

    /** Travel in the opposite direction (e.g., inbound travel). */
    public val Inbound: DirectionId = DirectionId(1)
  }
}

/**
 * Indicates wheelchair accessibility.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#tripstxt)
 */
@Serializable
@JvmInline
public value class WheelchairAccessibility(
  /** The integer value representing wheelchair accessibility. */
  public val value: Int
) {
  /** Companion object containing predefined wheelchair accessibility constants. */
  public companion object {
    /** No accessibility information for the trip. */
    public val NoInfo: WheelchairAccessibility = WheelchairAccessibility(0)

    /** Vehicle being used on this trip can accommodate at least one rider in a wheelchair. */
    public val Accessible: WheelchairAccessibility = WheelchairAccessibility(1)

    /** No riders in wheelchairs can be accommodated on this trip. */
    public val NotAccessible: WheelchairAccessibility = WheelchairAccessibility(2)
  }
}

/**
 * Indicates whether bikes are allowed.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#tripstxt)
 */
@Serializable
@JvmInline
public value class BikesAllowed(
  /** The integer value representing bikes allowed status. */
  public val value: Int
) {
  /** Companion object containing predefined bikes allowed constants. */
  public companion object {
    /** No bike information for the trip. */
    public val NoInfo: BikesAllowed = BikesAllowed(0)

    /** Vehicle being used on this trip can accommodate at least one bicycle. */
    public val Allowed: BikesAllowed = BikesAllowed(1)

    /** No bicycles are allowed on this trip. */
    public val NotAllowed: BikesAllowed = BikesAllowed(2)
  }
}

/** Placeholder for Block entity. */
public class Block

/** Placeholder for Shape entity. */
public class Shape
