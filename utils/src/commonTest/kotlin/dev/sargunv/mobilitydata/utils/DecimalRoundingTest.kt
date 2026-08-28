package dev.sargunv.mobilitydata.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DecimalRoundingTest {
  private fun d(value: String): Decimal = Decimal.parse(value)

  @Test
  fun decimalPlaceRoundingExamples() {
    assertEquals(d("2.56"), d("2.555").round(2, RoundingMode.HALF_UP))
    assertEquals(d("2.56"), d("2.555").round(2, RoundingMode.HALF_EVEN))
    assertEquals(d("2.54"), d("2.545").round(2, RoundingMode.HALF_EVEN))
    assertEquals(d("2.55"), d("2.555").round(2, RoundingMode.DOWN))
    assertEquals(d("2.56"), d("2.555").round(2, RoundingMode.UP))
    assertEquals(d("2.56"), d("2.555").round(2, RoundingMode.CEILING))
    assertEquals(d("2.55"), d("2.555").round(2, RoundingMode.FLOOR))
    assertEquals(d("2.555"), d("2.555").round(3, RoundingMode.UNNECESSARY))
    assertFailsWith<ArithmeticException> { d("2.555").round(2, RoundingMode.UNNECESSARY) }
    assertFailsWith<IllegalArgumentException> { d("1").round(-1, RoundingMode.DOWN) }
    assertFailsWith<IllegalArgumentException> { d("1").round(10, RoundingMode.DOWN) }
  }

  @Test
  fun decimalPlaceRoundingModes() {
    val value = d("1.235")
    val negative = d("-1.235")
    assertEquals(d("1.24"), value.round(2, RoundingMode.UP))
    assertEquals(d("1.23"), value.round(2, RoundingMode.DOWN))
    assertEquals(d("1.24"), value.round(2, RoundingMode.CEILING))
    assertEquals(d("1.23"), value.round(2, RoundingMode.FLOOR))
    assertEquals(d("1.24"), value.round(2, RoundingMode.HALF_UP))
    assertEquals(d("1.23"), value.round(2, RoundingMode.HALF_DOWN))
    assertEquals(d("1.24"), value.round(2, RoundingMode.HALF_EVEN))

    assertEquals(d("-1.24"), negative.round(2, RoundingMode.UP))
    assertEquals(d("-1.23"), negative.round(2, RoundingMode.DOWN))
    assertEquals(d("-1.23"), negative.round(2, RoundingMode.CEILING))
    assertEquals(d("-1.24"), negative.round(2, RoundingMode.FLOOR))
    assertEquals(d("-1.24"), negative.round(2, RoundingMode.HALF_UP))
    assertEquals(d("-1.23"), negative.round(2, RoundingMode.HALF_DOWN))
    assertEquals(d("-1.24"), negative.round(2, RoundingMode.HALF_EVEN))
  }

  @Test
  fun halfEvenRetainsEvenDigit() {
    assertEquals(d("2.24"), d("2.245").round(2, RoundingMode.HALF_EVEN))
    assertEquals(d("2.26"), d("2.255").round(2, RoundingMode.HALF_EVEN))
    assertEquals(d("-2.24"), d("-2.245").round(2, RoundingMode.HALF_EVEN))
    assertEquals(d("-2.26"), d("-2.255").round(2, RoundingMode.HALF_EVEN))
  }

  @Test
  fun roundedDivisionModes() {
    val one = d("1")
    val three = d("3")

    assertEquals(d("0.333333334"), one.divide(three, RoundingMode.UP))
    assertEquals(d("0.333333333"), one.divide(three, RoundingMode.DOWN))
    assertEquals(d("0.333333334"), one.divide(three, RoundingMode.CEILING))
    assertEquals(d("0.333333333"), one.divide(three, RoundingMode.FLOOR))
    assertEquals(d("0.333333333"), one.divide(three, RoundingMode.HALF_UP))
    assertEquals(d("0.333333333"), one.divide(three, RoundingMode.HALF_DOWN))
    assertEquals(d("0.333333333"), one.divide(three, RoundingMode.HALF_EVEN))
    assertFailsWith<ArithmeticException> { one.divide(three, RoundingMode.UNNECESSARY) }

    assertEquals(d("-0.333333334"), (-one).divide(three, RoundingMode.UP))
    assertEquals(d("-0.333333333"), (-one).divide(three, RoundingMode.DOWN))
    assertEquals(d("-0.333333333"), (-one).divide(three, RoundingMode.CEILING))
    assertEquals(d("-0.333333334"), (-one).divide(three, RoundingMode.FLOOR))
    assertEquals(d("-0.333333333"), (-one).divide(three, RoundingMode.HALF_UP))
    assertEquals(d("-0.333333333"), (-one).divide(three, RoundingMode.HALF_DOWN))
    assertEquals(d("-0.333333333"), (-one).divide(three, RoundingMode.HALF_EVEN))
  }

  @Test
  fun roundedDivisionAboveHalfAndTies() {
    assertEquals(d("0.666666667"), d("2").divide(d("3"), RoundingMode.HALF_UP))
    assertEquals(d("0.666666667"), d("2").divide(d("3"), RoundingMode.HALF_DOWN))
    assertEquals(d("0.666666667"), d("2").divide(d("3"), RoundingMode.UP))
    assertEquals(d("0.666666666"), d("2").divide(d("3"), RoundingMode.DOWN))

    val tie = d("1").divide(d("400000000"), RoundingMode.HALF_UP)
    assertEquals(d("0.000000003"), tie)
    assertEquals(d("0.000000002"), d("1").divide(d("400000000"), RoundingMode.HALF_DOWN))
    assertEquals(d("0.000000002"), d("1").divide(d("400000000"), RoundingMode.HALF_EVEN))
    assertEquals(d("-0.000000003"), d("-1").divide(d("400000000"), RoundingMode.HALF_UP))
    assertEquals(d("-0.000000002"), d("-1").divide(d("400000000"), RoundingMode.HALF_DOWN))
    assertEquals(d("-0.000000002"), d("-1").divide(d("400000000"), RoundingMode.HALF_EVEN))
  }

  @Test
  fun roundedDivisionExactAndZero() {
    assertEquals(d("0.5"), d("1").divide(d("2"), RoundingMode.UNNECESSARY))
    assertEquals(d("0.5"), d("1").divide(d("2"), RoundingMode.HALF_UP))
    assertEquals(Decimal.ZERO, Decimal.ZERO.divide(d("7"), RoundingMode.HALF_UP))
    assertFailsWith<ArithmeticException> { d("1").divide(Decimal.ZERO, RoundingMode.HALF_UP) }
  }
}
