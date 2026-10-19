package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class UnknownFieldStripperTest {
  private fun alertEntity(vararg alertFields: ByteArray): ByteArray =
    ProtoWire.concat(
      ProtoWire.stringField(1, "alert"),
      ProtoWire.messageField(5, ProtoWire.concat(*alertFields)),
    )

  @Test
  fun returnsTheInputWhenNothingIsDropped() {
    val bytes =
      feedMessageBytes(
        alertEntity(
          ProtoWire.varintField(6, 8),
          ProtoWire.messageField(10, ProtoWire.messageField(1, ProtoWire.stringField(1, "Hi"))),
          ProtoWire.stringField(1000, "unknown fields are kept"),
        ),
      )

    assertSame(bytes, Proto2UnknownFieldStripper.strip(bytes))
  }

  @Test
  fun copiesWhenANestedOccurrenceIsDropped() {
    val kept = feedMessageBytes(alertEntity(ProtoWire.varintField(6, 8)))
    val bytes =
      feedMessageBytes(alertEntity(ProtoWire.varintField(6, 8), ProtoWire.varintField(7, 99)))

    val stripped = Proto2UnknownFieldStripper.strip(bytes)

    assertNotSame(bytes, stripped)
    assertContentEquals(kept, stripped)
  }

  @Test
  fun copiesWhenAGroupIsDropped() {
    val bytes =
      feedMessageBytes(
        alertEntity(
          ProtoWire.tag(12, ProtoWire.START_GROUP),
          ProtoWire.tag(12, ProtoWire.END_GROUP),
        ),
      )

    assertContentEquals(feedMessageBytes(alertEntity()), Proto2UnknownFieldStripper.strip(bytes))
  }
}
