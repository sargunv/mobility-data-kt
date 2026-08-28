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

  var significand = text.takeScaledDigits(negative) ?: text.invalid()
  var fractionDigits = 0
  if (text.peek() == '.') {
    text.take()
    val firstFraction = text.takeDigit() ?: text.invalid()
    significand = appendScaledDigit(significand, firstFraction, negative)
    fractionDigits = 1
    while (true) {
      val digit = text.takeDigit() ?: break
      significand = appendScaledDigit(significand, digit, negative)
      fractionDigits++
    }
  }

  while (fractionDigits > 0 && significand % 10L == 0L) {
    significand /= 10L
    fractionDigits--
  }

  var exponent = 0L
  val exponentMarker = text.peek()
  if (exponentMarker == 'e' || exponentMarker == 'E') {
    text.take()
    exponent = text.takeExponent()
  }

  if (!text.atEnd()) text.invalid()

  val scaleShift =
    subtractExact(addExact(DECIMAL_SCALE.toLong(), exponent), fractionDigits.toLong())
  return Decimal.fromScaled(applyScaleShift(significand, scaleShift))
}

private const val MAX_SCALE_SHIFT: Long = 40L

private fun applyScaleShift(significand: Long, scaleShift: Long): Long {
  if (significand == 0L || scaleShift == 0L) return significand
  if (scaleShift > MAX_SCALE_SHIFT || scaleShift < -MAX_SCALE_SHIFT) {
    throw ArithmeticException("Decimal exponent is out of range")
  }
  return if (scaleShift > 0L) {
    multiplyByPowersOfTen(significand, scaleShift.toInt())
  } else {
    divideByPowersOfTen(significand, (-scaleShift).toInt())
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
