package dev.sargunv.mobilitydata.gbfs.v2.serialization

import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class MonthNumberSerializer : KSerializer<Month> {
  private val delegate = Int.serializer()
  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Month): Unit =
    delegate.serialize(encoder, value.number)

  override fun deserialize(decoder: Decoder): Month = Month(delegate.deserialize(decoder))
}
