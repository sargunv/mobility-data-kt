package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test
import kotlin.test.assertEquals

internal val sampleFeedMessage =
  FeedMessage(
    header =
      FeedHeader(
        gtfsRealtimeVersion = "2.0",
        timestamp = 1_744_470_400,
        feedVersion = "spring-2026",
      ),
    entity =
      listOf(
        FeedEntity(
          id = "trip-update-1",
          tripUpdate =
            TripUpdate(
              trip = TripDescriptor(tripId = "trip-1", routeId = "route-a", startDate = "20260412"),
              vehicle = VehicleDescriptor(id = "vehicle-7", label = "Train 7"),
              stopTimeUpdate =
                listOf(
                  TripUpdate.StopTimeUpdate(
                    stopSequence = 3,
                    stopId = "stop-3",
                    arrival = TripUpdate.StopTimeEvent(delay = 45, time = 1_744_470_445),
                    departure = TripUpdate.StopTimeEvent(delay = 60, time = 1_744_470_460),
                  )
                ),
              timestamp = 1_744_470_400,
              delay = 45,
            ),
        ),
        FeedEntity(
          id = "vehicle-1",
          vehicle =
            VehiclePosition(
              trip = TripDescriptor(tripId = "trip-1", routeId = "route-a"),
              vehicle = VehicleDescriptor(id = "vehicle-7", label = "Train 7"),
              position = Position(latitude = 47.6097F, longitude = -122.3331F, speed = 11.5F),
              currentStopSequence = 3,
              stopId = "stop-3",
              currentStatus = VehiclePosition.VehicleStopStatus.StoppedAt,
              timestamp = 1_744_470_405,
              occupancyStatus = VehiclePosition.OccupancyStatus.Full,
            ),
        ),
        FeedEntity(
          id = "alert-1",
          alert =
            Alert(
              informedEntity = listOf(EntitySelector(routeId = "route-a")),
              cause = Alert.Cause.Weather,
              effect = Alert.Effect.Detour,
              headerText =
                TranslatedString(
                  translation =
                    listOf(
                      TranslatedString.Translation(text = "Detour on Route A", language = "en")
                    )
                ),
              severityLevel = Alert.SeverityLevel.Warning,
            ),
        ),
      ),
  )

class FeedMessageTest {
  @Test
  fun roundTripsFeedMessage() {
    val encoded = GtfsRealtimeProto.encodeFeedMessage(sampleFeedMessage)
    val decoded = GtfsRealtimeProto.decodeFeedMessage(encoded)

    assertEquals(sampleFeedMessage, decoded)
  }
}
