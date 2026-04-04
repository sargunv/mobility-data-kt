package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.time.ExperimentalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET
import kotlinx.datetime.format.parse
import kotlinx.datetime.toInstant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [Timestamp] that handles RFC3339 format timestamp strings.
 *
 * This serializer converts between [Timestamp] objects and their string representation in RFC3339
 * format, preserving both the instant and UTC offset information.
 */
@OptIn(ExperimentalTime::class)
public object TimestampSerializer : KSerializer<Timestamp> {
  private val delegate = String.serializer()

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Timestamp): Unit =
    delegate.serialize(encoder, value.instant.format(ISO_DATE_TIME_OFFSET, value.offset))

  override fun deserialize(decoder: Decoder): Timestamp {
    val str = delegate.deserialize(decoder)
    // Normalize the input by replacing space with 'T' to accept both formats
    // RFC3339 uses 'T' but some feeds use space instead
    val normalizedStr = str.replace(Regex("""^(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}:\d{2})"""), "$1T$2")
    val components = DateTimeComponents.parse(normalizedStr, ISO_DATE_TIME_OFFSET)
    val offset = components.toUtcOffset()
    return Timestamp(components.toLocalDateTime().toInstant(offset), offset)
  }
}
