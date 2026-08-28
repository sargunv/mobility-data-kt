package dev.sargunv.mobilitydata.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CurrencyCodeTest {
  @Test
  fun defaultFractionDigitsForKnownCurrencies() {
    assertEquals(2, "USD".defaultFractionDigits)
    assertEquals(0, "JPY".defaultFractionDigits)
    assertEquals(3, "KWD".defaultFractionDigits)
    assertEquals(4, "CLF".defaultFractionDigits)
    assertEquals(4, "UYW".defaultFractionDigits)
    assertEquals(2, "EUR".defaultFractionDigits)
  }

  @Test
  fun defaultFractionDigitsIsNullForUnknownOrUnusableCodes() {
    assertNull("ZZZ".defaultFractionDigits)
    assertNull("XAU".defaultFractionDigits)
    assertNull("XDR".defaultFractionDigits)
    assertNull("XXX".defaultFractionDigits)
    assertNull("".defaultFractionDigits)
  }
}
