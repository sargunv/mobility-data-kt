package dev.sargunv.mobilitydata.gbfs.v2.serialization

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class WholeMinutesSerializer : KSerializer<Duration> {
  private val delegate = Long.serializer()
  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Duration): Unit =
    delegate.serialize(encoder, value.inWholeMinutes)

  override fun deserialize(decoder: Decoder): Duration = delegate.deserialize(decoder).minutes
}
