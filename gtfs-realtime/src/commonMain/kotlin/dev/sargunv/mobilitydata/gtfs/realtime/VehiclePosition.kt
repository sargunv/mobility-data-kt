@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("UndocumentedPublicProperty")

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/** Realtime positioning information for a vehicle. */
@Serializable
public data class VehiclePosition(
  @ProtoNumber(1) public val trip: TripDescriptor? = null,
  @ProtoNumber(8) public val vehicle: VehicleDescriptor? = null,
  @ProtoNumber(2) public val position: Position? = null,
  @ProtoNumber(3) public val currentStopSequence: Int? = null,
  @ProtoNumber(7) public val stopId: String? = null,
  @ProtoNumber(4) public val currentStatus: VehicleStopStatus = VehicleStopStatus.InTransitTo,
  @ProtoNumber(5) public val timestamp: Long? = null,
  @ProtoNumber(6) public val congestionLevel: CongestionLevel? = null,
  @ProtoNumber(9) public val occupancyStatus: OccupancyStatus? = null,
  @ProtoNumber(10) public val occupancyPercentage: Int? = null,
  @ProtoNumber(11) public val multiCarriageDetails: List<CarriageDetails> = emptyList(),
) {
  /** The vehicle's relationship to the current stop. */
  @Serializable
  public enum class VehicleStopStatus {
    @ProtoNumber(0) IncomingAt,
    @ProtoNumber(1) StoppedAt,
    @ProtoNumber(2) InTransitTo,
  }

  /** Traffic congestion affecting the vehicle. */
  @Serializable
  public enum class CongestionLevel {
    @ProtoNumber(0) UnknownCongestionLevel,
    @ProtoNumber(1) RunningSmoothly,
    @ProtoNumber(2) StopAndGo,
    @ProtoNumber(3) Congestion,
    @ProtoNumber(4) SevereCongestion,
  }

  /** Passenger occupancy state for the vehicle or carriage. */
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
    @ProtoNumber(1) public val id: String? = null,
    @ProtoNumber(2) public val label: String? = null,
    @ProtoNumber(3) public val occupancyStatus: OccupancyStatus = OccupancyStatus.NoDataAvailable,
    @ProtoNumber(4) public val occupancyPercentage: Int = -1,
    @ProtoNumber(5) public val carriageSequence: Int? = null,
  )
}

/** Geographic vehicle position. */
@Serializable
public data class Position(
  @ProtoNumber(1) public val latitude: Float,
  @ProtoNumber(2) public val longitude: Float,
  @ProtoNumber(3) public val bearing: Float? = null,
  @ProtoNumber(4) public val odometer: Double? = null,
  @ProtoNumber(5) public val speed: Float? = null,
)
