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

  val digits = StringBuilder()
  if (text.takeDigitsInto(digits) == 0) text.invalid()

  var fractionDigits = 0
  if (text.peek() == '.') {
    text.take()
    val added = text.takeDigitsInto(digits)
    if (added == 0) text.invalid()
    fractionDigits = added
  }

  var exponent = 0L
  val exponentMarker = text.peek()
  if (exponentMarker == 'e' || exponentMarker == 'E') {
    text.take()
    exponent = text.takeExponent()
  }

  if (!text.atEnd()) text.invalid()
  return Decimal.fromScaled(toScaledValue(digits, fractionDigits, exponent, negative))
}

private const val MAX_SCALE_SHIFT: Long = 40L

private fun toScaledValue(
  digits: StringBuilder,
  fractionDigits: Int,
  exponent: Long,
  negative: Boolean,
): Long {
  var start = 0
  var end = digits.length
  var fraction = fractionDigits

  while (fraction > 0 && end > start && digits[end - 1] == '0') {
    end--
    fraction--
  }
  while (start < end - 1 && digits[start] == '0') {
    start++
  }

  var scaleShift = subtractExact(addExact(DECIMAL_SCALE.toLong(), exponent), fraction.toLong())
  while (scaleShift < 0L && end > start) {
    if (digits[end - 1] != '0') {
      throw ArithmeticException("Decimal exponent requires more than 9 fractional digits")
    }
    end--
    scaleShift++
  }
  if (scaleShift < 0L || start == end || (end - start == 1 && digits[start] == '0')) {
    return 0L
  }

  var significand = 0L
  for (index in start until end) {
    significand = appendDigit(significand, (digits[index] - '0').toLong(), negative)
  }
  return applyScaleShift(significand, scaleShift)
}

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

private fun appendDigit(scaled: Long, digit: Long, negative: Boolean): Long =
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

  fun takeDigitsInto(digits: StringBuilder): Int {
    var count = 0
    while (true) {
      val digit = takeDigit() ?: break
      digits.append('0' + digit.toInt())
      count++
    }
    return count
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
