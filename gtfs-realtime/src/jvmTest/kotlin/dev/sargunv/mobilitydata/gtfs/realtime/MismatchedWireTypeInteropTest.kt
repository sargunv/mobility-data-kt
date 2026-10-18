package dev.sargunv.mobilitydata.gtfs.realtime

import com.google.transit.realtime.GtfsRealtime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class MismatchedWireTypeInteropTest {
  @Test
  fun officialJavaSkipsVarintTtsHeaderTextAndKotlinMatches() {
    val bytes =
      feedMessageBytes(
        ProtoWire.concat(
          ProtoWire.stringField(1, "wmata-alert"),
          ProtoWire.messageField(
            5,
            ProtoWire.concat(
              ProtoWire.varintField(7, 3),
              ProtoWire.messageField(
                10,
                ProtoWire.messageField(1, ProtoWire.stringField(1, "Red Line delays")),
              ),
              ProtoWire.varintField(12, 4),
              ProtoWire.varintField(14, 3),
            ),
          ),
        ),
      )

    val official = GtfsRealtime.FeedMessage.parseFrom(bytes).getEntity(0).alert
    assertFalse(official.hasTtsHeaderText())
    assertEquals(GtfsRealtime.Alert.Effect.SIGNIFICANT_DELAYS, official.effect)
    assertEquals("Red Line delays", official.headerText.getTranslation(0).text)
    assertEquals(GtfsRealtime.Alert.SeverityLevel.WARNING, official.severityLevel)

    val decoded = GtfsRealtimeProto.decodeFeedMessage(bytes).entity.single().alert
    assertNull(decoded?.ttsHeaderText)
    assertEquals(Alert.Effect.SignificantDelays, decoded?.effect)
    assertEquals("Red Line delays", decoded?.headerText?.translation?.single()?.text)
    assertEquals(Alert.SeverityLevel.Warning, decoded?.severityLevel)
  }

  @Test
  fun officialJavaSkipsGroupAndKotlinMatches() {
    val bytes =
      feedMessageBytes(
        ProtoWire.concat(
          ProtoWire.stringField(1, "alert"),
          ProtoWire.messageField(
            5,
            ProtoWire.concat(
              ProtoWire.tag(12, ProtoWire.START_GROUP),
              ProtoWire.varintField(1, 4),
              ProtoWire.tag(12, ProtoWire.END_GROUP),
              ProtoWire.varintField(7, 4),
            ),
          ),
        ),
      )

    val official = GtfsRealtime.FeedMessage.parseFrom(bytes).getEntity(0).alert
    assertFalse(official.hasTtsHeaderText())
    assertEquals(GtfsRealtime.Alert.Effect.DETOUR, official.effect)

    val decoded = GtfsRealtimeProto.decodeFeedMessage(bytes).entity.single().alert
    assertNull(decoded?.ttsHeaderText)
    assertEquals(Alert.Effect.Detour, decoded?.effect)
  }
}
