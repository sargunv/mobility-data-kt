package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlinx.serialization.SerializationException

class MismatchedWireTypeDecodeTest {
  private fun translatedString(text: String): ByteArray =
    ProtoWire.messageField(1, ProtoWire.stringField(1, text))

  private fun alertEntity(id: String, vararg alertFields: ByteArray): ByteArray =
    ProtoWire.concat(
      ProtoWire.stringField(1, id),
      ProtoWire.messageField(5, ProtoWire.concat(*alertFields)),
    )

  @Test
  fun varintInAlertTtsHeaderTextIsSkipped() {
    val wmataAlert =
      alertEntity(
        "wmata-alert",
        ProtoWire.messageField(5, ProtoWire.stringField(2, "RED")),
        ProtoWire.varintField(7, 3),
        ProtoWire.messageField(10, translatedString("Red Line delays")),
        ProtoWire.messageField(11, translatedString("Trains are single-tracking")),
        ProtoWire.varintField(12, 4),
        ProtoWire.messageField(13, translatedString("Red Line trains are single-tracking")),
        ProtoWire.varintField(14, 3),
      )
    val otherAlert = alertEntity("other-alert", ProtoWire.varintField(7, 1))

    val decoded = GtfsRealtimeProto.decodeFeedMessage(feedMessageBytes(wmataAlert, otherAlert))

    assertEquals(listOf("wmata-alert", "other-alert"), decoded.entity.map { it.id })
    val alert = decoded.entity[0].alert
    assertEquals("RED", alert?.informedEntity?.single()?.routeId)
    assertEquals(Alert.Effect.SignificantDelays, alert?.effect)
    assertEquals("Red Line delays", alert?.headerText?.translation?.single()?.text)
    assertEquals("Trains are single-tracking", alert?.descriptionText?.translation?.single()?.text)
    assertNull(alert?.ttsHeaderText)
    assertEquals(
      "Red Line trains are single-tracking",
      alert?.ttsDescriptionText?.translation?.single()?.text,
    )
    assertEquals(Alert.SeverityLevel.Warning, alert?.severityLevel)
    assertEquals(Alert.Effect.NoService, decoded.entity[1].alert?.effect)
  }

  @Test
  fun everyMismatchedWireTypeOnAMessageFieldIsSkipped() {
    val mismatched =
      listOf(
        ProtoWire.varintField(12, 4),
        ProtoWire.fixed32Field(12, 4),
        ProtoWire.fixed64Field(12, 4),
      )
    for (field in mismatched) {
      val decoded =
        GtfsRealtimeProto.decodeFeedMessage(
          feedMessageBytes(
            alertEntity("alert", field, ProtoWire.messageField(10, translatedString("Header")))
          )
        )

      val alert = decoded.entity.single().alert
      assertNull(alert?.ttsHeaderText)
      assertEquals("Header", alert?.headerText?.translation?.single()?.text)
    }
  }

  @Test
  fun wellTypedOccurrenceSurvivesAMismatchedOne() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          alertEntity(
            "alert",
            ProtoWire.varintField(12, 4),
            ProtoWire.messageField(12, translatedString("Spoken header")),
          )
        )
      )

    assertEquals(
      "Spoken header",
      decoded.entity.single().alert?.ttsHeaderText?.translation?.single()?.text,
    )
  }

  @Test
  fun mismatchedFeedHeaderFieldIsSkipped() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        ProtoWire.messageField(
          1,
          ProtoWire.concat(
            ProtoWire.stringField(1, "2.0"),
            ProtoWire.stringField(3, "1700000000"),
            ProtoWire.varintField(4, 7),
          ),
        )
      )

    assertEquals("2.0", decoded.header.gtfsRealtimeVersion)
    assertNull(decoded.header.timestamp)
    assertNull(decoded.header.feedVersion)
  }

  @Test
  fun mismatchedFeedEntityFieldsAreSkipped() {
    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(
            ProtoWire.stringField(1, "entity"),
            ProtoWire.stringField(2, "true"),
            ProtoWire.varintField(4, 1),
            ProtoWire.fixed32Field(3, 1),
            ProtoWire.messageField(5, ProtoWire.varintField(7, 4)),
          )
        )
      )

    val entity = decoded.entity.single()
    assertEquals("entity", entity.id)
    assertEquals(false, entity.isDeleted)
    assertNull(entity.vehicle)
    assertNull(entity.tripUpdate)
    assertEquals(Alert.Effect.Detour, entity.alert?.effect)
  }

  @Test
  fun mismatchedVehiclePositionFieldsAreSkipped() {
    val position =
      ProtoWire.concat(
        ProtoWire.fixed32Field(1, 38.9f.toRawBits()),
        ProtoWire.fixed32Field(2, (-77.0f).toRawBits()),
        ProtoWire.varintField(3, 90),
        ProtoWire.varintField(4, 1),
      )
    val vehicle =
      ProtoWire.concat(
        ProtoWire.messageField(1, ProtoWire.stringField(1, "trip-1")),
        ProtoWire.messageField(2, position),
        ProtoWire.fixed64Field(3, 5),
        ProtoWire.varintField(7, 42),
        ProtoWire.varintField(4, 1),
        ProtoWire.stringField(5, "1700000000"),
        ProtoWire.varintField(8, 1),
        ProtoWire.varintField(11, 2),
      )

    val decoded =
      GtfsRealtimeProto.decodeFeedMessage(
        feedMessageBytes(
          ProtoWire.concat(ProtoWire.stringField(1, "vehicle"), ProtoWire.messageField(4, vehicle))
        )
      )

    val decodedVehicle = decoded.entity.single().vehicle
    assertEquals("trip-1", decodedVehicle?.trip?.tripId)
    assertEquals(38.9f, decodedVehicle?.position?.latitude)
    assertEquals(-77.0f, decodedVehicle?.position?.longitude)
    assertNull(decodedVehicle?.position?.bearing)
    assertNull(decodedVehicle?.position?.odometer)
    assertNull(decodedVehicle?.currentStopSequence)
    assertNull(decodedVehicle?.stopId)
    assertEquals(VehiclePosition.VehicleStopStatus.StoppedAt, decodedVehicle?.currentStatus)
    assertNull(decodedVehicle?.timestamp)
    assertNull(decodedVehicle?.vehicle)
    assertEquals(emptyList(), decodedVehicle?.multiCarriageDetails)
  }

  @Test
  fun truncatedVarintStillFails() {
    assertMalformed(
      feedMessageBytes(
        alertEntity("alert", ProtoWire.concat(ProtoWire.tag(12, ProtoWire.VARINT), byteArrayOf(-1)))
      )
    )
  }

  @Test
  fun truncatedFixedValueStillFails() {
    assertMalformed(
      feedMessageBytes(
        alertEntity(
          "alert",
          ProtoWire.concat(ProtoWire.tag(12, ProtoWire.FIXED64), byteArrayOf(1, 2, 3)),
        )
      )
    )
  }

  @Test
  fun lengthPastTheEndStillFails() {
    assertMalformed(
      feedMessageBytes(
        alertEntity(
          "alert",
          ProtoWire.concat(
            ProtoWire.tag(10, ProtoWire.LEN),
            ProtoWire.varint(50),
            translatedString("Header"),
          ),
        )
      )
    )
  }

  @Test
  fun truncatedFieldInsideMessageWithoutEnumsStillFails() {
    assertMalformed(
      feedMessageBytes(
        alertEntity(
          "alert",
          ProtoWire.messageField(
            10,
            ProtoWire.messageField(
              1,
              ProtoWire.concat(ProtoWire.tag(1, ProtoWire.LEN), ProtoWire.varint(9)),
            ),
          ),
        )
      )
    )
  }

  @Test
  fun invalidWireTypesStillFail() {
    for (wireType in listOf(6, 7)) {
      assertMalformed(
        feedMessageBytes(alertEntity("alert", ProtoWire.tag(12, wireType), ProtoWire.varint(4)))
      )
      assertMalformed(
        ProtoWire.concat(
          ProtoWire.messageField(1, feedHeaderBytes()),
          ProtoWire.tag(2, wireType),
          ProtoWire.varint(4),
        )
      )
    }
  }

  private fun assertMalformed(bytes: ByteArray) {
    assertFailsWith<SerializationException> { GtfsRealtimeProto.decodeFeedMessage(bytes) }
  }
}
