package dev.sargunv.mobilitydata.utils

import de.westnordost.osm_opening_hours.model.Range
import de.westnordost.osm_opening_hours.model.Rule
import de.westnordost.osm_opening_hours.model.TwentyFourSeven
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class OsmOpeningHoursTest {
  @Test
  fun parseTwentyFourSeven() {
    val hours = OsmOpeningHours("24/7")

    assertEquals(listOf(Rule(TwentyFourSeven)), hours.rules)
    assertTrue(hours.containsTimeSpans())
  }

  @Test
  fun parseOfficialGbfsExample() {
    val hours = OsmOpeningHours("Apr 1-Nov 3 00:00-24:00")

    assertEquals("Apr 01-Nov 03 00:00-24:00", hours.toString())
    assertTrue(hours.rules.single().selector is Range)
  }

  @Test
  fun parseRejectsInvalidWhenStrict() {
    assertFails { OsmOpeningHours("Apr 1-Nov 3 00:00-24:00", lenient = false) }
  }
}
