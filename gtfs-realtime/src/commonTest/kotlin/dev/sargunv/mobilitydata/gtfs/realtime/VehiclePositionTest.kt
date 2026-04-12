package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test

class VehiclePositionTest {
  @Test
  fun roundTripsVehiclePositionEntities() {
    val feed =
      FeedMessage(
        header = FeedHeader(gtfsRealtimeVersion = "2.0"),
        entity =
          listOf(
            FeedEntity(
              id = "vehicle-position-advanced",
              vehicle =
                VehiclePosition(
                  trip =
                    TripDescriptor(
                      tripId = "trip-7",
                      routeId = "route-c",
                      directionId = 1,
                      scheduleRelationship = TripDescriptor.ScheduleRelationship.Scheduled,
                    ),
                  vehicle =
                    VehicleDescriptor(
                      id = "vehicle-44",
                      label = "Carset 44",
                      wheelchairAccessible =
                        VehicleDescriptor.WheelchairAccessible.WheelchairInaccessible,
                    ),
                  position =
                    Position(
                      latitude = 49.2827F,
                      longitude = -123.1207F,
                      bearing = 182.5F,
                      odometer = 12_345.67,
                      speed = 13.25F,
                    ),
                  currentStopSequence = 22,
                  stopId = "stop-22",
                  currentStatus = VehiclePosition.VehicleStopStatus.IncomingAt,
                  timestamp = 1_744_600_000,
                  congestionLevel = VehiclePosition.CongestionLevel.StopAndGo,
                  occupancyStatus = VehiclePosition.OccupancyStatus.CrushedStandingRoomOnly,
                  occupancyPercentage = 112,
                  multiCarriageDetails =
                    listOf(
                      VehiclePosition.CarriageDetails(
                        id = "car-a",
                        label = "A",
                        occupancyStatus = VehiclePosition.OccupancyStatus.ManySeatsAvailable,
                        occupancyPercentage = 35,
                        carriageSequence = 1,
                      ),
                      VehiclePosition.CarriageDetails(
                        id = "car-b",
                        label = "B",
                        occupancyStatus = VehiclePosition.OccupancyStatus.Full,
                        occupancyPercentage = 100,
                        carriageSequence = 2,
                      ),
                    ),
                ),
            )
          ),
      )

    assertFeedRoundTrips(feed)
  }
}
