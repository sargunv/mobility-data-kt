package dev.sargunv.mobilitydata.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class RgbColorTest {
  @Test
  fun toStringUsesSixUppercaseHexDigits() {
    assertEquals("#FFFFFF", RgbColor(0xFFFFFF).toString())
    assertEquals("#ABCDEF", RgbColor(0xABCDEF).toString())
  }

  @Test
  fun toStringPreservesLeadingZeros() {
    assertEquals("#000000", RgbColor(0x000000).toString())
    assertEquals("#000001", RgbColor(0x000001).toString())
    assertEquals("#0000AB", RgbColor(0x0000AB).toString())
    assertEquals("#000ABC", RgbColor(0x000ABC).toString())
    assertEquals("#005B95", RgbColor(0x005B95).toString())
    assertEquals("#0ABCDE", RgbColor(0x0ABCDE).toString())
  }
}
