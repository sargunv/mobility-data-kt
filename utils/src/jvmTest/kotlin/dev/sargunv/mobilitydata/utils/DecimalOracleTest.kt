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
    forEachPair { a, b -> assertMatchesOracle("$a * $b", expectedProduct(a, b)) { a * b } }
  }

  @Test
  fun exactDivMatchesBigDecimal() {
    forEachPair { a, b ->
      if (b == Decimal.ZERO) {
        assertFailsWith<ArithmeticException>("$a / 0") { a / b }
      } else {
        assertMatchesOracle("$a / $b", expectedExactQuotient(a, b)) { a / b }
      }
    }
  }

  @Test
  fun roundedDivideMatchesBigDecimal() {
    val random = Random(20260828L)
    repeat(200) {
      val a = randomDecimal(random)
      val b = randomNonZeroDecimal(random)
      val mode = RoundingMode.entries.random(random)
      assertRoundedDivide(a, b, mode)
    }
    for (a in interestingValues) {
      for (b in interestingValues) {
        if (b == Decimal.ZERO) continue
        for (mode in RoundingMode.entries) {
          assertRoundedDivide(a, b, mode)
        }
      }
    }
  }

  @Test
  fun roundMatchesBigDecimal() {
    val random = Random(20260829L)
    repeat(300) {
      assertRound(randomDecimal(random), random.nextInt(0, 10), RoundingMode.entries.random(random))
    }
    for (value in
      listOf(
        Decimal.parse("2.555"),
        Decimal.parse("-2.555"),
        Decimal.fromScaled(Long.MAX_VALUE),
        Decimal.fromScaled(Long.MIN_VALUE),
        Decimal.parse("0.000000001"),
      )) {
      for (places in 0..9) {
        for (mode in RoundingMode.entries) {
          assertRound(value, places, mode)
        }
      }
    }
  }

  private fun assertRoundedDivide(a: Decimal, b: Decimal, mode: RoundingMode) {
    assertMatchesOracle("$a.divide($b, $mode)", expectedRoundedQuotient(a, b, mode)) {
      a.divide(b, mode)
    }
  }

  private fun assertRound(value: Decimal, places: Int, mode: RoundingMode) {
    assertMatchesOracle("$value.round($places, $mode)", expectedRounded(value, places, mode)) {
      value.round(places, mode)
    }
  }

  private fun assertMatchesOracle(label: String, expected: Decimal?, actual: () -> Decimal) {
    if (expected == null) {
      assertFailsWith<ArithmeticException>(label) { actual() }
    } else {
      assertEquals(expected, actual(), label)
    }
  }

  private inline fun forEachPair(block: (Decimal, Decimal) -> Unit) {
    val random = Random(20260827L)
    repeat(250) { block(randomDecimal(random), randomDecimal(random)) }
    for (a in interestingValues) {
      for (b in interestingValues) {
        block(a, b)
      }
    }
  }

  private fun expectedProduct(a: Decimal, b: Decimal): Decimal? =
    a.toBigDecimal().multiply(b.toBigDecimal()).toDecimalOrNull()

  private fun expectedExactQuotient(a: Decimal, b: Decimal): Decimal? =
    try {
      a.toBigDecimal().divide(b.toBigDecimal()).toDecimalOrNull()
    } catch (_: ArithmeticException) {
      null
    }

  private fun expectedRoundedQuotient(a: Decimal, b: Decimal, mode: RoundingMode): Decimal? =
    try {
      a.toBigDecimal().divide(b.toBigDecimal(), DECIMAL_SCALE, mode.toJava()).toDecimalOrNull()
    } catch (_: ArithmeticException) {
      null
    }

  private fun expectedRounded(value: Decimal, places: Int, mode: RoundingMode): Decimal? =
    try {
      value.toBigDecimal().setScale(places, mode.toJava()).toDecimalOrNull()
    } catch (_: ArithmeticException) {
      null
    }

  private fun BigDecimal.toDecimalOrNull(): Decimal? {
    if (compareTo(DECIMAL_MIN) < 0 || compareTo(DECIMAL_MAX) > 0) return null
    val stripped = stripTrailingZeros()
    if (stripped.scale() > DECIMAL_SCALE) return null
    return Decimal.parse(stripped.toPlainString())
  }

  private fun Decimal.toBigDecimal(): BigDecimal = BigDecimal(toString())

  private fun RoundingMode.toJava(): JavaRoundingMode = JavaRoundingMode.valueOf(name)

  private fun randomDecimal(random: Random): Decimal =
    when (random.nextInt(4)) {
      0 -> Decimal.of(random.nextInt(-10_000, 10_001))
      1 -> Decimal.fromScaled(random.nextLong(-1_000_000_000L, 1_000_000_001L))
      2 ->
        Decimal.fromScaled(
          when (random.nextInt(4)) {
            0 -> Long.MAX_VALUE
            1 -> Long.MIN_VALUE
            2 -> Long.MAX_VALUE - random.nextLong(0L, 1_000L)
            else -> Long.MIN_VALUE + random.nextLong(0L, 1_000L)
          }
        )
      else -> Decimal.fromScaled(random.nextLong())
    }

  private fun randomNonZeroDecimal(random: Random): Decimal {
    var value: Decimal
    do {
      value = randomDecimal(random)
    } while (value == Decimal.ZERO)
    return value
  }

  private companion object {
    val DECIMAL_MIN: BigDecimal = BigDecimal("-9223372036.854775808")
    val DECIMAL_MAX: BigDecimal = BigDecimal("9223372036.854775807")
    val interestingValues =
      listOf(
        Decimal.ZERO,
        Decimal.ONE,
        Decimal.TEN,
        Decimal.parse("0.1"),
        Decimal.parse("0.2"),
        Decimal.parse("0.000000001"),
        Decimal.parse("-0.000000001"),
        Decimal.parse("0.5"),
        Decimal.parse("-0.5"),
        Decimal.parse("1.5"),
        Decimal.parse("-3"),
        Decimal.parse("5000000000"),
        Decimal.parse("9000000000"),
        Decimal.fromScaled(Long.MAX_VALUE),
        Decimal.fromScaled(Long.MIN_VALUE),
      )
  }
}
