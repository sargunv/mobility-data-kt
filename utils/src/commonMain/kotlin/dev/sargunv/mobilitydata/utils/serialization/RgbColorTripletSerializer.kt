package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.RgbColor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [RgbColor] as a six-digit hexadecimal color code without a leading "#".
 *
 * Example: FFFFFF for white or 000000 for black.
 */
public object RgbColorTripletSerializer : KSerializer<RgbColor> {
  private val delegate = String.serializer()
  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: RgbColor): Unit =
    delegate.serialize(encoder, value.value.toHexString(HexFormat.UpperCase).takeLast(6))

  override fun deserialize(decoder: Decoder): RgbColor {
    val str = delegate.deserialize(decoder)
    require(str.matches(Regex("[0-9a-fA-F]{6}"))) { "Invalid RGB color code: $str" }
    return RgbColor(str.toInt(16))
  }
}
