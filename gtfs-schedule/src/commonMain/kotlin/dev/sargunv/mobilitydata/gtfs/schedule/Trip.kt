package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Trips for each route. A trip is a sequence of two or more stops that occur during a specific time
 * period.
 *
 * This class represents a record in the trips.txt file.
 */
@Serializable
public data class Trip(
  /** Uniquely identifies a trip. */
  @SerialName("trip_id") public val tripId: String,

  /** Identifies a route. */
  @SerialName("route_id") public val routeId: String,

  /** Identifies a set of dates when service is available for one or more routes. */
  @SerialName("service_id") public val serviceId: String,

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
  @SerialName("wheelchair_accessible") public val wheelchairAccessible: TriState? = null,

  /** Indicates whether bikes are allowed. */
  @SerialName("bikes_allowed") public val bikesAllowed: TriState? = null,

  /** Indicates whether cars are allowed. */
  @SerialName("cars_allowed") public val carsAllowed: TriState? = null,
)

/** Indicates the direction of travel for a trip. */
@Serializable
@JvmInline
public value class DirectionId
private constructor(
  /** The integer value representing the direction. */
  public val value: Int
) {
  /** Companion object containing predefined direction constants. */
  public companion object {
    /** Travel in one direction (e.g., outbound travel). */
    public val ThatWay: DirectionId = DirectionId(0)

    /** Travel in the opposite direction (e.g., inbound travel). */
    public val OtherWay: DirectionId = DirectionId(1)
  }
}
