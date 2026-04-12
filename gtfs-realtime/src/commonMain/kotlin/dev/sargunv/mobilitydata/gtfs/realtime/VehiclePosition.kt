@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/** Realtime positioning information for a vehicle. */
@Serializable
public data class VehiclePosition(
  /** Trip the vehicle is serving. */
  @ProtoNumber(1) public val trip: TripDescriptor? = null,
  /** Vehicle serving the trip. */
  @ProtoNumber(8) public val vehicle: VehicleDescriptor? = null,
  /** Current geographic position of the vehicle. */
  @ProtoNumber(2) public val position: Position? = null,
  /** GTFS `stop_sequence` of the current stop. */
  @ProtoNumber(3) public val currentStopSequence: Int? = null,
  /** GTFS `stop_id` of the current stop. */
  @ProtoNumber(7) public val stopId: String? = null,
  /** Vehicle's relationship to the current stop. */
  @ProtoNumber(4) public val currentStatus: VehicleStopStatus = VehicleStopStatus.InTransitTo,
  /** POSIX timestamp when this position was measured. */
  @ProtoNumber(5) public val timestamp: Long? = null,
  /** Traffic congestion affecting the vehicle. */
  @ProtoNumber(6) public val congestionLevel: CongestionLevel? = null,
  /** Passenger occupancy status of the vehicle. */
  @ProtoNumber(9) public val occupancyStatus: OccupancyStatus? = null,
  /** Passenger occupancy as a percentage of capacity. */
  @ProtoNumber(10) public val occupancyPercentage: Int? = null,
  /** Per-carriage occupancy and identification details. */
  @ProtoNumber(11) public val multiCarriageDetails: List<CarriageDetails> = emptyList(),
) {
  /** The vehicle's relationship to the current stop. */
  @Suppress("UndocumentedPublicProperty")
  @Serializable
  public enum class VehicleStopStatus {
    @ProtoNumber(0) IncomingAt,
    @ProtoNumber(1) StoppedAt,
    @ProtoNumber(2) InTransitTo,
  }

  /** Traffic congestion affecting the vehicle. */
  @Suppress("UndocumentedPublicProperty")
  @Serializable
  public enum class CongestionLevel {
    @ProtoNumber(0) UnknownCongestionLevel,
    @ProtoNumber(1) RunningSmoothly,
    @ProtoNumber(2) StopAndGo,
    @ProtoNumber(3) Congestion,
    @ProtoNumber(4) SevereCongestion,
  }

  /** Passenger occupancy state for the vehicle or carriage. */
  @Suppress("UndocumentedPublicProperty")
  @Serializable
  public enum class OccupancyStatus {
    @ProtoNumber(0) Empty,
    @ProtoNumber(1) ManySeatsAvailable,
    @ProtoNumber(2) FewSeatsAvailable,
    @ProtoNumber(3) StandingRoomOnly,
    @ProtoNumber(4) CrushedStandingRoomOnly,
    @ProtoNumber(5) Full,
    @ProtoNumber(6) NotAcceptingPassengers,
    @ProtoNumber(7) NoDataAvailable,
    @ProtoNumber(8) NotBoardable,
  }

  /** Realtime details for a single carriage. */
  @Serializable
  public data class CarriageDetails(
    /** Identifier for the carriage, unique within the vehicle. */
    @ProtoNumber(1) public val id: String? = null,
    /** User-visible label for the carriage. */
    @ProtoNumber(2) public val label: String? = null,
    /** Passenger occupancy status of the carriage. */
    @ProtoNumber(3) public val occupancyStatus: OccupancyStatus = OccupancyStatus.NoDataAvailable,
    /** Passenger occupancy as a percentage of carriage capacity; -1 if unknown. */
    @ProtoNumber(4) public val occupancyPercentage: Int = -1,
    /** 1-based position of this carriage from the front of the vehicle. */
    @ProtoNumber(5) public val carriageSequence: Int? = null,
  )
}

/** Geographic vehicle position. */
@Serializable
public data class Position(
  /** Latitude in WGS84 degrees. */
  @ProtoNumber(1) public val latitude: Float,
  /** Longitude in WGS84 degrees. */
  @ProtoNumber(2) public val longitude: Float,
  /** Bearing in degrees, clockwise from true north. */
  @ProtoNumber(3) public val bearing: Float? = null,
  /** Odometer reading in meters. */
  @ProtoNumber(4) public val odometer: Double? = null,
  /** Momentary speed in meters per second. */
  @ProtoNumber(5) public val speed: Float? = null,
)
