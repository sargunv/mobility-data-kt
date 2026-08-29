package dev.sargunv.mobilitydata.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GtfsLocalTimeTest {
  @Test
  fun acceptsMidnightAndEndOfDay() {
    assertEquals(0, GtfsLocalTime(0, 0, 0).hours)
    assertEquals(24, GtfsLocalTime(24, 0, 0).hours)
  }

  @Test
  fun rejectsHoursAbove24() {
    assertFailsWith<IllegalArgumentException> { GtfsLocalTime(25, 0, 0) }
  }

  @Test
  fun rejectsTimeGreaterThan240000() {
    assertFailsWith<IllegalArgumentException> { GtfsLocalTime(24, 0, 1) }
    assertFailsWith<IllegalArgumentException> { GtfsLocalTime(24, 1, 0) }
  }

  @Test
  fun compareToOrdersByHoursMinutesSeconds() {
    assertTrue(GtfsLocalTime(7, 0, 0) < GtfsLocalTime(9, 0, 0))
    assertTrue(GtfsLocalTime(24, 0, 0) > GtfsLocalTime(23, 59, 59))
    assertEquals(0, GtfsLocalTime(8, 30, 0).compareTo(GtfsLocalTime(8, 30, 0)))
  }
}
