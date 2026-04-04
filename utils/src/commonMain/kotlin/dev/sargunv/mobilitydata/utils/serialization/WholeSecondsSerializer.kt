package dev.sargunv.mobilitydata.utils.serialization

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Serializer for [Duration] that uses whole seconds. Fractional seconds are rounded down. */
public object WholeSecondsSerializer : KSerializer<Duration> {
  private val delegate = Long.serializer()
  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Duration): Unit =
    delegate.serialize(encoder, value.inWholeSeconds)

  override fun deserialize(decoder: Decoder): Duration = delegate.deserialize(decoder).seconds
}
