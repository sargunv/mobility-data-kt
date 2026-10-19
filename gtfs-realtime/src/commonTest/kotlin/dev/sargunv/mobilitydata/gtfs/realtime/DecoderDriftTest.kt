package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind

/**
 * [GtfsRealtimeDecoder] is written by hand, next to data classes that are also written by hand.
 * This walks the serial descriptors from [FeedMessage] and, for every field with a `@ProtoNumber`,
 * decodes a feed that sets it, so a field the decoder doesn't read (or reads into the wrong
 * property, or with the wrong wire type) fails here.
 */
class DecoderDriftTest {
  @Test
  fun decoderReadsEveryDescriptorField() {
    val checked = mutableListOf<ProtoField>()
    checkFields(FeedMessage.serializer().descriptor, wrap = { it }, checked)

    val names = checked.map { it.name.substringAfterLast("realtime.") }
    assertTrue("Position.latitude" in names, "Walk missed nested messages: $names")
    assertTrue("TranslatedImage.LocalizedImage.mediaType" in names, "Walk missed $names")
    assertTrue("TripModifications.Modification.lastModifiedTime" in names, "Walk missed $names")
  }

  /**
   * Checks the fields of [message], where [wrap] embeds a [message] payload in a feed whose other
   * messages have only their required fields.
   */
  private fun checkFields(
    message: SerialDescriptor,
    wrap: (ByteArray) -> ByteArray,
    checked: MutableList<ProtoField>,
  ) {
    val baseline = oracleDecode(wrap(DescriptorWriter.requiredOnly(message)))
    for (field in message.protoFields()) {
      val decoded =
        DescriptorWriter.sampleValues(field).map { value ->
          val bytes =
            wrap(DescriptorWriter.requiredOnly(message, DescriptorWriter.field(field, value)))
          val expected = oracleDecode(bytes)
          assertEquals(expected, GtfsRealtimeProto.decodeFeedMessage(bytes), "$field")
          expected
        }
      checked += field

      // A required message is in the baseline already; the fields inside it are checked below.
      if (!field.required || field.kind != StructureKind.CLASS) {
        assertNotEquals(listOf(baseline), decoded.distinct(), "No sample value of $field shows up")
      }
      if (field.kind == StructureKind.CLASS) {
        checkFields(
          field.descriptor,
          wrap = { inner ->
            wrap(DescriptorWriter.requiredOnly(message, DescriptorWriter.field(field, inner)))
          },
          checked,
        )
      }
    }
  }
}
