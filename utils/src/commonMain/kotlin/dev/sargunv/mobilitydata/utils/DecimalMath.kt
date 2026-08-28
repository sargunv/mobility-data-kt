package dev.sargunv.mobilitydata.utils

internal const val DECIMAL_SCALE: Int = 9
internal const val DECIMAL_SCALE_FACTOR: Long = 1_000_000_000L

internal fun requireDecimalPlaces(decimalPlaces: Int) {
  require(decimalPlaces in 0..DECIMAL_SCALE) {
    "decimalPlaces must be in 0..$DECIMAL_SCALE, but was $decimalPlaces"
  }
}

internal val DECIMAL_POW10: LongArray =
  longArrayOf(
    1L,
    10L,
    100L,
    1_000L,
    10_000L,
    100_000L,
    1_000_000L,
    10_000_000L,
    100_000_000L,
    1_000_000_000L,
  )

internal fun addExact(x: Long, y: Long): Long {
  val result = x + y
  if ((x xor result) and (y xor result) < 0L) {
    throw ArithmeticException("Long overflow")
  }
  return result
}

internal fun subtractExact(x: Long, y: Long): Long {
  val result = x - y
  if ((x xor y) and (x xor result) < 0L) {
    throw ArithmeticException("Long overflow")
  }
  return result
}

internal fun negateExact(x: Long): Long {
  if (x == Long.MIN_VALUE) {
    throw ArithmeticException("Long overflow")
  }
  return -x
}

internal fun multiplyExact(x: Long, y: Long): Long {
  if (x == 0L || y == 0L) return 0L
  if (x == Long.MIN_VALUE) {
    if (y == 1L) return Long.MIN_VALUE
    throw ArithmeticException("Long overflow")
  }
  if (y == Long.MIN_VALUE) {
    if (x == 1L) return Long.MIN_VALUE
    throw ArithmeticException("Long overflow")
  }
  val result = x * y
  if (result / x != y) {
    throw ArithmeticException("Long overflow")
  }
  return result
}

private fun multiplyExact(x: ULong, y: ULong): ULong {
  if (x == 0uL || y == 0uL) return 0uL
  val result = x * y
  if (result / x != y) {
    throw ArithmeticException("Long overflow")
  }
  return result
}

internal fun Long.unsignedAbs(): ULong =
  when {
    this >= 0L -> toULong()
    this == Long.MIN_VALUE -> 1uL shl 63
    else -> (-this).toULong()
  }

internal fun ULong.toSignedLong(negative: Boolean): Long {
  if (!negative) {
    if (this > Long.MAX_VALUE.toULong()) {
      throw ArithmeticException("Long overflow")
    }
    return toLong()
  }
  if (this < (1uL shl 63)) {
    return -toLong()
  }
  if (this == (1uL shl 63)) {
    return Long.MIN_VALUE
  }
  throw ArithmeticException("Long overflow")
}

private fun gcd(a: ULong, b: ULong): ULong {
  var x = a
  var y = b
  while (y != 0uL) {
    val remainder = x % y
    x = y
    y = remainder
  }
  return x
}

internal fun multiplyScaledExact(a: Long, b: Long): Long {
  if (a == 0L || b == 0L) return 0L
  val negative = (a < 0L) != (b < 0L)
  val absA = a.unsignedAbs()
  val absB = b.unsignedAbs()
  val scale = DECIMAL_SCALE_FACTOR.toULong()

  val gcd1 = gcd(absA, scale)
  val a1 = absA / gcd1
  val scale1 = scale / gcd1
  val gcd2 = gcd(absB, scale1)
  val b1 = absB / gcd2
  val scale2 = scale1 / gcd2

  if (scale2 != 1uL) {
    throw ArithmeticException("Product is not exactly representable at 9 decimal places")
  }
  return multiplyExact(a1, b1).toSignedLong(negative)
}

internal fun divideScaledExact(a: Long, b: Long): Long {
  if (b == 0L) {
    throw ArithmeticException("Division by zero")
  }
  if (a == 0L) return 0L
  val negative = (a < 0L) != (b < 0L)
  val absA = a.unsignedAbs()
  val absB = b.unsignedAbs()
  val scale = DECIMAL_SCALE_FACTOR.toULong()

  val gcd1 = gcd(absA, absB)
  val a1 = absA / gcd1
  val b1 = absB / gcd1
  val gcd2 = gcd(scale, b1)
  val scale1 = scale / gcd2
  val b2 = b1 / gcd2

  if (b2 != 1uL) {
    throw ArithmeticException("Quotient is not exactly representable at 9 decimal places")
  }
  return multiplyExact(a1, scale1).toSignedLong(negative)
}

internal fun divideScaledRounded(a: Long, b: Long, roundingMode: RoundingMode): Long {
  if (b == 0L) {
    throw ArithmeticException("Division by zero")
  }
  if (a == 0L) return 0L
  val negative = (a < 0L) != (b < 0L)
  val absA = a.unsignedAbs()
  val absB = b.unsignedAbs()
  val (high, low) = multiplyTo128(absA, DECIMAL_SCALE_FACTOR.toULong())
  val (quotient, remainder) = divide128By64(high, low, absB)
  val rounded = applyRounding(quotient, remainder, absB, roundingMode, negative)
  return rounded.toSignedLong(negative)
}

internal fun roundScaled(value: Long, decimalPlaces: Int, roundingMode: RoundingMode): Long {
  val factor = DECIMAL_POW10[DECIMAL_SCALE - decimalPlaces].toULong()
  if (value == 0L) return 0L
  val negative = value < 0L
  val magnitude = value.unsignedAbs()
  val quotient = magnitude / factor
  val remainder = magnitude % factor
  val rounded = applyRounding(quotient, remainder, factor, roundingMode, negative)
  return multiplyExact(rounded, factor).toSignedLong(negative)
}

private fun applyRounding(
  quotient: ULong,
  remainder: ULong,
  divisor: ULong,
  roundingMode: RoundingMode,
  negative: Boolean,
): ULong {
  if (remainder == 0uL) return quotient
  val increment =
    when (roundingMode) {
      RoundingMode.UNNECESSARY -> throw ArithmeticException("Rounding necessary")
      RoundingMode.UP -> true
      RoundingMode.DOWN -> false
      RoundingMode.CEILING -> !negative
      RoundingMode.FLOOR -> negative
      RoundingMode.HALF_UP,
      RoundingMode.HALF_DOWN,
      RoundingMode.HALF_EVEN -> {
        val comparedToHalf = compareRemainderToHalf(remainder, divisor)
        when {
          comparedToHalf > 0 -> true
          comparedToHalf < 0 -> false
          roundingMode == RoundingMode.HALF_UP -> true
          roundingMode == RoundingMode.HALF_DOWN -> false
          else -> (quotient and 1uL) == 1uL
        }
      }
    }
  if (!increment) return quotient
  if (quotient == ULong.MAX_VALUE) {
    throw ArithmeticException("Long overflow")
  }
  return quotient + 1uL
}

private fun compareRemainderToHalf(remainder: ULong, divisor: ULong): Int {
  if (remainder > ULong.MAX_VALUE / 2uL) return 1
  return (remainder * 2uL).compareTo(divisor)
}

private fun multiplyTo128(a: ULong, b: ULong): Pair<ULong, ULong> {
  val mask = 0xFFFFFFFFUL
  val aLow = a and mask
  val aHigh = a shr 32
  val bLow = b and mask
  val bHigh = b shr 32

  val p0 = aLow * bLow
  val p1 = aLow * bHigh
  val p2 = aHigh * bLow
  val p3 = aHigh * bHigh

  var middle = (p0 shr 32) + (p1 and mask) + (p2 and mask)
  val low = (p0 and mask) or (middle shl 32)
  middle = middle shr 32
  val high = p3 + (p1 shr 32) + (p2 shr 32) + middle
  return high to low
}

private fun divide128By64(high: ULong, low: ULong, divisor: ULong): Pair<ULong, ULong> {
  if (divisor == 0uL) {
    throw ArithmeticException("Division by zero")
  }
  if (high == 0uL) {
    return (low / divisor) to (low % divisor)
  }
  if (high >= divisor) {
    throw ArithmeticException("Long overflow")
  }

  // Long division over the 128-bit numerator, high word first. A set bit in the
  // high word would mean the quotient does not fit in 64 bits.
  var remainder = 0uL
  var quotient = 0uL
  for (bitIndex in 63 downTo 0) {
    val (nextRemainder, subtract) =
      shiftRemainderInBit(remainder, (high shr bitIndex) and 1uL, divisor)
    remainder = nextRemainder
    if (subtract) {
      throw ArithmeticException("Long overflow")
    }
  }
  for (bitIndex in 63 downTo 0) {
    val (nextRemainder, subtract) =
      shiftRemainderInBit(remainder, (low shr bitIndex) and 1uL, divisor)
    remainder = nextRemainder
    if (subtract) {
      quotient = quotient or (1uL shl bitIndex)
    }
  }
  return quotient to remainder
}

private fun shiftRemainderInBit(
  remainder: ULong,
  bit: ULong,
  divisor: ULong,
): Pair<ULong, Boolean> {
  val wouldOverflow = remainder >= (1uL shl 63)
  if (wouldOverflow) {
    val next = ((remainder shl 1) or bit) + (0uL - divisor)
    return next to true
  }
  val next = (remainder shl 1) or bit
  return if (next >= divisor) {
    (next - divisor) to true
  } else {
    next to false
  }
}
