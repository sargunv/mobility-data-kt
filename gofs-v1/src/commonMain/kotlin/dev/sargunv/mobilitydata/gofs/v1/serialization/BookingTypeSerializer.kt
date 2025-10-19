package dev.sargunv.mobilitydata.gofs.v1.serialization

import dev.sargunv.mobilitydata.gofs.v1.BookingType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object BookingTypeSerializer : KSerializer<BookingType> {
  private val delegate = Int.serializer()

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: BookingType) {
    delegate.serialize(encoder, value.value)
  }

  override fun deserialize(decoder: Decoder): BookingType {
    val intValue = delegate.deserialize(decoder)
    return BookingType.entries.first { it.value == intValue }
  }
}
