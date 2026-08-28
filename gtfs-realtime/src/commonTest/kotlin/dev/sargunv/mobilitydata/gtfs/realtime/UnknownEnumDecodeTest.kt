package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber

class UnknownEnumDecodeTest {
  @Test
  fun unknownAlertCauseDoesNotAbortFeed() {
    val tripEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "trip-ok"),
        ProtoWire.messageField(
          3,
          ProtoWire.concat(
            ProtoWire.messageField(1, ProtoWire.stringField(1, "trip-1")),
            ProtoWire.varintField(5, 30),
          ),
        ),
      )
    val alertEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "alert-unknown-cause"),
        ProtoWire.messageField(
          5,
          ProtoWire.concat(
            ProtoWire.messageField(5, ProtoWire.stringField(2, "route-a")),
            ProtoWire.varintField(6, 99),
            ProtoWire.varintField(7, 4),
            ProtoWire.messageField(
              10,
              ProtoWire.messageField(
                1,
                ProtoWire.concat(
                  ProtoWire.stringField(1, "Detour on Route A"),
                  ProtoWire.stringField(2, "en"),
                ),
              ),
            ),
          ),
        ),
      )

    val decoded = GtfsRealtimeProto.decodeFeedMessage(feedMessageBytes(tripEntity, alertEntity))

    assertEquals("2.0", decoded.header.gtfsRealtimeVersion)
    assertEquals(2, decoded.entity.size)

    val tripUpdate = decoded.entity[0].tripUpdate
    assertEquals("trip-ok", decoded.entity[0].id)
    assertEquals("trip-1", tripUpdate?.trip?.tripId)
    assertEquals(30, tripUpdate?.delay)

    val alert = decoded.entity[1].alert
    assertEquals("alert-unknown-cause", decoded.entity[1].id)
    assertEquals("route-a", alert?.informedEntity?.single()?.routeId)
    assertEquals(Alert.Cause.UnknownCause, alert?.cause)
    assertEquals(Alert.Effect.Detour, alert?.effect)
    assertEquals("Detour on Route A", alert?.headerText?.translation?.single()?.text)
    assertEquals("en", alert?.headerText?.translation?.single()?.language)
  }

  @Test
  fun unknownOccupancyStatusDoesNotAbortFeed() {
    val vehicleEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "vehicle-unknown-occupancy"),
        ProtoWire.messageField(
          4,
          ProtoWire.concat(
            ProtoWire.messageField(1, ProtoWire.stringField(1, "trip-7")),
            ProtoWire.varintField(4, 1),
            ProtoWire.varintField(9, 99),
          ),
        ),
      )
    val alertEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "alert-ok"),
        ProtoWire.messageField(5, ProtoWire.varintField(7, 1)),
      )

    val decoded = GtfsRealtimeProto.decodeFeedMessage(feedMessageBytes(vehicleEntity, alertEntity))

    assertEquals(2, decoded.entity.size)
    assertEquals("vehicle-unknown-occupancy", decoded.entity[0].id)
    assertEquals("trip-7", decoded.entity[0].vehicle?.trip?.tripId)
    assertEquals(
      VehiclePosition.VehicleStopStatus.StoppedAt,
      decoded.entity[0].vehicle?.currentStatus,
    )
    assertNull(decoded.entity[0].vehicle?.occupancyStatus)
    assertEquals("alert-ok", decoded.entity[1].id)
    assertEquals(Alert.Effect.NoService, decoded.entity[1].alert?.effect)
  }

  @Test
  fun unknownTripScheduleRelationshipDoesNotAbortFeed() {
    val tripEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "trip-unknown-relationship"),
        ProtoWire.messageField(
          3,
          ProtoWire.concat(
            ProtoWire.messageField(
              1,
              ProtoWire.concat(ProtoWire.stringField(1, "trip-9"), ProtoWire.varintField(4, 99)),
            ),
            ProtoWire.varintField(5, 12),
          ),
        ),
      )

    val decoded = GtfsRealtimeProto.decodeFeedMessage(feedMessageBytes(tripEntity))

    val tripUpdate = decoded.entity.single().tripUpdate
    assertEquals("trip-unknown-relationship", decoded.entity.single().id)
    assertEquals("trip-9", tripUpdate?.trip?.tripId)
    assertNull(tripUpdate?.trip?.scheduleRelationship)
    assertEquals(12, tripUpdate?.delay)
  }

  @OptIn(ExperimentalSerializationApi::class)
  @Test
  fun generatedProtobufEnumSerializerStillAbortsOnUnknownNumber() {
    val result = runCatching {
      ProtoBuf { encodeDefaults = false }
        .decodeFromByteArray(StrictAlert.serializer(), ProtoWire.varintField(6, 99))
    }
    assertTrue(result.isFailure, "Expected generated protobuf enum decode to fail for cause=99")
    assertIs<SerializationException>(result.exceptionOrNull())
  }

  @Test
  fun knownCauseFollowedByUnknownKeepsRecognizedValue() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "alert-known-then-unknown"),
            ProtoWire.messageField(
              5,
              ProtoWire.concat(ProtoWire.varintField(6, 8), ProtoWire.varintField(6, 99)),
            ),
          )
        )
      )

    assertEquals(Alert.Cause.Weather, decoded.entity.single().alert?.cause)
  }

  @Test
  fun unknownCauseFollowedByKnownKeepsRecognizedValue() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "alert-unknown-then-known"),
            ProtoWire.messageField(
              5,
              ProtoWire.concat(ProtoWire.varintField(6, 99), ProtoWire.varintField(6, 8)),
            ),
          )
        )
      )

    assertEquals(Alert.Cause.Weather, decoded.entity.single().alert?.cause)
  }

  @Test
  fun unknownCauseInSecondAlertDoesNotReuseFirstAlertCause() {
    val weatherAlert =
      ProtoWire.concat(
        ProtoWire.stringField(1, "alert-weather"),
        ProtoWire.messageField(5, ProtoWire.varintField(6, 8)),
      )
    val unknownAlert =
      ProtoWire.concat(
        ProtoWire.stringField(1, "alert-unknown"),
        ProtoWire.messageField(5, ProtoWire.varintField(6, 99)),
      )

    val decoded = GtfsRealtimeProto.decodeFeedMessage(feedMessageBytes(weatherAlert, unknownAlert))

    assertEquals(Alert.Cause.Weather, decoded.entity[0].alert?.cause)
    assertEquals(Alert.Cause.UnknownCause, decoded.entity[1].alert?.cause)
  }

  @Test
  fun unknownDropOffDoesNotReusePickupType() {
    val stopTimeProperties =
      ProtoWire.concat(ProtoWire.varintField(3, 0), ProtoWire.varintField(4, 99))
    val tripEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "trip-pickup-dropoff"),
        ProtoWire.messageField(
          3,
          ProtoWire.concat(
            ProtoWire.messageField(1, ProtoWire.stringField(1, "trip-1")),
            ProtoWire.messageField(
              2,
              ProtoWire.concat(
                ProtoWire.varintField(1, 1),
                ProtoWire.messageField(6, stopTimeProperties),
              ),
            ),
          ),
        ),
      )

    val decoded = GtfsRealtimeProto.decodeFeedMessage(feedMessageBytes(tripEntity))
    val properties =
      decoded.entity.single().tripUpdate?.stopTimeUpdate?.single()?.stopTimeProperties

    assertEquals(
      TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType.Regular,
      properties?.pickupType,
    )
    assertNull(properties?.dropOffType)
  }

  @Test
  fun unknownIncrementalityDoesNotAbortFeed() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        ProtoWire.concat(
          ProtoWire.messageField(
            1,
            ProtoWire.concat(ProtoWire.stringField(1, "2.0"), ProtoWire.varintField(2, 99)),
          )
        )
      )

    assertEquals("2.0", decoded.header.gtfsRealtimeVersion)
    assertEquals(FeedHeader.Incrementality.FullDataset, decoded.header.incrementality)
  }

  @Test
  fun unknownCarriageOccupancyDoesNotReuseEarlierCarriage() {
    val vehicleEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "vehicle-carriages"),
        ProtoWire.messageField(
          4,
          ProtoWire.concat(
            ProtoWire.varintField(4, 1),
            ProtoWire.messageField(11, ProtoWire.varintField(3, 5)),
            ProtoWire.messageField(11, ProtoWire.varintField(3, 99)),
          ),
        ),
      )

    val decoded = GtfsRealtimeProto.decodeFeedMessage(feedMessageBytes(vehicleEntity))
    val carriages = decoded.entity.single().vehicle?.multiCarriageDetails.orEmpty()

    assertEquals(2, carriages.size)
    assertEquals(VehiclePosition.OccupancyStatus.Full, carriages[0].occupancyStatus)
    assertEquals(VehiclePosition.OccupancyStatus.NoDataAvailable, carriages[1].occupancyStatus)
  }

  @Test
  fun unknownCarriageOccupancyDoesNotAbortFeed() {
    val vehicleEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "vehicle-carriage"),
        ProtoWire.messageField(
          4,
          ProtoWire.concat(
            ProtoWire.varintField(4, 1),
            ProtoWire.messageField(11, ProtoWire.varintField(3, 99)),
          ),
        ),
      )

    val decoded = GtfsRealtimeProto.decodeFeedMessage(feedMessageBytes(vehicleEntity))
    assertEquals(
      VehiclePosition.VehicleStopStatus.StoppedAt,
      decoded.entity.single().vehicle?.currentStatus,
    )
    assertEquals(
      VehiclePosition.OccupancyStatus.NoDataAvailable,
      decoded.entity.single().vehicle?.multiCarriageDetails?.single()?.occupancyStatus,
    )
  }

  @Test
  fun packedCauseWrongWireTypeDoesNotAbortFeed() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "alert-packed-cause"),
            ProtoWire.messageField(5, ProtoWire.packedVarintField(6, 8, 99)),
          )
        )
      )

    assertEquals("alert-packed-cause", decoded.entity.single().id)
    assertEquals(Alert.Cause.UnknownCause, decoded.entity.single().alert?.cause)
  }

  @Test
  fun unknownNestedTripScheduleRelationshipDoesNotAbortFeed() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "alert-nested-trip"),
            ProtoWire.messageField(
              5,
              ProtoWire.concat(
                ProtoWire.messageField(
                  5,
                  ProtoWire.messageField(
                    4,
                    ProtoWire.concat(
                      ProtoWire.stringField(1, "trip-2"),
                      ProtoWire.varintField(4, 99),
                    ),
                  ),
                ),
                ProtoWire.varintField(7, 4),
              ),
            ),
          )
        )
      )

    val selector = decoded.entity.single().alert?.informedEntity?.single()
    assertEquals("trip-2", selector?.trip?.tripId)
    assertNull(selector?.trip?.scheduleRelationship)
    assertEquals(Alert.Effect.Detour, decoded.entity.single().alert?.effect)
  }

  @Test
  fun knownThenUnknownIncrementalityKeepsRecognizedValue() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        ProtoWire.concat(
          ProtoWire.messageField(
            1,
            ProtoWire.concat(
              ProtoWire.stringField(1, "2.0"),
              ProtoWire.varintField(2, 1),
              ProtoWire.varintField(2, 99),
            ),
          )
        )
      )

    assertEquals(FeedHeader.Incrementality.Differential, decoded.header.incrementality)
  }

  @Test
  fun unknownStopWheelchairBoardingDoesNotAbortFeed() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "stop-unknown-wheelchair"),
            ProtoWire.messageField(
              7,
              ProtoWire.concat(ProtoWire.stringField(1, "stop-1"), ProtoWire.varintField(13, 99)),
            ),
          )
        )
      )

    assertEquals("stop-1", decoded.entity.single().stop?.stopId)
    assertEquals(Stop.WheelchairBoarding.Unknown, decoded.entity.single().stop?.wheelchairBoarding)
  }

  @Test
  fun knownThenUnknownWheelchairAccessibleKeepsRecognizedValue() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "vehicle-wheelchair"),
            ProtoWire.messageField(
              4,
              ProtoWire.messageField(
                8,
                ProtoWire.concat(ProtoWire.varintField(4, 2), ProtoWire.varintField(4, 99)),
              ),
            ),
          )
        )
      )

    assertEquals(
      VehicleDescriptor.WheelchairAccessible.WheelchairAccessible,
      decoded.entity.single().vehicle?.vehicle?.wheelchairAccessible,
    )
  }

  @Test
  fun knownThenUnknownOccupancyKeepsRecognizedValue() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "vehicle-occupancy"),
            ProtoWire.messageField(
              4,
              ProtoWire.concat(ProtoWire.varintField(9, 5), ProtoWire.varintField(9, 99)),
            ),
          )
        )
      )

    assertEquals(
      VehiclePosition.OccupancyStatus.Full,
      decoded.entity.single().vehicle?.occupancyStatus,
    )
  }

  @Test
  fun knownThenUnknownStopTimeScheduleKeepsRecognizedValue() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "trip-stop-schedule"),
            ProtoWire.messageField(
              3,
              ProtoWire.concat(
                ProtoWire.messageField(1, ProtoWire.stringField(1, "trip-1")),
                ProtoWire.messageField(
                  2,
                  ProtoWire.concat(ProtoWire.varintField(5, 1), ProtoWire.varintField(5, 99)),
                ),
              ),
            ),
          )
        )
      )

    assertEquals(
      TripUpdate.StopTimeUpdate.ScheduleRelationship.Skipped,
      decoded.entity.single().tripUpdate?.stopTimeUpdate?.single()?.scheduleRelationship,
    )
  }

  @Test
  fun unknownCongestionDoesNotAbortFeed() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "vehicle-congestion"),
            ProtoWire.messageField(
              4,
              ProtoWire.concat(ProtoWire.varintField(4, 1), ProtoWire.varintField(6, 99)),
            ),
          )
        )
      )

    assertEquals(
      VehiclePosition.VehicleStopStatus.StoppedAt,
      decoded.entity.single().vehicle?.currentStatus,
    )
    assertNull(decoded.entity.single().vehicle?.congestionLevel)
  }

  @Test
  fun derivedSchemaIncludesEveryRealtimeEnumField() {
    val feed = GtfsRealtimeEnumSchema.feedMessage
    val header = feed.messages.getValue(1)
    val entity = feed.messages.getValue(2)
    val tripUpdate = entity.messages.getValue(3)
    val vehicle = entity.messages.getValue(4)
    val alert = entity.messages.getValue(5)
    val stop = entity.messages.getValue(7)
    val tripDescriptor = tripUpdate.messages.getValue(1)
    val stopTimeUpdate = tripUpdate.messages.getValue(2)
    val vehicleDescriptor = tripUpdate.messages.getValue(3)
    val stopTimeProperties = stopTimeUpdate.messages.getValue(6)
    val carriage = vehicle.messages.getValue(11)
    val informedTrip = alert.messages.getValue(5).messages.getValue(4)

    assertEquals(setOf(0, 1), header.enums.getValue(2))
    assertTrue(4 in tripDescriptor.enums)
    assertTrue(5 in stopTimeUpdate.enums)
    assertTrue(7 in stopTimeUpdate.enums)
    assertTrue(3 in stopTimeProperties.enums)
    assertTrue(4 in stopTimeProperties.enums)
    assertTrue(4 in vehicleDescriptor.enums)
    assertTrue(4 in vehicle.enums)
    assertTrue(6 in vehicle.enums)
    assertTrue(9 in vehicle.enums)
    assertTrue(3 in carriage.enums)
    assertTrue(6 in alert.enums)
    assertTrue(7 in alert.enums)
    assertTrue(14 in alert.enums)
    assertTrue(13 in stop.enums)
    assertTrue(4 in informedTrip.enums)
    assertTrue(4 in vehicle.messages.getValue(1).enums)
    assertTrue(6 !in entity.messages)
    assertTrue(8 !in entity.messages)
  }

  @Test
  fun knownEnumValuesStillRoundTrip() {
    assertFeedRoundTrips(
      FeedMessage(
        header = FeedHeader(gtfsRealtimeVersion = "2.0"),
        entity =
          listOf(
            FeedEntity(
              id = "known-enums",
              alert =
                Alert(
                  informedEntity = listOf(EntitySelector(routeId = "route-a")),
                  cause = Alert.Cause.Weather,
                  effect = Alert.Effect.Detour,
                  severityLevel = Alert.SeverityLevel.Warning,
                ),
              vehicle =
                VehiclePosition(
                  trip =
                    TripDescriptor(
                      tripId = "trip-1",
                      scheduleRelationship = TripDescriptor.ScheduleRelationship.Canceled,
                    ),
                  occupancyStatus = VehiclePosition.OccupancyStatus.Full,
                  currentStatus = VehiclePosition.VehicleStopStatus.StoppedAt,
                ),
            )
          ),
      )
    )
  }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
private data class StrictAlert(@ProtoNumber(6) val cause: StrictCause = StrictCause.UnknownCause) {
  @Serializable
  enum class StrictCause {
    @ProtoNumber(1) UnknownCause,
    @ProtoNumber(8) Weather,
  }
}
