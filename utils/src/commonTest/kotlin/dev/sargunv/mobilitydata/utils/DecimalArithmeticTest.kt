package dev.sargunv.mobilitydata.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DecimalArithmeticTest {
  private fun d(value: String): Decimal = Decimal.parse(value)

  @Test
  fun additionAndSubtraction() {
    assertEquals(d("3"), d("1") + d("2"))
    assertEquals(d("-1"), d("1") + d("-2"))
    assertEquals(d("-3"), d("-1") + d("-2"))
    assertEquals(d("1.5"), d("0") + d("1.5"))
    assertEquals(d("0.3"), d("0.1") + d("0.2"))
    assertEquals(d("1"), d("3") - d("2"))
    assertEquals(d("5"), d("3") - d("-2"))
    assertEquals(d("-1"), d("-3") - d("-2"))
    assertEquals(d("-1.5"), -d("1.5"))
    assertEquals(d("1.5"), d("-1.5").abs())
    assertEquals(Decimal.ZERO, Decimal.ZERO.abs())
    assertEquals(Decimal.ZERO, -Decimal.ZERO)
  }

  @Test
  fun additionBoundaries() {
    val max = d("9223372036.854775807")
    val min = d("-9223372036.854775808")
    assertEquals(d("-0.000000001"), max + min)
    assertFailsWith<ArithmeticException> { max + Decimal.ONE }
    assertFailsWith<ArithmeticException> { min - Decimal.ONE }
    assertFailsWith<ArithmeticException> { max + max }
    assertFailsWith<ArithmeticException> { -min }
    assertFailsWith<ArithmeticException> { min.abs() }
  }

  @Test
  fun multiplication() {
    assertEquals(d("3"), d("1.5") * d("2"))
    assertEquals(d("0.02"), d("0.1") * d("0.2"))
    assertEquals(d("-0.02"), d("-0.1") * d("0.2"))
    assertEquals(d("0.02"), d("-0.1") * d("-0.2"))
    assertEquals(Decimal.ZERO, d("1.5") * Decimal.ZERO)
    assertEquals(d("2.25"), d("1.5") * d("1.5"))
    assertEquals(d("0.015625"), d("0.125") * d("0.125"))
    assertEquals(d("0.000000001"), d("0.000000001") * Decimal.ONE)
    assertFailsWith<ArithmeticException> { d("0.000000001") * d("0.1") }
    assertFailsWith<ArithmeticException> { d("0.000000003") * d("0.5") }
  }

  @Test
  fun multiplicationIntermediateOverflowStillFits() {
    assertEquals(d("9000000000"), d("9000000000") * Decimal.ONE)
    assertEquals(d("5"), d("0.000000001") * d("5000000000"))
    assertEquals(d("-9000000000"), d("9000000000") * d("-1"))
  }

  @Test
  fun multiplicationOverflowsRange() {
    val large = d("3000000000")
    assertFailsWith<ArithmeticException> { large * d("4") }
    assertFailsWith<ArithmeticException> { d("9223372036.854775807") * d("2") }
  }

  @Test
  fun exactDivision() {
    assertEquals(d("0.5"), d("1") / d("2"))
    assertEquals(d("0.25"), d("1") / d("4"))
    assertEquals(d("1.25"), d("10") / d("8"))
    assertEquals(d("-0.5"), d("-1") / d("2"))
    assertEquals(d("0.5"), d("-1") / d("-2"))
    assertEquals(Decimal.ZERO, Decimal.ZERO / d("3"))
    assertFailsWith<ArithmeticException> { d("1") / d("3") }
    assertFailsWith<ArithmeticException> { d("2") / d("7") }
    assertFailsWith<ArithmeticException> { d("1") / Decimal.ZERO }
  }

  @Test
  fun exactDivisionIntermediateOverflowStillFits() {
    assertEquals(d("9000000000"), d("9000000000") / Decimal.ONE)
    assertEquals(d("0.000000002"), d("2") / d("1000000000"))
  }

  @Test
  fun comparison() {
    assertTrue(d("1") < d("2"))
    assertTrue(d("-1") < d("1"))
    assertTrue(d("1.000000001") > d("1"))
    assertEquals(0, d("2.5").compareTo(d("2.50")))
  }
}
