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

@OptIn(ExperimentalTime::class)
public object TimestampSerializer : KSerializer<Timestamp> {
  private val delegate = String.serializer()

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Timestamp): Unit =
    delegate.serialize(encoder, value.instant.format(ISO_DATE_TIME_OFFSET, value.offset))

  override fun deserialize(decoder: Decoder): Timestamp {
    val str = delegate.deserialize(decoder)
    val components = DateTimeComponents.parse(str, ISO_DATE_TIME_OFFSET)
    val offset = components.toUtcOffset()
    return Timestamp(components.toLocalDateTime().toInstant(offset), offset)
  }
}
