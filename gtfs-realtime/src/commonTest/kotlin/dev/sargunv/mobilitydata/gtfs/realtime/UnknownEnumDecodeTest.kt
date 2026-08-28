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
    assertEquals(
      TripDescriptor.ScheduleRelationship.Scheduled,
      tripUpdate?.trip?.scheduleRelationship,
    )
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
