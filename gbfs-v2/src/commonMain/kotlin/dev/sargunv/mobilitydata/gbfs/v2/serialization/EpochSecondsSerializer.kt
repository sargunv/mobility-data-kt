package dev.sargunv.mobilitydata.gbfs.v2.serialization

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalTime::class)
internal class EpochSecondsSerializer : KSerializer<Instant> {
  private val delegate = Long.serializer()
  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Instant): Unit =
    delegate.serialize(encoder, value.epochSeconds)

  override fun deserialize(decoder: Decoder): Instant =
    Instant.fromEpochSeconds(delegate.deserialize(decoder))
}
