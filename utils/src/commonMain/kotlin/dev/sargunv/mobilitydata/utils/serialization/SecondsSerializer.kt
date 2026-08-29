package dev.sargunv.mobilitydata.utils.serialization

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Serializer for [Duration] that uses a floating-point number of seconds. */
public object SecondsSerializer : KSerializer<Duration> {
  private val delegate = Double.serializer()
  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Duration): Unit =
    delegate.serialize(encoder, value.toDouble(DurationUnit.SECONDS))

  override fun deserialize(decoder: Decoder): Duration =
    delegate.deserialize(decoder).toDuration(DurationUnit.SECONDS)
}
