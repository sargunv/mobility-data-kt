package dev.sargunv.mobilitydata.utils.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object IntBooleanSerializer : KSerializer<Boolean> {
  private val delegate = Int.serializer()
  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Boolean): Unit =
    delegate.serialize(encoder, if (value) 1 else 0)

  override fun deserialize(decoder: Decoder): Boolean = delegate.deserialize(decoder) != 0
}
