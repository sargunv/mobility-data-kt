package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [ServiceTime] in HH:MM:SS format.
 *
 * Example: 14:30:00 for 2:30PM or 25:35:00 for 1:35AM on the next day.
 */
public object ServiceTimeSerializer : KSerializer<ServiceTime> {
  private val delegate = String.serializer()

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: ServiceTime) {
    val hh = value.hours.toString().padStart(2, '0')
    val mm = value.minutes.toString().padStart(2, '0')
    val ss = value.seconds.toString().padStart(2, '0')
    encoder.encodeString("$hh:$mm:$ss")
  }

  override fun deserialize(decoder: Decoder): ServiceTime {
    val value = decoder.decodeString()
    val parts = value.split(":")
    require(parts.size == 3) { "ServiceTime must be in the format HH:MM:SS, but was $value." }
    val hours = parts[0].toInt()
    val minutes = parts[1].toInt()
    val seconds = parts[2].toInt()
    return ServiceTime(hours, minutes, seconds)
  }
}
