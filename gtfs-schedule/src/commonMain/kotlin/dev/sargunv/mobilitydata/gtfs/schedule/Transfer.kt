package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Rules and overrides for selected transfers between stops and routes.
 *
 * This class represents a record in the transfers.txt file.
 */
@Serializable
public data class Transfer(
  /**
   * Identifies a stop or station where a connection between routes begins. If this field refers to
   * a station, the transfer rule applies to all its child stops.
   */
  @SerialName("from_stop_id") public val fromStopId: Id<Stop>? = null,

  /**
   * Identifies a stop or station where a connection between routes ends. If this field refers to a
   * station, the transfer rule applies to all child stops.
   */
  @SerialName("to_stop_id") public val toStopId: Id<Stop>? = null,

  /**
   * Identifies a route where a connection begins. If defined, the transfer will apply to the
   * arriving trip on the route for the given from_stop_id.
   */
  @SerialName("from_route_id") public val fromRouteId: Id<Route>? = null,

  /**
   * Identifies a route where a connection ends. If defined, the transfer will apply to the
   * departing trip on the route for the given to_stop_id.
   */
  @SerialName("to_route_id") public val toRouteId: Id<Route>? = null,

  /**
   * Identifies a trip where a connection between routes begins. If defined, the transfer will apply
   * to the arriving trip for the given from_stop_id.
   */
  @SerialName("from_trip_id") public val fromTripId: Id<Trip>? = null,

  /**
   * Identifies a trip where a connection between routes ends. If defined, the transfer will apply
   * to the departing trip for the given to_stop_id.
   */
  @SerialName("to_trip_id") public val toTripId: Id<Trip>? = null,

  /** Indicates the type of connection for the specified (from_stop_id, to_stop_id) pair. */
  @SerialName("transfer_type") public val transferType: TransferType? = null,

  /**
   * Amount of time, in seconds, that must be available to permit a transfer between routes at the
   * specified stops.
   */
  @SerialName("min_transfer_time") public val minTransferTime: Int? = null,
)

/** Indicates the type of connection for a transfer. */
@Serializable
@JvmInline
public value class TransferType
private constructor(
  /** The integer value representing the transfer type. */
  public val value: Int
) {
  /** Companion object containing predefined transfer type constants. */
  public companion object {
    /** Recommended transfer point between routes. */
    public val Recommended: TransferType = TransferType(0)

    /**
     * Timed transfer point between two routes. The departing vehicle is expected to wait for the
     * arriving one and leave sufficient time for a rider to transfer between routes.
     */
    public val Timed: TransferType = TransferType(1)

    /**
     * Transfer requires a minimum amount of time between arrival and departure to ensure a
     * connection. The time required to transfer is specified by min_transfer_time.
     */
    public val MinimumTime: TransferType = TransferType(2)

    /** Transfers are not possible between routes at the location. */
    public val NotPossible: TransferType = TransferType(3)

    /**
     * Passengers can transfer from one trip to another by staying onboard the same vehicle (an
     * "in-seat transfer").
     */
    public val InSeat: TransferType = TransferType(4)

    /**
     * In-seat transfers are not allowed between sequential trips. The passenger must alight from
     * the vehicle and re-board.
     */
    public val NotInSeat: TransferType = TransferType(5)
  }
}
