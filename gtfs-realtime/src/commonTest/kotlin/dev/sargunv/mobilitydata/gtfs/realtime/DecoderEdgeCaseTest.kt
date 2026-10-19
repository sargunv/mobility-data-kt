@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind

/**
 * Pins how decoding treats inputs where kotlinx and proto2 could plausibly differ. Every case also
 * goes through [decodeMatchingOracle], so it holds for the old strip-and-decode path too. Where the
 * behavior differs from protobuf-java, the test name says so.
 */
class DecoderEdgeCaseTest {
  private val trip = ProtoWire.messageField(1, ProtoWire.stringField(1, "trip"))

  private fun tripUpdate(vararg fields: ByteArray): TripUpdate =
    decodeMatchingOracle(tripUpdateFeed(*fields)).entity.single().tripUpdate!!

  private fun tripUpdateFeed(vararg fields: ByteArray): ByteArray =
    feedMessageBytes(
      ProtoWire.concat(
        ProtoWire.stringField(1, "entity"),
        ProtoWire.messageField(3, ProtoWire.concat(*fields)),
      ),
    )

  @Test
  fun repeatedSingularMessageIsReplacedNotMergedUnlikeProtobufJava() {
    val update =
      tripUpdate(
        ProtoWire.messageField(
          1,
          ProtoWire.concat(ProtoWire.stringField(1, "first"), ProtoWire.stringField(5, "route")),
        ),
        ProtoWire.messageField(1, ProtoWire.stringField(1, "second")),
      )

    assertEquals(TripDescriptor(tripId = "second"), update.trip)
  }

  @Test
  fun lastScalarOccurrenceWins() {
    val update = tripUpdate(trip, ProtoWire.varintField(5, 1), ProtoWire.varintField(5, 2))

    assertEquals(2, update.delay)
  }

  @Test
  fun repeatedFieldAppendsAcrossOtherFields() {
    val update =
      tripUpdate(
        ProtoWire.messageField(2, ProtoWire.varintField(1, 1)),
        trip,
        ProtoWire.messageField(2, ProtoWire.varintField(1, 2)),
      )

    assertEquals(listOf(1, 2), update.stopTimeUpdate.map { it.stopSequence })
  }

  @Test
  fun noRepeatedFieldIsPackable() {
    // kotlinx and proto2 only pack repeated scalars; every repeated field here is a message or a
    // string, so packed encoding never applies.
    fun check(message: SerialDescriptor) {
      for (field in message.protoFields()) {
        if (field.repeated) {
          assertTrue(
            field.kind == StructureKind.CLASS || field.kind == PrimitiveKind.STRING,
            "$field is packable",
          )
        }
        if (field.kind == StructureKind.CLASS) check(field.descriptor)
      }
    }
    check(FeedMessage.serializer().descriptor)
  }

  @Test
  fun negativeInt32ArrivesAsATenByteVarint() {
    assertEquals(10, ProtoWire.varint(-5).size)

    assertEquals(-5, tripUpdate(trip, ProtoWire.varintField(5, -5)).delay)
  }

  @Test
  fun int32AndUint32KeepTheLow32Bits() {
    val update =
      tripUpdate(
        trip,
        ProtoWire.varintField(5, (1L shl 32) + 7),
        ProtoWire.messageField(2, ProtoWire.varintField(1, 0xFFFFFFFFL)),
      )

    assertEquals(7, update.delay)
    assertEquals(-1, update.stopTimeUpdate.single().stopSequence)
  }

  @Test
  fun enumNumbersKeepTheLow32Bits() {
    val alert =
      decodeMatchingOracle(
          feedMessageBytes(
            ProtoWire.concat(
              ProtoWire.stringField(1, "alert"),
              ProtoWire.messageField(5, ProtoWire.varintField(6, (1L shl 32) + 4)),
            ),
          ),
        )
        .entity
        .single()
        .alert

    assertEquals(Alert.Cause.Strike, alert?.cause)
  }

  @Test
  fun uint64AboveLongMaxValueIsNegative() {
    assertEquals(-1L, tripUpdate(trip, ProtoWire.varintField(4, -1)).timestamp)
    assertEquals(
      Long.MIN_VALUE,
      tripUpdate(trip, ProtoWire.varintField(4, Long.MIN_VALUE)).timestamp,
    )
  }

  @Test
  fun invalidUtf8DecodesToReplacementCharacters() {
    val invalid = byteArrayOf('a'.code.toByte(), 0xFF.toByte(), 'b'.code.toByte())
    val update =
      tripUpdate(
        ProtoWire.messageField(
          1,
          ProtoWire.concat(ProtoWire.tag(1, ProtoWire.LEN), ProtoWire.varint(3), invalid),
        ),
      )

    assertEquals("a�b", update.trip.tripId)
  }

  @Test
  fun boolAcceptsOnlyZeroAndOneUnlikeProtobufJava() {
    fun isDeleted(value: Long): Boolean =
      decodeMatchingOracle(
          feedMessageBytes(
            ProtoWire.concat(ProtoWire.stringField(1, "entity"), ProtoWire.varintField(2, value)),
          ),
        )
        .entity
        .single()
        .isDeleted

    assertEquals(true, isDeleted(1))
    assertFailsWith<SerializationException> { isDeleted(2) }
    // Truncated to 32 bits first, like int32.
    assertEquals(false, isDeleted(1L shl 32))
    assertEquals(true, isDeleted((1L shl 32) + 1))
  }

  @Test
  fun missingRequiredFieldsThrowMissingFieldException() {
    val entityWithoutId = ProtoWire.varintField(2, 0)
    val vehicle = { position: ByteArray ->
      ProtoWire.concat(
        ProtoWire.stringField(1, "vehicle"),
        ProtoWire.messageField(4, ProtoWire.messageField(2, position)),
      )
    }
    val alert = { field: Int, translatedMessage: ByteArray ->
      ProtoWire.concat(
        ProtoWire.stringField(1, "alert"),
        ProtoWire.messageField(5, ProtoWire.messageField(field, translatedMessage)),
      )
    }
    val missing =
      listOf(
        ProtoWire.concat(),
        ProtoWire.messageField(1, ProtoWire.stringField(4, "no version")),
        feedMessageBytes(entityWithoutId),
        tripUpdateFeed(ProtoWire.varintField(5, 1)),
        feedMessageBytes(vehicle(ProtoWire.fixed32Field(2, 0))),
        feedMessageBytes(vehicle(ProtoWire.fixed32Field(1, 0))),
        feedMessageBytes(alert(10, ProtoWire.messageField(1, ProtoWire.stringField(2, "en")))),
        feedMessageBytes(alert(15, ProtoWire.messageField(1, ProtoWire.stringField(2, "png")))),
        feedMessageBytes(alert(15, ProtoWire.messageField(1, ProtoWire.stringField(1, "url")))),
      )
    for (bytes in missing) {
      assertFailsWith<MissingFieldException> { decodeMatchingOracle(bytes) }
    }
  }

  @Test
  fun fieldNumberZeroIsRejectedExceptAsAGroup() {
    for (wireType in listOf(ProtoWire.VARINT, ProtoWire.FIXED64, ProtoWire.FIXED32)) {
      val field =
        when (wireType) {
          ProtoWire.VARINT -> ProtoWire.varintField(0, 1)
          ProtoWire.FIXED64 -> ProtoWire.fixed64Field(0, 1)
          else -> ProtoWire.fixed32Field(0, 1)
        }
      assertFailsWith<SerializationException> { tripUpdate(trip, field) }
    }
    assertFailsWith<SerializationException> { tripUpdate(trip, ProtoWire.stringField(0, "x")) }

    // protobuf-java rejects this too; the old path dropped groups before kotlinx saw them.
    val group =
      ProtoWire.concat(
        ProtoWire.tag(0, ProtoWire.START_GROUP),
        ProtoWire.varintField(0, 1),
        ProtoWire.tag(0, ProtoWire.END_GROUP),
      )
    assertEquals(4, tripUpdate(trip, group, ProtoWire.varintField(5, 4)).delay)
  }

  @Test
  fun overlongTagKeepsTheLow32Bits() {
    // A five-byte tag with bits above 32 set, which no encoder writes. Like protobuf-java, only
    // the low 32 bits count, so this is field 5 (delay).
    fun overlongTag(field: Int, wireType: Int) =
      ProtoWire.varint((1L shl 33) or (field.toLong() shl 3) or wireType.toLong())

    val update = tripUpdate(trip, overlongTag(5, ProtoWire.VARINT), ProtoWire.varint(9))
    assertEquals(9, update.delay)

    // The old stripper read bits 3 to 34 as the field number, kept these as unknown fields, and
    // then kotlinx read them as known ones without stripping: it threw on the mismatched wire
    // type and replaced the recognized enum value with the default.
    val mismatched = tripUpdate(trip, overlongTag(5, ProtoWire.LEN), ProtoWire.varint(0))
    assertNull(mismatched.delay)
    val stopTimeUpdate =
      ProtoWire.concat(
        ProtoWire.varintField(5, 1),
        overlongTag(5, ProtoWire.VARINT),
        ProtoWire.varint(99),
      )
    assertEquals(
      TripUpdate.StopTimeUpdate.ScheduleRelationship.Skipped,
      tripUpdate(trip, ProtoWire.messageField(2, stopTimeUpdate))
        .stopTimeUpdate
        .single()
        .scheduleRelationship,
    )
  }

  @Test
  fun unrecognizedEnumKeepsTheDefaultWhenNothingIsRecognized() {
    val update = tripUpdate(trip, ProtoWire.messageField(2, ProtoWire.varintField(5, 99)))

    assertEquals(
      TripUpdate.StopTimeUpdate.ScheduleRelationship.Scheduled,
      update.stopTimeUpdate.single().scheduleRelationship,
    )
    assertNull(update.stopTimeUpdate.single().departureOccupancyStatus)
  }
}
