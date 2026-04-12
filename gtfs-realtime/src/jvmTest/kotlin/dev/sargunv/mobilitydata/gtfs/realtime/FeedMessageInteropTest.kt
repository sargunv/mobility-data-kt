package dev.sargunv.mobilitydata.gtfs.realtime

import com.google.transit.realtime.GtfsRealtime
import kotlin.test.Test
import kotlin.test.assertEquals

class FeedMessageInteropTest {
  @Test
  fun decodesOfficialJavaBindingsPayload() {
    val officialFeed =
      GtfsRealtime.FeedMessage.newBuilder()
        .setHeader(
          GtfsRealtime.FeedHeader.newBuilder()
            .setGtfsRealtimeVersion("2.0")
            .setTimestamp(1_744_470_400)
            .setIncrementality(GtfsRealtime.FeedHeader.Incrementality.FULL_DATASET)
            .build()
        )
        .addEntity(
          GtfsRealtime.FeedEntity.newBuilder()
            .setId("trip-update-1")
            .setTripUpdate(
              GtfsRealtime.TripUpdate.newBuilder()
                .setTrip(
                  GtfsRealtime.TripDescriptor.newBuilder()
                    .setTripId("trip-1")
                    .setRouteId("route-a")
                    .setStartDate("20260412")
                    .build()
                )
                .setVehicle(
                  GtfsRealtime.VehicleDescriptor.newBuilder()
                    .setId("vehicle-7")
                    .setLabel("Train 7")
                    .build()
                )
                .addStopTimeUpdate(
                  GtfsRealtime.TripUpdate.StopTimeUpdate.newBuilder()
                    .setStopSequence(3)
                    .setStopId("stop-3")
                    .setArrival(
                      GtfsRealtime.TripUpdate.StopTimeEvent.newBuilder()
                        .setDelay(45)
                        .setTime(1_744_470_445)
                        .build()
                    )
                    .setDeparture(
                      GtfsRealtime.TripUpdate.StopTimeEvent.newBuilder()
                        .setDelay(60)
                        .setTime(1_744_470_460)
                        .build()
                    )
                    .setScheduleRelationship(
                      GtfsRealtime.TripUpdate.StopTimeUpdate.ScheduleRelationship.SCHEDULED
                    )
                    .build()
                )
                .setTimestamp(1_744_470_400)
                .setDelay(45)
                .build()
            )
            .build()
        )
        .addEntity(
          GtfsRealtime.FeedEntity.newBuilder()
            .setId("vehicle-1")
            .setVehicle(
              GtfsRealtime.VehiclePosition.newBuilder()
                .setTrip(
                  GtfsRealtime.TripDescriptor.newBuilder()
                    .setTripId("trip-1")
                    .setRouteId("route-a")
                    .build()
                )
                .setVehicle(
                  GtfsRealtime.VehicleDescriptor.newBuilder()
                    .setId("vehicle-7")
                    .setLabel("Train 7")
                    .build()
                )
                .setPosition(
                  GtfsRealtime.Position.newBuilder()
                    .setLatitude(47.6097F)
                    .setLongitude(-122.3331F)
                    .setSpeed(11.5F)
                    .build()
                )
                .setCurrentStopSequence(3)
                .setStopId("stop-3")
                .setCurrentStatus(GtfsRealtime.VehiclePosition.VehicleStopStatus.STOPPED_AT)
                .setTimestamp(1_744_470_405)
                .setOccupancyStatus(GtfsRealtime.VehiclePosition.OccupancyStatus.FULL)
                .build()
            )
            .build()
        )
        .addEntity(
          GtfsRealtime.FeedEntity.newBuilder()
            .setId("alert-1")
            .setAlert(
              GtfsRealtime.Alert.newBuilder()
                .addInformedEntity(
                  GtfsRealtime.EntitySelector.newBuilder().setRouteId("route-a").build()
                )
                .setCause(GtfsRealtime.Alert.Cause.WEATHER)
                .setEffect(GtfsRealtime.Alert.Effect.DETOUR)
                .setHeaderText(
                  GtfsRealtime.TranslatedString.newBuilder()
                    .addTranslation(
                      GtfsRealtime.TranslatedString.Translation.newBuilder()
                        .setText("Detour on Route A")
                        .setLanguage("en")
                        .build()
                    )
                    .build()
                )
                .setSeverityLevel(GtfsRealtime.Alert.SeverityLevel.WARNING)
                .build()
            )
            .build()
        )
        .build()

    val decoded = GtfsRealtimeProto.decodeFeedMessage(officialFeed.toByteArray())

    assertEquals("2.0", decoded.header.gtfsRealtimeVersion)
    assertEquals(3, decoded.entity.size)
    assertEquals("trip-1", decoded.entity[0].tripUpdate?.trip?.tripId)
    assertEquals(45, decoded.entity[0].tripUpdate?.delay)
    assertEquals(VehiclePosition.OccupancyStatus.Full, decoded.entity[1].vehicle?.occupancyStatus)
    assertEquals(Alert.Cause.Weather, decoded.entity[2].alert?.cause)
    assertEquals(Alert.Effect.Detour, decoded.entity[2].alert?.effect)
    assertEquals(
      "Detour on Route A",
      decoded.entity[2].alert?.headerText?.translation?.single()?.text,
    )
  }

  @Test
  fun encodesPayloadReadableByOfficialJavaBindings() {
    val encoded = GtfsRealtimeProto.encodeFeedMessage(sampleFeedMessage)
    val parsed = GtfsRealtime.FeedMessage.parseFrom(encoded)

    assertEquals("2.0", parsed.header.gtfsRealtimeVersion)
    assertEquals("trip-1", parsed.entityList[0].tripUpdate.trip.tripId)
    assertEquals(45, parsed.entityList[0].tripUpdate.delay)
    assertEquals(
      GtfsRealtime.VehiclePosition.OccupancyStatus.FULL,
      parsed.entityList[1].vehicle.occupancyStatus,
    )
    assertEquals(GtfsRealtime.Alert.Cause.WEATHER, parsed.entityList[2].alert.cause)
    assertEquals(GtfsRealtime.Alert.Effect.DETOUR, parsed.entityList[2].alert.effect)
    assertEquals(
      "Detour on Route A",
      parsed.entityList[2].alert.headerText.translationList.single().text,
    )
  }

  @Test
  fun decodesRealFixturesConsistentlyWithOfficialBindings() {
    val fixtures =
      listOf(
        readFixtureBytes("gtfs-realtime", "sound-transit", "alerts.pb"),
        readFixtureBytes("gtfs-realtime", "tri-rail", "trip_updates.pb"),
        readFixtureBytes("gtfs-realtime", "tri-rail", "vehicle_positions.pb"),
        readFixtureBytes("gtfs-realtime", "tri-rail", "alerts.pb"),
      )

    fixtures.forEach { bytes ->
      val kotlinFeed = GtfsRealtimeProto.decodeFeedMessage(bytes)
      val officialFeed = GtfsRealtime.FeedMessage.parseFrom(bytes)

      assertEquals(officialFeed.entityCount, kotlinFeed.entity.size)
      assertEquals(officialFeed.header.gtfsRealtimeVersion, kotlinFeed.header.gtfsRealtimeVersion)

      // Spot-check entity-level fields for each type present
      officialFeed.entityList.zip(kotlinFeed.entity).forEach { (official, kotlin) ->
        assertEquals(official.id, kotlin.id)
        if (official.hasTripUpdate()) {
          assertEquals(official.tripUpdate.trip.tripId, kotlin.tripUpdate?.trip?.tripId.orEmpty())
          assertEquals(official.tripUpdate.trip.routeId, kotlin.tripUpdate?.trip?.routeId.orEmpty())
          assertEquals(
            official.tripUpdate.stopTimeUpdateCount,
            kotlin.tripUpdate?.stopTimeUpdate?.size,
          )
        }
        if (official.hasVehicle()) {
          assertEquals(official.vehicle.vehicle.id, kotlin.vehicle?.vehicle?.id.orEmpty())
          if (official.vehicle.hasPosition()) {
            assertEquals(official.vehicle.position.latitude, kotlin.vehicle?.position?.latitude)
          }
        }
        if (official.hasAlert()) {
          assertEquals(official.alert.informedEntityCount, kotlin.alert?.informedEntity?.size)
        }
      }
    }
  }
}
