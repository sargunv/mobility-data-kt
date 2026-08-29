package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Serializer for GBFS Datetime fields. Encodes whole seconds only. */
@OptIn(ExperimentalTime::class)
public object DatetimeSerializer : KSerializer<Timestamp> {
  private val delegate = String.serializer()

  override val descriptor: SerialDescriptor = TimestampSerializer.descriptor

  override fun serialize(encoder: Encoder, value: Timestamp) {
    val wholeSeconds = Instant.fromEpochSeconds(value.instant.epochSeconds)
    delegate.serialize(encoder, wholeSeconds.format(ISO_DATE_TIME_OFFSET, value.offset))
  }

  override fun deserialize(decoder: Decoder): Timestamp = TimestampSerializer.deserialize(decoder)
}
