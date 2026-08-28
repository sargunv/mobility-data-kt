package dev.sargunv.mobilitydata.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DecimalFormatTest {
  @Test
  fun canonicalFormatting() {
    val cases =
      listOf(
        "0" to "0",
        "-0" to "0",
        "1" to "1",
        "1.5" to "1.5",
        "1.500000000" to "1.5",
        "0.000000001" to "0.000000001",
        "-12.34" to "-12.34",
        "2.0" to "2",
        "2.50" to "2.5",
        "0.001" to "0.001",
        "-3.250" to "-3.25",
        "123456789.123456789" to "123456789.123456789",
        "9223372036.854775807" to "9223372036.854775807",
        "-9223372036.854775808" to "-9223372036.854775808",
      )
    for ((input, expected) in cases) {
      assertEquals(expected, Decimal.parse(input).toString(), input)
    }
  }

  @Test
  fun fixedDecimalPlaceFormatting() {
    assertEquals("2.50", Decimal.parse("2.5").toString(decimalPlaces = 2))
    assertEquals("2.0000", Decimal.parse("2").toString(decimalPlaces = 4))
    assertEquals("2.50", Decimal.parse("2.500").toString(decimalPlaces = 2))
    assertEquals("2", Decimal.parse("2.000").toString(decimalPlaces = 0))
    assertEquals("-3.250", Decimal.parse("-3.25").toString(decimalPlaces = 3))
    assertFailsWith<ArithmeticException> { Decimal.parse("2.501").toString(decimalPlaces = 2) }
    assertFailsWith<IllegalArgumentException> { Decimal.parse("1").toString(decimalPlaces = -1) }
    assertFailsWith<IllegalArgumentException> { Decimal.parse("1").toString(decimalPlaces = 10) }
  }

  @Test
  fun conversions() {
    assertEquals(2.5, Decimal.parse("2.5").toDouble())
    assertEquals(-1.25f, Decimal.parse("-1.25").toFloat())
    assertEquals(12L, Decimal.parse("12").toLongExact())
    assertEquals(-3, Decimal.parse("-3.000").toIntExact())
    assertFailsWith<ArithmeticException> { Decimal.parse("1.5").toLongExact() }
    assertFailsWith<ArithmeticException> { Decimal.parse("1.5").toIntExact() }
    assertFailsWith<ArithmeticException> { Decimal.parse("2147483648").toIntExact() }
    assertFailsWith<ArithmeticException> { Decimal.parse("-2147483649").toIntExact() }
  }
}
