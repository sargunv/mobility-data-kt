@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("UndocumentedPublicProperty")

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/** Realtime progress information for a trip. */
@Serializable
public data class TripUpdate(
  @ProtoNumber(1) public val trip: TripDescriptor,
  @ProtoNumber(3) public val vehicle: VehicleDescriptor? = null,
  @ProtoNumber(2) public val stopTimeUpdate: List<StopTimeUpdate> = emptyList(),
  @ProtoNumber(4) public val timestamp: Long? = null,
  @ProtoNumber(5) public val delay: Int? = null,
  @ProtoNumber(6) public val tripProperties: TripProperties? = null,
) {
  /** Timing information for a single arrival or departure event. */
  @Serializable
  public data class StopTimeEvent(
    @ProtoNumber(1) public val delay: Int? = null,
    @ProtoNumber(2) public val time: Long? = null,
    @ProtoNumber(3) public val uncertainty: Int? = null,
    @ProtoNumber(4) public val scheduledTime: Long? = null,
  )

  /** Realtime update for a stop on a trip. */
  @Serializable
  public data class StopTimeUpdate(
    @ProtoNumber(1) public val stopSequence: Int? = null,
    @ProtoNumber(4) public val stopId: String? = null,
    @ProtoNumber(2) public val arrival: StopTimeEvent? = null,
    @ProtoNumber(3) public val departure: StopTimeEvent? = null,
    @ProtoNumber(7) public val departureOccupancyStatus: VehiclePosition.OccupancyStatus? = null,
    @ProtoNumber(5)
    public val scheduleRelationship: ScheduleRelationship = ScheduleRelationship.Scheduled,
    @ProtoNumber(6) public val stopTimeProperties: StopTimeProperties? = null,
  ) {
    /** Relationship between the realtime stop and the scheduled stop. */
    @Serializable
    public enum class ScheduleRelationship {
      @ProtoNumber(0) Scheduled,
      @ProtoNumber(1) Skipped,
      @ProtoNumber(2) NoData,
      @ProtoNumber(3) Unscheduled,
    }

    /** Realtime stop-level property overrides. */
    @Serializable
    public data class StopTimeProperties(
      @ProtoNumber(1) public val assignedStopId: String? = null,
      @ProtoNumber(2) public val stopHeadsign: String? = null,
      @ProtoNumber(3) public val pickupType: DropOffPickupType? = null,
      @ProtoNumber(4) public val dropOffType: DropOffPickupType? = null,
    ) {
      /** Updated pickup or drop-off behavior. */
      @Serializable
      public enum class DropOffPickupType {
        @ProtoNumber(0) Regular,
        @ProtoNumber(1) None,
        @ProtoNumber(2) PhoneAgency,
        @ProtoNumber(3) CoordinateWithDriver,
      }
    }
  }

  /** Realtime trip-level property overrides. */
  @Serializable
  public data class TripProperties(
    @ProtoNumber(1) public val tripId: String? = null,
    @ProtoNumber(2) public val startDate: String? = null,
    @ProtoNumber(3) public val startTime: String? = null,
    @ProtoNumber(4) public val shapeId: String? = null,
    @ProtoNumber(5) public val tripHeadsign: String? = null,
    @ProtoNumber(6) public val tripShortName: String? = null,
  )
}

/** Identifies a trip instance or set of trips. */
@Serializable
public data class TripDescriptor(
  @ProtoNumber(1) public val tripId: String? = null,
  @ProtoNumber(5) public val routeId: String? = null,
  @ProtoNumber(6) public val directionId: Int? = null,
  @ProtoNumber(2) public val startTime: String? = null,
  @ProtoNumber(3) public val startDate: String? = null,
  @ProtoNumber(4)
  public val scheduleRelationship: ScheduleRelationship = ScheduleRelationship.Scheduled,
  @ProtoNumber(7) public val modifiedTrip: ModifiedTripSelector? = null,
) {
  /** Relationship between this trip descriptor and the static GTFS schedule. */
  @Serializable
  public enum class ScheduleRelationship {
    @ProtoNumber(0) Scheduled,
    @ProtoNumber(1) Added,
    @ProtoNumber(2) Unscheduled,
    @ProtoNumber(3) Canceled,
    @ProtoNumber(5) Replacement,
    @ProtoNumber(6) Duplicated,
    @ProtoNumber(7) Deleted,
    @ProtoNumber(8) New,
  }

  /** Selects a trip that is modified by a `TripModifications` entity. */
  @Serializable
  public data class ModifiedTripSelector(
    @ProtoNumber(1) public val modificationsId: String? = null,
    @ProtoNumber(2) public val affectedTripId: String? = null,
    @ProtoNumber(3) public val startTime: String? = null,
    @ProtoNumber(4) public val startDate: String? = null,
  )
}

/** Identifies the vehicle serving a trip. */
@Serializable
public data class VehicleDescriptor(
  @ProtoNumber(1) public val id: String? = null,
  @ProtoNumber(2) public val label: String? = null,
  @ProtoNumber(3) public val licensePlate: String? = null,
  @ProtoNumber(4)
  public val wheelchairAccessible: WheelchairAccessible = WheelchairAccessible.NoValue,
) {
  /** Wheelchair accessibility override for the trip or vehicle. */
  @Serializable
  public enum class WheelchairAccessible {
    @ProtoNumber(0) NoValue,
    @ProtoNumber(1) Unknown,
    @ProtoNumber(2) WheelchairAccessible,
    @ProtoNumber(3) WheelchairInaccessible,
  }
}

/** A realtime detour shape. */
@Serializable
public data class Shape(
  @ProtoNumber(1) public val shapeId: String? = null,
  @ProtoNumber(2) public val encodedPolyline: String? = null,
)

/** A stop dynamically added to the feed. */
@Serializable
public data class Stop(
  @ProtoNumber(1) public val stopId: String? = null,
  @ProtoNumber(2) public val stopCode: TranslatedString? = null,
  @ProtoNumber(3) public val stopName: TranslatedString? = null,
  @ProtoNumber(4) public val ttsStopName: TranslatedString? = null,
  @ProtoNumber(5) public val stopDesc: TranslatedString? = null,
  @ProtoNumber(6) public val stopLat: Float? = null,
  @ProtoNumber(7) public val stopLon: Float? = null,
  @ProtoNumber(8) public val zoneId: String? = null,
  @ProtoNumber(9) public val stopUrl: TranslatedString? = null,
  @ProtoNumber(11) public val parentStation: String? = null,
  @ProtoNumber(12) public val stopTimezone: String? = null,
  @ProtoNumber(13) public val wheelchairBoarding: WheelchairBoarding = WheelchairBoarding.Unknown,
  @ProtoNumber(14) public val levelId: String? = null,
  @ProtoNumber(15) public val platformCode: TranslatedString? = null,
) {
  /** Wheelchair boarding status for the stop. */
  @Serializable
  public enum class WheelchairBoarding {
    @ProtoNumber(0) Unknown,
    @ProtoNumber(1) Available,
    @ProtoNumber(2) NotAvailable,
  }
}

/** Realtime trip modifications such as detours. */
@Serializable
public data class TripModifications(
  @ProtoNumber(1) public val selectedTrips: List<SelectedTrips> = emptyList(),
  @ProtoNumber(2) public val startTimes: List<String> = emptyList(),
  @ProtoNumber(3) public val serviceDates: List<String> = emptyList(),
  @ProtoNumber(4) public val modifications: List<Modification> = emptyList(),
) {
  /** A set of trips affected by this modification set. */
  @Serializable
  public data class SelectedTrips(
    @ProtoNumber(1) public val tripIds: List<String> = emptyList(),
    @ProtoNumber(2) public val shapeId: String? = null,
  )

  /** A replacement applied to a span of stops on the original trip. */
  @Serializable
  public data class Modification(
    @ProtoNumber(1) public val startStopSelector: StopSelector? = null,
    @ProtoNumber(2) public val endStopSelector: StopSelector? = null,
    @ProtoNumber(3) public val propagatedModificationDelay: Int = 0,
    @ProtoNumber(4) public val replacementStops: List<ReplacementStop> = emptyList(),
    @ProtoNumber(5) public val serviceAlertId: String? = null,
    @ProtoNumber(6) public val lastModifiedTime: Long? = null,
  )
}

/** Selects a stop by sequence or stop ID. */
@Serializable
public data class StopSelector(
  @ProtoNumber(1) public val stopSequence: Int? = null,
  @ProtoNumber(2) public val stopId: String? = null,
)

/** A replacement stop for a trip modification. */
@Serializable
public data class ReplacementStop(
  @ProtoNumber(1) public val travelTimeToStop: Int? = null,
  @ProtoNumber(2) public val stopId: String? = null,
)
