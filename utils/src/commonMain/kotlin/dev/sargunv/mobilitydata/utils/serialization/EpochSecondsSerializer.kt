package dev.sargunv.mobilitydata.utils.serialization

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Serializer for [Instant] that uses POSIX epoch seconds (seconds since 1970-01-01T00:00:00Z). */
@OptIn(ExperimentalTime::class)
public object EpochSecondsSerializer : KSerializer<Instant> {
  private val delegate = Long.serializer()
  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Instant): Unit =
    delegate.serialize(encoder, value.epochSeconds)

  override fun deserialize(decoder: Decoder): Instant =
    Instant.fromEpochSeconds(delegate.deserialize(decoder))
}
