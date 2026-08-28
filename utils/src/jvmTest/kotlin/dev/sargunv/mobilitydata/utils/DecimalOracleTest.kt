package dev.sargunv.mobilitydata.utils

import java.math.BigDecimal
import java.math.RoundingMode as JavaRoundingMode
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DecimalOracleTest {
  @Test
  fun timesMatchesBigDecimal() {
    forEachPair { a, b ->
      val expected = expectedProduct(a, b)
      if (expected == null) {
        assertFailsWith<ArithmeticException>("$a * $b") { a * b }
      } else {
        assertEquals(expected, a * b, "$a * $b")
      }
    }
  }

  @Test
  fun exactDivMatchesBigDecimal() {
    forEachPair { a, b ->
      if (b == Decimal.ZERO) {
        assertFailsWith<ArithmeticException>("$a / 0") { a / b }
        return@forEachPair
      }
      val expected = expectedExactQuotient(a, b)
      if (expected == null) {
        assertFailsWith<ArithmeticException>("$a / $b") { a / b }
      } else {
        assertEquals(expected, a / b, "$a / $b")
      }
    }
  }

  @Test
  fun roundedDivideMatchesBigDecimal() {
    val modes = RoundingMode.entries
    val random = Random(20260828L)
    repeat(200) {
      val a = randomDecimal(random)
      val b = randomNonZeroDecimal(random)
      val mode = modes[random.nextInt(modes.size)]
      assertRoundedDivide(a, b, mode)
    }
    val interesting =
      listOf(
        Decimal.ZERO,
        Decimal.ONE,
        Decimal.TEN,
        Decimal.parse("0.000000001"),
        Decimal.parse("-0.000000001"),
        Decimal.parse("9223372036.854775807"),
        Decimal.parse("-9223372036.854775808"),
        Decimal.parse("0.5"),
        Decimal.parse("-0.5"),
      )
    for (a in interesting) {
      for (b in interesting) {
        if (b == Decimal.ZERO) continue
        for (mode in modes) {
          assertRoundedDivide(a, b, mode)
        }
      }
    }
  }

  @Test
  fun roundMatchesBigDecimal() {
    val modes = RoundingMode.entries
    val random = Random(20260829L)
    repeat(300) {
      val value = randomDecimal(random)
      val places = random.nextInt(0, 10)
      val mode = modes[random.nextInt(modes.size)]
      assertRound(value, places, mode)
    }
    for (value in
      listOf(
        Decimal.parse("2.555"),
        Decimal.parse("-2.555"),
        Decimal.parse("9223372036.854775807"),
        Decimal.parse("-9223372036.854775808"),
        Decimal.parse("0.000000001"),
      )) {
      for (places in 0..9) {
        for (mode in modes) {
          assertRound(value, places, mode)
        }
      }
    }
  }

  private fun assertRoundedDivide(a: Decimal, b: Decimal, mode: RoundingMode) {
    val expected = expectedRoundedQuotient(a, b, mode)
    if (expected == null) {
      assertFailsWith<ArithmeticException>("$a.divide($b, $mode)") { a.divide(b, mode) }
    } else {
      assertEquals(expected, a.divide(b, mode), "$a.divide($b, $mode)")
    }
  }

  private fun assertRound(value: Decimal, places: Int, mode: RoundingMode) {
    val expected = expectedRounded(value, places, mode)
    if (expected == null) {
      assertFailsWith<ArithmeticException>("$value.round($places, $mode)") {
        value.round(places, mode)
      }
    } else {
      assertEquals(expected, value.round(places, mode), "$value.round($places, $mode)")
    }
  }

  private inline fun forEachPair(block: (Decimal, Decimal) -> Unit) {
    val random = Random(20260827L)
    repeat(250) { block(randomDecimal(random), randomDecimal(random)) }
    val interesting =
      listOf(
        Decimal.ZERO,
        Decimal.ONE,
        Decimal.TEN,
        Decimal.parse("0.1"),
        Decimal.parse("0.2"),
        Decimal.parse("0.000000001"),
        Decimal.parse("-0.000000001"),
        Decimal.parse("1.5"),
        Decimal.parse("-3"),
        Decimal.parse("5000000000"),
        Decimal.parse("9000000000"),
        Decimal.parse("9223372036.854775807"),
        Decimal.parse("-9223372036.854775808"),
      )
    for (a in interesting) {
      for (b in interesting) {
        block(a, b)
      }
    }
  }

  private fun expectedProduct(a: Decimal, b: Decimal): Decimal? {
    val product = a.toBigDecimal().multiply(b.toBigDecimal())
    return product.toDecimalOrNull()
  }

  private fun expectedExactQuotient(a: Decimal, b: Decimal): Decimal? {
    val quotient =
      try {
        a.toBigDecimal().divide(b.toBigDecimal())
      } catch (_: ArithmeticException) {
        return null
      }
    return quotient.toDecimalOrNull()
  }

  private fun expectedRoundedQuotient(a: Decimal, b: Decimal, mode: RoundingMode): Decimal? {
    val quotient =
      try {
        a.toBigDecimal().divide(b.toBigDecimal(), DECIMAL_SCALE, mode.toJava())
      } catch (_: ArithmeticException) {
        return null
      }
    return quotient.toDecimalOrNull()
  }

  private fun expectedRounded(value: Decimal, places: Int, mode: RoundingMode): Decimal? {
    val rounded =
      try {
        value.toBigDecimal().setScale(places, mode.toJava())
      } catch (_: ArithmeticException) {
        return null
      }
    return rounded.toDecimalOrNull()
  }

  private fun BigDecimal.toDecimalOrNull(): Decimal? {
    if (compareTo(DECIMAL_MIN) < 0 || compareTo(DECIMAL_MAX) > 0) return null
    val stripped = stripTrailingZeros()
    if (stripped.scale() > DECIMAL_SCALE) return null
    return Decimal.parse(toPlainString())
  }

  private fun Decimal.toBigDecimal(): BigDecimal = BigDecimal(toString())

  private fun RoundingMode.toJava(): JavaRoundingMode =
    when (this) {
      RoundingMode.UP -> JavaRoundingMode.UP
      RoundingMode.DOWN -> JavaRoundingMode.DOWN
      RoundingMode.CEILING -> JavaRoundingMode.CEILING
      RoundingMode.FLOOR -> JavaRoundingMode.FLOOR
      RoundingMode.HALF_UP -> JavaRoundingMode.HALF_UP
      RoundingMode.HALF_DOWN -> JavaRoundingMode.HALF_DOWN
      RoundingMode.HALF_EVEN -> JavaRoundingMode.HALF_EVEN
      RoundingMode.UNNECESSARY -> JavaRoundingMode.UNNECESSARY
    }

  private fun randomDecimal(random: Random): Decimal {
    return when (random.nextInt(4)) {
      0 -> Decimal.of(random.nextInt(-10_000, 10_001))
      1 -> decimalFromScaled(random.nextLong(-1_000_000_000L, 1_000_000_001L))
      2 ->
        decimalFromScaled(
          when (random.nextInt(4)) {
            0 -> Long.MAX_VALUE
            1 -> Long.MIN_VALUE
            2 -> Long.MAX_VALUE - random.nextLong(0L, 1_000L)
            else -> Long.MIN_VALUE + random.nextLong(0L, 1_000L)
          }
        )
      else -> decimalFromScaled(random.nextLong())
    }
  }

  private fun randomNonZeroDecimal(random: Random): Decimal {
    var value: Decimal
    do {
      value = randomDecimal(random)
    } while (value == Decimal.ZERO)
    return value
  }

  private fun decimalFromScaled(scaled: Long): Decimal {
    if (scaled == 0L) return Decimal.ZERO
    val negative = scaled < 0L
    val magnitude =
      if (scaled == Long.MIN_VALUE) {
        "9223372036.854775808"
      } else {
        val abs = if (negative) -scaled else scaled
        val integerPart = abs / DECIMAL_SCALE_FACTOR
        val fraction = abs % DECIMAL_SCALE_FACTOR
        if (fraction == 0L) {
          integerPart.toString()
        } else {
          "$integerPart.${fraction.toString().padStart(DECIMAL_SCALE, '0')}"
        }
      }
    return Decimal.parse(if (negative) "-$magnitude" else magnitude)
  }

  private companion object {
    val DECIMAL_MIN: BigDecimal = BigDecimal("-9223372036.854775808")
    val DECIMAL_MAX: BigDecimal = BigDecimal("9223372036.854775807")
  }
}
