package dev.sargunv.mobilitydata.gtfs.realtime

import com.google.transit.realtime.GtfsRealtime
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies that every enum value's `@ProtoNumber` annotation matches the official Java protobuf
 * bindings. Since the Kotlin data classes are handwritten, a typo in any proto number would
 * silently produce wrong data.
 */
class EnumInteropTest {
  private fun decodeTripUpdate(builder: GtfsRealtime.TripUpdate.Builder): TripUpdate {
    val feed =
      GtfsRealtime.FeedMessage.newBuilder()
        .setHeader(GtfsRealtime.FeedHeader.newBuilder().setGtfsRealtimeVersion("2.0").build())
        .addEntity(
          GtfsRealtime.FeedEntity.newBuilder().setId("e1").setTripUpdate(builder.build()).build()
        )
        .build()
    return GtfsRealtimeProto.decodeFeedMessage(feed.toByteArray()).entity.single().tripUpdate!!
  }

  private fun decodeVehiclePosition(
    builder: GtfsRealtime.VehiclePosition.Builder
  ): VehiclePosition {
    val feed =
      GtfsRealtime.FeedMessage.newBuilder()
        .setHeader(GtfsRealtime.FeedHeader.newBuilder().setGtfsRealtimeVersion("2.0").build())
        .addEntity(
          GtfsRealtime.FeedEntity.newBuilder().setId("e1").setVehicle(builder.build()).build()
        )
        .build()
    return GtfsRealtimeProto.decodeFeedMessage(feed.toByteArray()).entity.single().vehicle!!
  }

  private fun decodeAlert(builder: GtfsRealtime.Alert.Builder): Alert {
    val feed =
      GtfsRealtime.FeedMessage.newBuilder()
        .setHeader(GtfsRealtime.FeedHeader.newBuilder().setGtfsRealtimeVersion("2.0").build())
        .addEntity(
          GtfsRealtime.FeedEntity.newBuilder().setId("e1").setAlert(builder.build()).build()
        )
        .build()
    return GtfsRealtimeProto.decodeFeedMessage(feed.toByteArray()).entity.single().alert!!
  }

  @Test
  fun tripDescriptorScheduleRelationship() {
    val mapping =
      mapOf(
        GtfsRealtime.TripDescriptor.ScheduleRelationship.SCHEDULED to
          TripDescriptor.ScheduleRelationship.Scheduled,
        GtfsRealtime.TripDescriptor.ScheduleRelationship.CANCELED to
          TripDescriptor.ScheduleRelationship.Canceled,
        GtfsRealtime.TripDescriptor.ScheduleRelationship.REPLACEMENT to
          TripDescriptor.ScheduleRelationship.Replacement,
        GtfsRealtime.TripDescriptor.ScheduleRelationship.DUPLICATED to
          TripDescriptor.ScheduleRelationship.Duplicated,
      )

    mapping.forEach { (official, expected) ->
      val tripUpdate =
        decodeTripUpdate(
          GtfsRealtime.TripUpdate.newBuilder()
            .setTrip(
              GtfsRealtime.TripDescriptor.newBuilder().setScheduleRelationship(official).build()
            )
        )
      assertEquals(expected, tripUpdate.trip.scheduleRelationship, "Mismatch for $official")
    }
  }

  @Test
  fun tripDescriptorScheduleRelationshipPresence() {
    val absent =
      decodeTripUpdate(
        GtfsRealtime.TripUpdate.newBuilder()
          .setTrip(GtfsRealtime.TripDescriptor.newBuilder().setTripId("t1").build())
      )
    assertEquals(null, absent.trip.scheduleRelationship)

    val explicitScheduled =
      decodeTripUpdate(
        GtfsRealtime.TripUpdate.newBuilder()
          .setTrip(
            GtfsRealtime.TripDescriptor.newBuilder()
              .setTripId("t1")
              .setScheduleRelationship(GtfsRealtime.TripDescriptor.ScheduleRelationship.SCHEDULED)
              .build()
          )
      )
    assertEquals(
      TripDescriptor.ScheduleRelationship.Scheduled,
      explicitScheduled.trip.scheduleRelationship,
    )
  }

  @Test
  fun tripDescriptorScheduleRelationshipEncodePresence() {
    fun encodeTrip(descriptor: TripDescriptor): GtfsRealtime.TripDescriptor {
      val feed =
        FeedMessage(
          header = FeedHeader(gtfsRealtimeVersion = "2.0"),
          entity = listOf(FeedEntity(id = "e1", tripUpdate = TripUpdate(trip = descriptor))),
        )
      return GtfsRealtime.FeedMessage.parseFrom(GtfsRealtimeProto.encodeFeedMessage(feed))
        .getEntity(0)
        .tripUpdate
        .trip
    }

    val omitted = encodeTrip(TripDescriptor(tripId = "t1"))
    assertEquals(false, omitted.hasScheduleRelationship())

    val scheduled =
      encodeTrip(
        TripDescriptor(
          tripId = "t1",
          scheduleRelationship = TripDescriptor.ScheduleRelationship.Scheduled,
        )
      )
    assertEquals(true, scheduled.hasScheduleRelationship())
    assertEquals(
      GtfsRealtime.TripDescriptor.ScheduleRelationship.SCHEDULED,
      scheduled.scheduleRelationship,
    )
  }

  @Test
  fun stopTimeUpdateScheduleRelationship() {
    val mapping =
      mapOf(
        GtfsRealtime.TripUpdate.StopTimeUpdate.ScheduleRelationship.SCHEDULED to
          TripUpdate.StopTimeUpdate.ScheduleRelationship.Scheduled,
        GtfsRealtime.TripUpdate.StopTimeUpdate.ScheduleRelationship.SKIPPED to
          TripUpdate.StopTimeUpdate.ScheduleRelationship.Skipped,
        GtfsRealtime.TripUpdate.StopTimeUpdate.ScheduleRelationship.NO_DATA to
          TripUpdate.StopTimeUpdate.ScheduleRelationship.NoData,
        GtfsRealtime.TripUpdate.StopTimeUpdate.ScheduleRelationship.UNSCHEDULED to
          TripUpdate.StopTimeUpdate.ScheduleRelationship.Unscheduled,
      )

    mapping.forEach { (official, expected) ->
      val tripUpdate =
        decodeTripUpdate(
          GtfsRealtime.TripUpdate.newBuilder()
            .setTrip(GtfsRealtime.TripDescriptor.newBuilder().build())
            .addStopTimeUpdate(
              GtfsRealtime.TripUpdate.StopTimeUpdate.newBuilder()
                .setScheduleRelationship(official)
                .build()
            )
        )
      assertEquals(
        expected,
        tripUpdate.stopTimeUpdate.single().scheduleRelationship,
        "Mismatch for $official",
      )
    }
  }

  @Test
  fun vehicleStopStatus() {
    val mapping =
      mapOf(
        GtfsRealtime.VehiclePosition.VehicleStopStatus.INCOMING_AT to
          VehiclePosition.VehicleStopStatus.IncomingAt,
        GtfsRealtime.VehiclePosition.VehicleStopStatus.STOPPED_AT to
          VehiclePosition.VehicleStopStatus.StoppedAt,
        GtfsRealtime.VehiclePosition.VehicleStopStatus.IN_TRANSIT_TO to
          VehiclePosition.VehicleStopStatus.InTransitTo,
      )

    mapping.forEach { (official, expected) ->
      val vp =
        decodeVehiclePosition(GtfsRealtime.VehiclePosition.newBuilder().setCurrentStatus(official))
      assertEquals(expected, vp.currentStatus, "Mismatch for $official")
    }
  }

  @Test
  fun congestionLevel() {
    val mapping =
      mapOf(
        GtfsRealtime.VehiclePosition.CongestionLevel.UNKNOWN_CONGESTION_LEVEL to
          VehiclePosition.CongestionLevel.UnknownCongestionLevel,
        GtfsRealtime.VehiclePosition.CongestionLevel.RUNNING_SMOOTHLY to
          VehiclePosition.CongestionLevel.RunningSmoothly,
        GtfsRealtime.VehiclePosition.CongestionLevel.STOP_AND_GO to
          VehiclePosition.CongestionLevel.StopAndGo,
        GtfsRealtime.VehiclePosition.CongestionLevel.CONGESTION to
          VehiclePosition.CongestionLevel.Congestion,
        GtfsRealtime.VehiclePosition.CongestionLevel.SEVERE_CONGESTION to
          VehiclePosition.CongestionLevel.SevereCongestion,
      )

    mapping.forEach { (official, expected) ->
      val vp =
        decodeVehiclePosition(
          GtfsRealtime.VehiclePosition.newBuilder().setCongestionLevel(official)
        )
      assertEquals(expected, vp.congestionLevel, "Mismatch for $official")
    }
  }

  @Test
  fun occupancyStatus() {
    val mapping =
      mapOf(
        GtfsRealtime.VehiclePosition.OccupancyStatus.EMPTY to VehiclePosition.OccupancyStatus.Empty,
        GtfsRealtime.VehiclePosition.OccupancyStatus.MANY_SEATS_AVAILABLE to
          VehiclePosition.OccupancyStatus.ManySeatsAvailable,
        GtfsRealtime.VehiclePosition.OccupancyStatus.FEW_SEATS_AVAILABLE to
          VehiclePosition.OccupancyStatus.FewSeatsAvailable,
        GtfsRealtime.VehiclePosition.OccupancyStatus.STANDING_ROOM_ONLY to
          VehiclePosition.OccupancyStatus.StandingRoomOnly,
        GtfsRealtime.VehiclePosition.OccupancyStatus.CRUSHED_STANDING_ROOM_ONLY to
          VehiclePosition.OccupancyStatus.CrushedStandingRoomOnly,
        GtfsRealtime.VehiclePosition.OccupancyStatus.FULL to VehiclePosition.OccupancyStatus.Full,
        GtfsRealtime.VehiclePosition.OccupancyStatus.NOT_ACCEPTING_PASSENGERS to
          VehiclePosition.OccupancyStatus.NotAcceptingPassengers,
        GtfsRealtime.VehiclePosition.OccupancyStatus.NO_DATA_AVAILABLE to
          VehiclePosition.OccupancyStatus.NoDataAvailable,
        GtfsRealtime.VehiclePosition.OccupancyStatus.NOT_BOARDABLE to
          VehiclePosition.OccupancyStatus.NotBoardable,
      )

    mapping.forEach { (official, expected) ->
      val vp =
        decodeVehiclePosition(
          GtfsRealtime.VehiclePosition.newBuilder().setOccupancyStatus(official)
        )
      assertEquals(expected, vp.occupancyStatus, "Mismatch for $official")
    }
  }

  @Test
  fun alertCause() {
    val mapping =
      mapOf(
        GtfsRealtime.Alert.Cause.UNKNOWN_CAUSE to Alert.Cause.UnknownCause,
        GtfsRealtime.Alert.Cause.OTHER_CAUSE to Alert.Cause.OtherCause,
        GtfsRealtime.Alert.Cause.TECHNICAL_PROBLEM to Alert.Cause.TechnicalProblem,
        GtfsRealtime.Alert.Cause.STRIKE to Alert.Cause.Strike,
        GtfsRealtime.Alert.Cause.DEMONSTRATION to Alert.Cause.Demonstration,
        GtfsRealtime.Alert.Cause.ACCIDENT to Alert.Cause.Accident,
        GtfsRealtime.Alert.Cause.HOLIDAY to Alert.Cause.Holiday,
        GtfsRealtime.Alert.Cause.WEATHER to Alert.Cause.Weather,
        GtfsRealtime.Alert.Cause.MAINTENANCE to Alert.Cause.Maintenance,
        GtfsRealtime.Alert.Cause.CONSTRUCTION to Alert.Cause.Construction,
        GtfsRealtime.Alert.Cause.POLICE_ACTIVITY to Alert.Cause.PoliceActivity,
        GtfsRealtime.Alert.Cause.MEDICAL_EMERGENCY to Alert.Cause.MedicalEmergency,
      )

    mapping.forEach { (official, expected) ->
      val alert = decodeAlert(GtfsRealtime.Alert.newBuilder().setCause(official))
      assertEquals(expected, alert.cause, "Mismatch for $official")
    }
  }

  @Test
  fun alertEffect() {
    val mapping =
      mapOf(
        GtfsRealtime.Alert.Effect.NO_SERVICE to Alert.Effect.NoService,
        GtfsRealtime.Alert.Effect.REDUCED_SERVICE to Alert.Effect.ReducedService,
        GtfsRealtime.Alert.Effect.SIGNIFICANT_DELAYS to Alert.Effect.SignificantDelays,
        GtfsRealtime.Alert.Effect.DETOUR to Alert.Effect.Detour,
        GtfsRealtime.Alert.Effect.ADDITIONAL_SERVICE to Alert.Effect.AdditionalService,
        GtfsRealtime.Alert.Effect.MODIFIED_SERVICE to Alert.Effect.ModifiedService,
        GtfsRealtime.Alert.Effect.OTHER_EFFECT to Alert.Effect.OtherEffect,
        GtfsRealtime.Alert.Effect.UNKNOWN_EFFECT to Alert.Effect.UnknownEffect,
        GtfsRealtime.Alert.Effect.STOP_MOVED to Alert.Effect.StopMoved,
        GtfsRealtime.Alert.Effect.NO_EFFECT to Alert.Effect.NoEffect,
        GtfsRealtime.Alert.Effect.ACCESSIBILITY_ISSUE to Alert.Effect.AccessibilityIssue,
      )

    mapping.forEach { (official, expected) ->
      val alert = decodeAlert(GtfsRealtime.Alert.newBuilder().setEffect(official))
      assertEquals(expected, alert.effect, "Mismatch for $official")
    }
  }

  @Test
  fun alertSeverityLevel() {
    val mapping =
      mapOf(
        GtfsRealtime.Alert.SeverityLevel.UNKNOWN_SEVERITY to Alert.SeverityLevel.UnknownSeverity,
        GtfsRealtime.Alert.SeverityLevel.INFO to Alert.SeverityLevel.Info,
        GtfsRealtime.Alert.SeverityLevel.WARNING to Alert.SeverityLevel.Warning,
        GtfsRealtime.Alert.SeverityLevel.SEVERE to Alert.SeverityLevel.Severe,
      )

    mapping.forEach { (official, expected) ->
      val alert = decodeAlert(GtfsRealtime.Alert.newBuilder().setSeverityLevel(official))
      assertEquals(expected, alert.severityLevel, "Mismatch for $official")
    }
  }

  @Test
  fun feedHeaderIncrementality() {
    val mapping =
      mapOf(
        GtfsRealtime.FeedHeader.Incrementality.FULL_DATASET to
          FeedHeader.Incrementality.FullDataset,
        GtfsRealtime.FeedHeader.Incrementality.DIFFERENTIAL to
          FeedHeader.Incrementality.Differential,
      )

    mapping.forEach { (official, expected) ->
      val feed =
        GtfsRealtime.FeedMessage.newBuilder()
          .setHeader(
            GtfsRealtime.FeedHeader.newBuilder()
              .setGtfsRealtimeVersion("2.0")
              .setIncrementality(official)
              .build()
          )
          .build()
      val decoded = GtfsRealtimeProto.decodeFeedMessage(feed.toByteArray())
      assertEquals(expected, decoded.header.incrementality, "Mismatch for $official")
    }
  }
}
