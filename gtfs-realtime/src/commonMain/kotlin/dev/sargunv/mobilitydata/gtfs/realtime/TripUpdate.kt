@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import dev.sargunv.mobilitydata.utils.TimeZoneId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/** Realtime progress information for a trip. */
@Serializable
public data class TripUpdate(
  /** Trip this update applies to. */
  @ProtoNumber(1) public val trip: TripDescriptor,
  /** Vehicle serving this trip. */
  @ProtoNumber(3) public val vehicle: VehicleDescriptor? = null,
  /** Updated timing for each stop on the trip. */
  @ProtoNumber(2) public val stopTimeUpdate: List<StopTimeUpdate> = emptyList(),
  /** POSIX timestamp when this update was generated. */
  @ProtoNumber(4) public val timestamp: Long? = null,
  /** Current schedule deviation in seconds; positive means late. */
  @ProtoNumber(5) public val delay: Int? = null,
  /** Realtime property overrides for the trip. */
  @ProtoNumber(6) public val tripProperties: TripProperties? = null,
) {
  /** Timing information for a single arrival or departure event. */
  @Serializable
  public data class StopTimeEvent(
    /** Delay from the scheduled time in seconds; positive means late. */
    @ProtoNumber(1) public val delay: Int? = null,
    /** Predicted event time as a POSIX timestamp. */
    @ProtoNumber(2) public val time: Long? = null,
    /** Uncertainty of the prediction in seconds. */
    @ProtoNumber(3) public val uncertainty: Int? = null,
    /** Original scheduled time as a POSIX timestamp. */
    @ProtoNumber(4) public val scheduledTime: Long? = null,
  )

  /** Realtime update for a stop on a trip. */
  @Serializable
  public data class StopTimeUpdate(
    /** GTFS `stop_sequence` for the stop. */
    @ProtoNumber(1) public val stopSequence: Int? = null,
    /** GTFS `stop_id` for the stop. */
    @ProtoNumber(4) public val stopId: String? = null,
    /** Updated arrival timing. */
    @ProtoNumber(2) public val arrival: StopTimeEvent? = null,
    /** Updated departure timing. */
    @ProtoNumber(3) public val departure: StopTimeEvent? = null,
    /** Predicted passenger occupancy at departure. */
    @ProtoNumber(7) public val departureOccupancyStatus: VehiclePosition.OccupancyStatus? = null,
    /** Relationship between this stop time and the static schedule. */
    @ProtoNumber(5)
    public val scheduleRelationship: ScheduleRelationship = ScheduleRelationship.Scheduled,
    /** Realtime property overrides for this stop time. */
    @ProtoNumber(6) public val stopTimeProperties: StopTimeProperties? = null,
  ) {
    /** Relationship between the realtime stop and the scheduled stop. */
    @Suppress("UndocumentedPublicProperty")
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
      /** Overridden GTFS `stop_id` for the stop assignment. */
      @ProtoNumber(1) public val assignedStopId: String? = null,
      /** Overridden headsign displayed at this stop. */
      @ProtoNumber(2) public val stopHeadsign: String? = null,
      /** Overridden pickup behavior at this stop. */
      @ProtoNumber(3) public val pickupType: DropOffPickupType? = null,
      /** Overridden drop-off behavior at this stop. */
      @ProtoNumber(4) public val dropOffType: DropOffPickupType? = null,
    ) {
      /** Updated pickup or drop-off behavior. */
      @Suppress("UndocumentedPublicProperty")
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
    /** New GTFS `trip_id` for the duplicated trip. */
    @ProtoNumber(1) public val tripId: String? = null,
    /** Overridden service date in YYYYMMDD format. */
    @ProtoNumber(2) public val startDate: String? = null,
    /** Overridden start time in HH:MM:SS format. */
    @ProtoNumber(3) public val startTime: String? = null,
    /** Overridden GTFS `shape_id`. */
    @ProtoNumber(4) public val shapeId: String? = null,
    /** Overridden trip headsign. */
    @ProtoNumber(5) public val tripHeadsign: String? = null,
    /** Overridden trip short name. */
    @ProtoNumber(6) public val tripShortName: String? = null,
  )
}

/** Identifies a trip instance or set of trips. */
@Serializable
public data class TripDescriptor(
  /** GTFS `trip_id`. */
  @ProtoNumber(1) public val tripId: String? = null,
  /** GTFS `route_id`. */
  @ProtoNumber(5) public val routeId: String? = null,
  /** GTFS `direction_id`. */
  @ProtoNumber(6) public val directionId: Int? = null,
  /** Scheduled start time in HH:MM:SS format. */
  @ProtoNumber(2) public val startTime: String? = null,
  /** Service date in YYYYMMDD format. */
  @ProtoNumber(3) public val startDate: String? = null,
  /** Relationship between this trip and the static schedule. */
  @ProtoNumber(4)
  public val scheduleRelationship: ScheduleRelationship = ScheduleRelationship.Scheduled,
  /** Reference to a modified trip, if applicable. */
  @ProtoNumber(7) public val modifiedTrip: ModifiedTripSelector? = null,
) {
  /** Relationship between this trip descriptor and the static GTFS schedule. */
  @Suppress("UndocumentedPublicProperty")
  @Serializable
  public enum class ScheduleRelationship {
    @ProtoNumber(0) Scheduled,
    @Deprecated(
      "Use Duplicated for extras of an existing scheduled trip, or New for unrelated extras"
    )
    @ProtoNumber(1)
    Added,
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
    /** Feed entity ID of the `TripModifications` entity. */
    @ProtoNumber(1) public val modificationsId: String? = null,
    /** GTFS `trip_id` of the original trip being modified. */
    @ProtoNumber(2) public val affectedTripId: String? = null,
    /** Start time of the modified trip in HH:MM:SS format. */
    @ProtoNumber(3) public val startTime: String? = null,
    /** Service date of the modified trip in YYYYMMDD format. */
    @ProtoNumber(4) public val startDate: String? = null,
  )
}

/** Identifies the vehicle serving a trip. */
@Serializable
public data class VehicleDescriptor(
  /** Internal system identifier for the vehicle. */
  @ProtoNumber(1) public val id: String? = null,
  /** User-visible label for the vehicle. */
  @ProtoNumber(2) public val label: String? = null,
  /** License plate number of the vehicle. */
  @ProtoNumber(3) public val licensePlate: String? = null,
  /** Wheelchair accessibility of the vehicle. */
  @ProtoNumber(4)
  public val wheelchairAccessible: WheelchairAccessible = WheelchairAccessible.NoValue,
) {
  /** Wheelchair accessibility override for the trip or vehicle. */
  @Suppress("UndocumentedPublicProperty")
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
  /** GTFS `shape_id` this shape replaces or supplements. */
  @ProtoNumber(1) public val shapeId: String? = null,
  /** Shape geometry as an encoded polyline string. */
  @ProtoNumber(2) public val encodedPolyline: String? = null,
)

/** A stop dynamically added to the feed. */
@Serializable
public data class Stop(
  /** Unique identifier for the stop. */
  @ProtoNumber(1) public val stopId: String? = null,
  /** Short text or number identifying the stop to riders. */
  @ProtoNumber(2) public val stopCode: TranslatedString? = null,
  /** Name of the stop. */
  @ProtoNumber(3) public val stopName: TranslatedString? = null,
  /** Text-to-speech version of [stopName]. */
  @ProtoNumber(4) public val ttsStopName: TranslatedString? = null,
  /** Description of the stop. */
  @ProtoNumber(5) public val stopDesc: TranslatedString? = null,
  /** Latitude of the stop in WGS84. */
  @ProtoNumber(6) public val stopLat: Float? = null,
  /** Longitude of the stop in WGS84. */
  @ProtoNumber(7) public val stopLon: Float? = null,
  /** Fare zone for the stop. */
  @ProtoNumber(8) public val zoneId: String? = null,
  /** URL with information about the stop. */
  @ProtoNumber(9) public val stopUrl: TranslatedString? = null,
  /** GTFS `stop_id` of the parent station. */
  @ProtoNumber(11) public val parentStation: String? = null,
  /** IANA time zone identifier for the stop. */
  @ProtoNumber(12) public val stopTimezone: TimeZoneId? = null,
  /** Wheelchair boarding availability at the stop. */
  @ProtoNumber(13) public val wheelchairBoarding: WheelchairBoarding = WheelchairBoarding.Unknown,
  /** GTFS `level_id` for the stop. */
  @ProtoNumber(14) public val levelId: String? = null,
  /** Platform identifier for the stop. */
  @ProtoNumber(15) public val platformCode: TranslatedString? = null,
) {
  /** Wheelchair boarding status for the stop. */
  @Suppress("UndocumentedPublicProperty")
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
  /** Trips affected by these modifications. */
  @ProtoNumber(1) public val selectedTrips: List<SelectedTrips> = emptyList(),
  /** Exact start times of the affected trips in HH:MM:SS format. */
  @ProtoNumber(2) public val startTimes: List<String> = emptyList(),
  /** Service dates when modifications apply, in YYYYMMDD format. */
  @ProtoNumber(3) public val serviceDates: List<String> = emptyList(),
  /** Ordered list of modifications to apply. */
  @ProtoNumber(4) public val modifications: List<Modification> = emptyList(),
) {
  /** A set of trips affected by this modification set. */
  @Serializable
  public data class SelectedTrips(
    /** GTFS `trip_id` values of the affected trips. */
    @ProtoNumber(1) public val tripIds: List<String> = emptyList(),
    /** Replacement GTFS `shape_id` for the affected trips. */
    @ProtoNumber(2) public val shapeId: String? = null,
  )

  /** A replacement applied to a span of stops on the original trip. */
  @Serializable
  public data class Modification(
    /** First stop in the span to replace. */
    @ProtoNumber(1) public val startStopSelector: StopSelector? = null,
    /** Last stop in the span to replace. */
    @ProtoNumber(2) public val endStopSelector: StopSelector? = null,
    /** Delay in seconds propagated to stops after the modification. */
    @ProtoNumber(3) public val propagatedModificationDelay: Int = 0,
    /** Stops inserted in place of the replaced span. */
    @ProtoNumber(4) public val replacementStops: List<ReplacementStop> = emptyList(),
    /** Feed entity ID of the related service alert. */
    @ProtoNumber(5) public val serviceAlertId: String? = null,
    /** POSIX timestamp when this modification was last updated. */
    @ProtoNumber(6) public val lastModifiedTime: Long? = null,
  )
}

/** Selects a stop by sequence or stop ID. */
@Serializable
public data class StopSelector(
  /** GTFS `stop_sequence` value. */
  @ProtoNumber(1) public val stopSequence: Int? = null,
  /** GTFS `stop_id` value. */
  @ProtoNumber(2) public val stopId: String? = null,
)

/** A replacement stop for a trip modification. */
@Serializable
public data class ReplacementStop(
  /** Travel time in seconds from the previous stop to this replacement stop. */
  @ProtoNumber(1) public val travelTimeToStop: Int? = null,
  /** GTFS `stop_id` of the replacement stop. */
  @ProtoNumber(2) public val stopId: String? = null,
)
