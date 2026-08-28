package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TripUpdateTest {
  @Test
  fun roundTripsTripUpdateEntities() {
    val feed =
      FeedMessage(
        header = FeedHeader(gtfsRealtimeVersion = "2.0"),
        entity =
          listOf(
            FeedEntity(
              id = "trip-update-advanced",
              tripUpdate =
                TripUpdate(
                  trip =
                    TripDescriptor(
                      tripId = "trip-2",
                      routeId = "route-b",
                      startTime = "25:15:00",
                      startDate = "20260413",
                      scheduleRelationship = TripDescriptor.ScheduleRelationship.Duplicated,
                      modifiedTrip =
                        TripDescriptor.ModifiedTripSelector(
                          modificationsId = "mods-1",
                          affectedTripId = "trip-base",
                          startTime = "25:15:00",
                          startDate = "20260413",
                        ),
                    ),
                  vehicle =
                    VehicleDescriptor(
                      id = "vehicle-9",
                      label = "Train 9",
                      licensePlate = "TEST-9",
                      wheelchairAccessible =
                        VehicleDescriptor.WheelchairAccessible.WheelchairAccessible,
                    ),
                  stopTimeUpdate =
                    listOf(
                      TripUpdate.StopTimeUpdate(
                        stopSequence = 10,
                        stopId = "stop-10",
                        arrival =
                          TripUpdate.StopTimeEvent(
                            delay = -30,
                            time = 1_744_556_770,
                            uncertainty = 5,
                            scheduledTime = 1_744_556_800,
                          ),
                        departure = TripUpdate.StopTimeEvent(delay = -15, time = 1_744_556_785),
                        departureOccupancyStatus =
                          VehiclePosition.OccupancyStatus.FewSeatsAvailable,
                        scheduleRelationship =
                          TripUpdate.StopTimeUpdate.ScheduleRelationship.Unscheduled,
                        stopTimeProperties =
                          TripUpdate.StopTimeUpdate.StopTimeProperties(
                            assignedStopId = "platform-2",
                            stopHeadsign = "Downtown",
                            pickupType =
                              TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType
                                .PhoneAgency,
                            dropOffType =
                              TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType
                                .CoordinateWithDriver,
                          ),
                      )
                    ),
                  timestamp = 1_744_556_700,
                  delay = -30,
                  tripProperties =
                    TripUpdate.TripProperties(
                      tripId = "trip-2-dup",
                      startDate = "20260413",
                      startTime = "25:15:00",
                      shapeId = "shape-detour-2",
                      tripHeadsign = "Downtown Express",
                      tripShortName = "DX",
                    ),
                ),
            )
          ),
      )

    assertFeedRoundTrips(feed)
  }

  @Test
  fun distinguishesAbsentAndExplicitScheduled() {
    val absentFeed =
      FeedMessage(
        header = FeedHeader(gtfsRealtimeVersion = "2.0"),
        entity =
          listOf(
            FeedEntity(
              id = "absent",
              tripUpdate = TripUpdate(trip = TripDescriptor(tripId = "trip-absent")),
            )
          ),
      )
    assertFeedRoundTrips(absentFeed)
    assertNull(absentFeed.entity.single().tripUpdate?.trip?.scheduleRelationship)

    val scheduledFeed =
      FeedMessage(
        header = FeedHeader(gtfsRealtimeVersion = "2.0"),
        entity =
          listOf(
            FeedEntity(
              id = "scheduled",
              tripUpdate =
                TripUpdate(
                  trip =
                    TripDescriptor(
                      tripId = "trip-scheduled",
                      scheduleRelationship = TripDescriptor.ScheduleRelationship.Scheduled,
                    )
                ),
            )
          ),
      )
    assertFeedRoundTrips(scheduledFeed)
    assertEquals(
      TripDescriptor.ScheduleRelationship.Scheduled,
      scheduledFeed.entity.single().tripUpdate?.trip?.scheduleRelationship,
    )

    val encodedAbsent = GtfsRealtimeProto.encodeFeedMessage(absentFeed)
    val encodedScheduled = GtfsRealtimeProto.encodeFeedMessage(scheduledFeed)
    assertEquals(
      null,
      GtfsRealtimeProto.decodeFeedMessage(encodedAbsent)
        .entity
        .single()
        .tripUpdate
        ?.trip
        ?.scheduleRelationship,
    )
    assertEquals(
      TripDescriptor.ScheduleRelationship.Scheduled,
      GtfsRealtimeProto.decodeFeedMessage(encodedScheduled)
        .entity
        .single()
        .tripUpdate
        ?.trip
        ?.scheduleRelationship,
    )
  }
}
