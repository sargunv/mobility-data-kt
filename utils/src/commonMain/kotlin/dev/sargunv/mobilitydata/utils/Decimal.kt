package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.DecimalSerializer
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * An exact base-10 number with up to 9 fractional digits.
 *
 * `Decimal` is intended for mobility-data prices, fares, rates, and similar values. It stores a
 * single scaled [Long] and never converts through [Double] or [Float] except in the explicitly
 * lossy [toDouble] and [toFloat] helpers.
 *
 * All values use a fixed internal scale of 9 fractional decimal places, so `2.5` and `2.50` are the
 * same number. The representable range is `-9223372036.854775808` through `9223372036.854775807`.
 */
@Serializable(with = DecimalSerializer::class)
@JvmInline
public value class Decimal private constructor(private val value: Long) : Comparable<Decimal> {
  /** Compares this value to [other] using exact numeric order. */
  public override fun compareTo(other: Decimal): Int = value.compareTo(other.value)

  /** Returns the negation of this value. */
  public operator fun unaryMinus(): Decimal = fromScaled(negateExact(value))

  /** Returns the exact sum of this value and [other]. */
  public operator fun plus(other: Decimal): Decimal = fromScaled(addExact(value, other.value))

  /** Returns the exact difference of this value and [other]. */
  public operator fun minus(other: Decimal): Decimal = fromScaled(subtractExact(value, other.value))

  /**
   * Returns the exact product of this value and [other].
   *
   * Throws [ArithmeticException] if the mathematical product is not representable at 9 decimal
   * places or is outside the representable range.
   */
  public operator fun times(other: Decimal): Decimal =
    fromScaled(multiplyScaledExact(value, other.value))

  /**
   * Returns the exact quotient of this value and [other].
   *
   * Throws [ArithmeticException] if [other] is zero, if the mathematical quotient is not
   * representable at 9 decimal places, or if the result is outside the representable range.
   */
  public operator fun div(other: Decimal): Decimal =
    fromScaled(divideScaledExact(value, other.value))

  /** Returns the absolute value of this number. */
  public fun abs(): Decimal = if (value >= 0L) this else unaryMinus()

  /**
   * Divides this value by [other] and rounds the result to 9 fractional digits using
   * [roundingMode].
   *
   * Throws [ArithmeticException] if [other] is zero or if the result is outside the representable
   * range.
   */
  public fun divide(other: Decimal, roundingMode: RoundingMode): Decimal =
    fromScaled(divideScaledRounded(value, other.value, roundingMode))

  /**
   * Rounds this value to [decimalPlaces] fractional digits using [roundingMode].
   *
   * [decimalPlaces] must be in `0..9`. The returned value still uses the fixed internal scale of 9.
   */
  public fun round(decimalPlaces: Int, roundingMode: RoundingMode): Decimal {
    requireDecimalPlaces(decimalPlaces)
    return fromScaled(roundScaled(value, decimalPlaces, roundingMode))
  }

  /** Converts this value to a [Double]. This conversion may lose precision. */
  public fun toDouble(): Double = value.toDouble() / DECIMAL_SCALE_FACTOR.toDouble()

  /** Converts this value to a [Float]. This conversion may lose precision. */
  public fun toFloat(): Float = toDouble().toFloat()

  /**
   * Returns this value as a [Long] if it is an integer in range.
   *
   * Throws [ArithmeticException] if the value has a non-zero fractional component.
   */
  public fun toLongExact(): Long {
    if (value % DECIMAL_SCALE_FACTOR != 0L) {
      throw ArithmeticException("Not an integer: $this")
    }
    return value / DECIMAL_SCALE_FACTOR
  }

  /**
   * Returns this value as an [Int] if it is an integer in range.
   *
   * Throws [ArithmeticException] if the value has a non-zero fractional component or exceeds the
   * [Int] range.
   */
  public fun toIntExact(): Int {
    val longValue = toLongExact()
    if (longValue < Int.MIN_VALUE || longValue > Int.MAX_VALUE) {
      throw ArithmeticException("Integer overflow: $this")
    }
    return longValue.toInt()
  }

  /**
   * Returns the shortest ordinary decimal representation that preserves this exact numeric value.
   *
   * The result never uses scientific notation. Negative zero is formatted as `0`.
   */
  public override fun toString(): String {
    if (value == 0L) return "0"
    val parts = parts()
    return buildString {
      if (parts.negative) append('-')
      append(parts.integerPart)
      if (parts.fraction != 0uL) {
        append('.')
        append(parts.fraction.toString().padStart(DECIMAL_SCALE, '0').trimEnd('0'))
      }
    }
  }

  /**
   * Formats this value with exactly [decimalPlaces] fractional digits.
   *
   * This is exact formatting, not rounding. Throws [ArithmeticException] if the value has
   * meaningful precision beyond [decimalPlaces]. [decimalPlaces] must be in `0..9`.
   */
  public fun toString(decimalPlaces: Int): String {
    requireDecimalPlaces(decimalPlaces)
    val parts = parts()
    val factor = DECIMAL_POW10[DECIMAL_SCALE - decimalPlaces].toULong()
    if (parts.fraction % factor != 0uL) {
      throw ArithmeticException("Value $this has precision beyond $decimalPlaces decimal places")
    }
    return buildString {
      if (parts.negative) append('-')
      append(parts.integerPart)
      if (decimalPlaces > 0) {
        append('.')
        append((parts.fraction / factor).toString().padStart(decimalPlaces, '0'))
      }
    }
  }

  private fun parts(): DecimalParts {
    val magnitude = value.unsignedAbs()
    return DecimalParts(
      negative = value < 0L,
      integerPart = magnitude / DECIMAL_SCALE_FACTOR.toULong(),
      fraction = magnitude % DECIMAL_SCALE_FACTOR.toULong(),
    )
  }

  /** Constants and parsers for [Decimal]. */
  public companion object {
    /** The value `0`. */
    public val ZERO: Decimal = fromScaled(0L)

    /** The value `1`. */
    public val ONE: Decimal = fromScaled(DECIMAL_SCALE_FACTOR)

    /** The value `10`. */
    public val TEN: Decimal = fromScaled(10L * DECIMAL_SCALE_FACTOR)

    internal fun fromScaled(scaled: Long): Decimal = Decimal(scaled)

    /**
     * Returns a [Decimal] equal to the integer [value].
     *
     * Throws [ArithmeticException] if [value] is outside the representable range.
     */
    public fun of(value: Int): Decimal = of(value.toLong())

    /**
     * Returns a [Decimal] equal to the integer [value].
     *
     * Throws [ArithmeticException] if [value] is outside the representable range.
     */
    public fun of(value: Long): Decimal = fromScaled(multiplyExact(value, DECIMAL_SCALE_FACTOR))

    /**
     * Parses ordinary decimal text into a [Decimal].
     *
     * Accepted syntax is an optional leading `-`, one or more integer digits, an optional decimal
     * point followed by one or more fractional digits, and an optional JSON-style exponent (`e` or
     * `E`, optional `+`/`-`, and one or more digits). Leading `+` on the number and forms such as
     * `.5` or `1.` are rejected.
     *
     * Throws [NumberFormatException] for malformed syntax and [ArithmeticException] for excess
     * precision or overflow.
     */
    public fun parse(value: String): Decimal = parseDecimal(value)

    /**
     * Parses ordinary decimal text, returning `null` if the input is malformed, over-precise, or
     * out of range.
     */
    public fun parseOrNull(value: String): Decimal? =
      try {
        parse(value)
      } catch (_: NumberFormatException) {
        null
      } catch (_: ArithmeticException) {
        null
      }
  }
}

private class DecimalParts(val negative: Boolean, val integerPart: ULong, val fraction: ULong)
