package dev.sargunv.mobilitydata.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DecimalParseTest {
  @Test
  fun parsesSuccessfulValues() {
    val cases =
      listOf(
        "0" to "0",
        "-0" to "0",
        "1" to "1",
        "-1" to "-1",
        "1.5" to "1.5",
        "-1.5" to "-1.5",
        "1.000000001" to "1.000000001",
        "0.000000001" to "0.000000001",
        "123456789.123456789" to "123456789.123456789",
        "00012" to "12",
        "00012.3400" to "12.34",
        "1.2300000000" to "1.23",
        "1.000000000000000000" to "1",
        "1e3" to "1000",
        "1E3" to "1000",
        "1e+2" to "100",
        "1e-2" to "0.01",
        "1E-2" to "0.01",
        "123e-9" to "0.000000123",
        "1.23e2" to "123",
        "12.3e-1" to "1.23",
        "-1e-2" to "-0.01",
        "-1.5E+1" to "-15",
        "1.0e-9" to "0.000000001",
        "1e01" to "10",
        "9e9" to "9000000000",
        "0.0000000001e1" to "0.000000001",
        "9223372036854775807e-9" to "9223372036.854775807",
        "-9223372036854775808e-9" to "-9223372036.854775808",
        "92233720368547758070e-10" to "9223372036.854775807",
        "-92233720368547758080e-10" to "-9223372036.854775808",
      )
    for ((input, canonical) in cases) {
      assertEquals(canonical, Decimal.parse(input).toString(), input)
    }
  }

  @Test
  fun rejectsMalformedSyntax() {
    val cases =
      listOf(
        "",
        ".",
        "-",
        "+1",
        ".5",
        "1.",
        "-.5",
        "1..2",
        "abc",
        "NaN",
        "Infinity",
        "-Infinity",
        "1e",
        "1e+",
        "1e-",
        "e3",
        "1e1.5",
      )
    for (input in cases) {
      assertFailsWith<NumberFormatException>(input) { Decimal.parse(input) }
      assertNull(Decimal.parseOrNull(input), input)
    }
  }

  @Test
  fun rejectsExcessPrecision() {
    val cases =
      listOf("0.0000000001", "1.1234567891", "1.2300000001", "1e-10", "123e-10", "1.23e-8")
    for (input in cases) {
      assertFailsWith<ArithmeticException>(input) { Decimal.parse(input) }
      assertNull(Decimal.parseOrNull(input), input)
    }
  }

  @Test
  fun rejectsExponentOverflow() {
    val cases =
      listOf("9223372036.854775807e1", "-9223372036.854775808e1", "1e10", "10e9", "1e40", "1e41")
    for (input in cases) {
      assertFailsWith<ArithmeticException>(input) { Decimal.parse(input) }
      assertNull(Decimal.parseOrNull(input), input)
    }
  }

  @Test
  fun parsesExponentFormFromEffectiveScale() {
    assertEquals(Decimal.parse("0.000000001"), Decimal.parse("0.0000000001e1"))
    assertEquals(Decimal.parse("9223372036.854775807"), Decimal.parse("9223372036854775807e-9"))
    assertEquals(Decimal.parse("-9223372036.854775808"), Decimal.parse("-9223372036854775808e-9"))
    assertEquals(Decimal.parse("9223372036.854775807"), Decimal.parse("92233720368547758070e-10"))
    assertEquals(Decimal.parse("-9223372036.854775808"), Decimal.parse("-92233720368547758080e-10"))
  }

  @Test
  fun parsesRangeBoundaries() {
    val max = Decimal.parse("9223372036.854775807")
    val min = Decimal.parse("-9223372036.854775808")
    assertEquals("9223372036.854775807", max.toString())
    assertEquals("-9223372036.854775808", min.toString())
    assertFailsWith<ArithmeticException> { Decimal.parse("9223372036.854775808") }
    assertFailsWith<ArithmeticException> { Decimal.parse("-9223372036.854775809") }
    assertNull(Decimal.parseOrNull("9223372036.854775808"))
    assertNull(Decimal.parseOrNull("-9223372036.854775809"))
  }

  @Test
  fun integerFactoriesAreExact() {
    assertEquals(Decimal.ZERO, Decimal.of(0))
    assertEquals(Decimal.ONE, Decimal.of(1))
    assertEquals(Decimal.TEN, Decimal.of(10))
    assertEquals(Decimal.parse("-3"), Decimal.of(-3))
    assertEquals(Decimal.parse("2147483647"), Decimal.of(Int.MAX_VALUE))
    assertEquals(Decimal.parse("9223372036"), Decimal.of(9_223_372_036L))
    assertFailsWith<ArithmeticException> { Decimal.of(9_223_372_037L) }
    assertFailsWith<ArithmeticException> { Decimal.of(Long.MAX_VALUE) }
    assertFailsWith<ArithmeticException> { Decimal.of(Long.MIN_VALUE) }
  }

  @Test
  fun constantsDoNotRequireParsing() {
    assertEquals("0", Decimal.ZERO.toString())
    assertEquals("1", Decimal.ONE.toString())
    assertEquals("10", Decimal.TEN.toString())
    assertTrue(Decimal.ZERO < Decimal.ONE)
    assertTrue(Decimal.ONE < Decimal.TEN)
  }

  @Test
  fun equalValuesNormalizeLexicalScale() {
    assertEquals(Decimal.parse("2.5"), Decimal.parse("2.50"))
    assertEquals(Decimal.parse("2.5").hashCode(), Decimal.parse("2.50").hashCode())
    assertEquals(Decimal.parse("2.500000000"), Decimal.parse("2.5"))
    assertEquals(Decimal.ZERO, Decimal.parse("-0"))
    assertEquals(Decimal.parse("0").hashCode(), Decimal.parse("-0").hashCode())
  }
}
