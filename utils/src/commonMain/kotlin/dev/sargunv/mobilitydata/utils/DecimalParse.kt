package dev.sargunv.mobilitydata.utils

internal fun parseDecimal(value: String): Decimal {
  val text = DecimalText(value)
  if (text.atEnd()) text.invalid()

  val negative =
    when (text.peek()) {
      '-' -> {
        text.take()
        if (text.atEnd()) text.invalid()
        true
      }
      '+' -> text.invalid()
      else -> false
    }

  var scaled = text.takeScaledDigits(negative) ?: text.invalid()
  var fractionDigits = 0
  if (text.peek() == '.') {
    text.take()
    val firstFraction = text.takeDigit() ?: text.invalid()
    scaled = appendScaledDigit(scaled, firstFraction, negative)
    fractionDigits = 1
    while (true) {
      val digit = text.takeDigit() ?: break
      if (fractionDigits < DECIMAL_SCALE) {
        scaled = appendScaledDigit(scaled, digit, negative)
        fractionDigits++
      } else if (digit != 0L) {
        throw ArithmeticException("Decimal exceeds 9 fractional digits: $value")
      }
    }
  }

  repeat(DECIMAL_SCALE - fractionDigits) { scaled = multiplyExact(scaled, 10L) }

  val exponentMarker = text.peek()
  if (exponentMarker == 'e' || exponentMarker == 'E') {
    text.take()
    scaled = applyExponent(scaled, text.takeExponent())
  }

  if (!text.atEnd()) text.invalid()
  return Decimal.fromScaled(scaled)
}

private const val MAX_EXPONENT_SHIFT: Long = 40L

private fun applyExponent(scaled: Long, exponent: Long): Long {
  if (exponent == 0L || scaled == 0L) return scaled
  if (exponent > MAX_EXPONENT_SHIFT || exponent < -MAX_EXPONENT_SHIFT) {
    throw ArithmeticException("Decimal exponent is out of range")
  }
  return if (exponent > 0L) {
    multiplyByPowersOfTen(scaled, exponent.toInt())
  } else {
    divideByPowersOfTen(scaled, (-exponent).toInt())
  }
}

private fun multiplyByPowersOfTen(scaled: Long, exponent: Int): Long {
  var result = scaled
  var remaining = exponent
  while (remaining >= DECIMAL_SCALE) {
    result = multiplyExact(result, DECIMAL_SCALE_FACTOR)
    remaining -= DECIMAL_SCALE
  }
  if (remaining > 0) {
    result = multiplyExact(result, DECIMAL_POW10[remaining])
  }
  return result
}

private fun divideByPowersOfTen(scaled: Long, exponent: Int): Long {
  var result = scaled
  var remaining = exponent
  while (remaining >= DECIMAL_SCALE) {
    if (result % DECIMAL_SCALE_FACTOR != 0L) {
      throw ArithmeticException("Decimal exponent requires more than 9 fractional digits")
    }
    result /= DECIMAL_SCALE_FACTOR
    remaining -= DECIMAL_SCALE
  }
  if (remaining > 0) {
    val factor = DECIMAL_POW10[remaining]
    if (result % factor != 0L) {
      throw ArithmeticException("Decimal exponent requires more than 9 fractional digits")
    }
    result /= factor
  }
  return result
}

private fun appendScaledDigit(scaled: Long, digit: Long, negative: Boolean): Long =
  if (negative) {
    subtractExact(multiplyExact(scaled, 10L), digit)
  } else {
    addExact(multiplyExact(scaled, 10L), digit)
  }

private class DecimalText(private val text: String) {
  private var index = 0

  fun atEnd(): Boolean = index >= text.length

  fun peek(): Char? = text.getOrNull(index)

  fun take() {
    index++
  }

  fun takeDigit(): Long? {
    val char = peek() ?: return null
    if (char !in '0'..'9') return null
    index++
    return (char - '0').toLong()
  }

  fun takeScaledDigits(negative: Boolean): Long? {
    val first = takeDigit() ?: return null
    var scaled = appendScaledDigit(0L, first, negative)
    while (true) {
      val digit = takeDigit() ?: break
      scaled = appendScaledDigit(scaled, digit, negative)
    }
    return scaled
  }

  fun takeExponent(): Long {
    if (atEnd()) invalid()
    val negative =
      when (peek()) {
        '-' -> {
          take()
          true
        }
        '+' -> {
          take()
          false
        }
        else -> false
      }
    var exponent = takeDigit() ?: invalid()
    while (true) {
      val digit = takeDigit() ?: break
      exponent = addExact(multiplyExact(exponent, 10L), digit)
    }
    return if (negative) negateExact(exponent) else exponent
  }

  fun invalid(): Nothing = throw NumberFormatException("Invalid decimal: $text")
}
