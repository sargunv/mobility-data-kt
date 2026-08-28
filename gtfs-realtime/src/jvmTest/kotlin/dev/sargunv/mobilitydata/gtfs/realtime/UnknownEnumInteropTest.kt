package dev.sargunv.mobilitydata.gtfs.realtime

import com.google.transit.realtime.GtfsRealtime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UnknownEnumInteropTest {
  @Test
  fun officialJavaKeepsUnknownCauseAndKotlinMatchesGetterDefault() {
    val tripEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "trip-ok"),
        ProtoWire.messageField(3, ProtoWire.messageField(1, ProtoWire.stringField(1, "trip-1"))),
      )
    val alertEntity =
      ProtoWire.concat(
        ProtoWire.stringField(1, "alert-unknown-cause"),
        ProtoWire.messageField(
          5,
          ProtoWire.concat(ProtoWire.varintField(6, 99), ProtoWire.varintField(7, 4)),
        ),
      )
    val bytes = feedMessageBytes(tripEntity, alertEntity)

    val official = GtfsRealtime.FeedMessage.parseFrom(bytes)
    assertEquals(2, official.entityCount)
    assertEquals("trip-1", official.getEntity(0).tripUpdate.trip.tripId)
    assertFalse(official.getEntity(1).alert.hasCause())
    assertEquals(GtfsRealtime.Alert.Cause.UNKNOWN_CAUSE, official.getEntity(1).alert.cause)
    assertEquals(GtfsRealtime.Alert.Effect.DETOUR, official.getEntity(1).alert.effect)

    val decoded = GtfsRealtimeProto.decodeFeedMessage(bytes)
    assertEquals("trip-1", decoded.entity[0].tripUpdate?.trip?.tripId)
    assertEquals(Alert.Cause.UnknownCause, decoded.entity[1].alert?.cause)
    assertEquals(Alert.Effect.Detour, decoded.entity[1].alert?.effect)
  }

  @Test
  fun officialJavaKeepsUnknownOccupancyAndKotlinTreatsFieldAsUnset() {
    val bytes =
      feedMessageBytes(
        ProtoWire.concat(
          ProtoWire.stringField(1, "vehicle-unknown-occupancy"),
          ProtoWire.messageField(
            4,
            ProtoWire.concat(ProtoWire.varintField(4, 1), ProtoWire.varintField(9, 99)),
          ),
        )
      )

    val official = GtfsRealtime.FeedMessage.parseFrom(bytes)
    assertFalse(official.getEntity(0).vehicle.hasOccupancyStatus())
    assertEquals(
      GtfsRealtime.VehiclePosition.OccupancyStatus.EMPTY,
      official.getEntity(0).vehicle.occupancyStatus,
    )
    assertEquals(
      GtfsRealtime.VehiclePosition.VehicleStopStatus.STOPPED_AT,
      official.getEntity(0).vehicle.currentStatus,
    )

    val decoded = GtfsRealtimeProto.decodeFeedMessage(bytes)
    assertNull(decoded.entity.single().vehicle?.occupancyStatus)
    assertEquals(
      VehiclePosition.VehicleStopStatus.StoppedAt,
      decoded.entity.single().vehicle?.currentStatus,
    )
  }

  @Test
  fun officialJavaKeepsKnownCauseWhenFollowedByUnknown() {
    val bytes =
      feedMessageBytes(
        ProtoWire.concat(
          ProtoWire.stringField(1, "alert-known-then-unknown"),
          ProtoWire.messageField(
            5,
            ProtoWire.concat(ProtoWire.varintField(6, 8), ProtoWire.varintField(6, 99)),
          ),
        )
      )

    val official = GtfsRealtime.FeedMessage.parseFrom(bytes)
    assertTrue(official.getEntity(0).alert.hasCause())
    assertEquals(GtfsRealtime.Alert.Cause.WEATHER, official.getEntity(0).alert.cause)

    val decoded = GtfsRealtimeProto.decodeFeedMessage(bytes)
    assertEquals(Alert.Cause.Weather, decoded.entity.single().alert?.cause)
  }

  @Test
  fun officialJavaKeepsKnownCauseWhenPrecededByUnknown() {
    val bytes =
      feedMessageBytes(
        ProtoWire.concat(
          ProtoWire.stringField(1, "alert-unknown-then-known"),
          ProtoWire.messageField(
            5,
            ProtoWire.concat(ProtoWire.varintField(6, 99), ProtoWire.varintField(6, 8)),
          ),
        )
      )

    val official = GtfsRealtime.FeedMessage.parseFrom(bytes)
    assertTrue(official.getEntity(0).alert.hasCause())
    assertEquals(GtfsRealtime.Alert.Cause.WEATHER, official.getEntity(0).alert.cause)

    val decoded = GtfsRealtimeProto.decodeFeedMessage(bytes)
    assertEquals(Alert.Cause.Weather, decoded.entity.single().alert?.cause)
  }

  @Test
  fun overlappingDecodesDoNotDropKnownThenUnknownCause() {
    val weatherThenUnknown =
      feedMessageBytes(
        ProtoWire.concat(
          ProtoWire.stringField(1, "alert-weather"),
          ProtoWire.messageField(
            5,
            ProtoWire.concat(ProtoWire.varintField(6, 8), ProtoWire.varintField(6, 99)),
          ),
        )
      )
    val constructionThenUnknown =
      feedMessageBytes(
        ProtoWire.concat(
          ProtoWire.stringField(1, "alert-construction"),
          ProtoWire.messageField(
            5,
            ProtoWire.concat(ProtoWire.varintField(6, 10), ProtoWire.varintField(6, 99)),
          ),
        )
      )

    val errors = java.util.Collections.synchronizedList(mutableListOf<String>())
    val threads =
      List(16) { index ->
        Thread {
          val bytes = if (index % 2 == 0) weatherThenUnknown else constructionThenUnknown
          val expected = if (index % 2 == 0) Alert.Cause.Weather else Alert.Cause.Construction
          repeat(80) {
            val decoded = GtfsRealtimeProto.decodeFeedMessage(bytes)
            val cause = decoded.entity.single().alert?.cause
            if (cause != expected) {
              errors += "thread $index expected $expected but was $cause"
            }
          }
        }
      }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    assertTrue(errors.isEmpty(), errors.take(8).joinToString(separator = "; "))
  }

  @Test
  fun officialJavaIgnoresPackedSingularCause() {
    val bytes =
      feedMessageBytes(
        ProtoWire.concat(
          ProtoWire.stringField(1, "alert-packed-cause"),
          ProtoWire.messageField(5, ProtoWire.packedVarintField(6, 8, 99)),
        )
      )

    val official = GtfsRealtime.FeedMessage.parseFrom(bytes)
    assertFalse(official.getEntity(0).alert.hasCause())
    assertEquals(GtfsRealtime.Alert.Cause.UNKNOWN_CAUSE, official.getEntity(0).alert.cause)

    val decoded = GtfsRealtimeProto.decodeFeedMessage(bytes)
    assertEquals(Alert.Cause.UnknownCause, decoded.entity.single().alert?.cause)
  }

  @Test
  fun officialJavaDoesNotReuseEarlierCarriageOccupancy() {
    val bytes =
      feedMessageBytes(
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
      )

    val official = GtfsRealtime.FeedMessage.parseFrom(bytes)
    assertEquals(2, official.getEntity(0).vehicle.multiCarriageDetailsCount)
    assertEquals(
      GtfsRealtime.VehiclePosition.OccupancyStatus.FULL,
      official.getEntity(0).vehicle.getMultiCarriageDetails(0).occupancyStatus,
    )
    assertFalse(official.getEntity(0).vehicle.getMultiCarriageDetails(1).hasOccupancyStatus())
    assertEquals(
      GtfsRealtime.VehiclePosition.OccupancyStatus.NO_DATA_AVAILABLE,
      official.getEntity(0).vehicle.getMultiCarriageDetails(1).occupancyStatus,
    )

    val decoded = GtfsRealtimeProto.decodeFeedMessage(bytes)
    val carriages = decoded.entity.single().vehicle?.multiCarriageDetails.orEmpty()
    assertEquals(VehiclePosition.OccupancyStatus.Full, carriages[0].occupancyStatus)
    assertEquals(VehiclePosition.OccupancyStatus.NoDataAvailable, carriages[1].occupancyStatus)
  }
}
