package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.RgbColorCodeSerializer
import dev.sargunv.mobilitydata.utils.serialization.RgbColorTripletSerializer
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * A 24-bit color with eight bits each for red, green, and blue components.
 *
 * @see RgbColorCode
 * @see RgbColorTriplet
 */
@JvmInline
public value class RgbColor(
  /** The 24-bit integer representation of this color (0x000000 to 0xFFFFFF). */
  public val value: Int
) {
  init {
    require(value in 0x000000..0xFFFFFF) { "Color value must be a 6-digit hexadecimal number." }
  }

  /** The red component of this color (0-255). */
  public val red: Byte
    get() = (value shr 16).toByte()

  /** The green component of this color (0-255). */
  public val green: Byte
    get() = (value shr 8).toByte()

  /** The blue component of this color (0-255). */
  public val blue: Byte
    get() = value.toByte()

  public override fun toString(): String = "#${value.toHexString(HexFormat.UpperCase)}"
}

/**
 * An [RgbColor] encoded as a six-digit hexadecimal number. The leading "#" MUST NOT be included.
 * Example: FFFFFF for white or 000000 for black.
 */
public typealias RgbColorTriplet = @Serializable(with = RgbColorTripletSerializer::class) RgbColor

/**
 * An [RgbColor] encoded as a six-digit hexadecimal number. The leading "#" MUST be included.
 * Example: #FFFFFF for white or #000000 for black.
 */
public typealias RgbColorCode = @Serializable(with = RgbColorCodeSerializer::class) RgbColor
