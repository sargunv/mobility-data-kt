package dev.sargunv.mobilitydata.utils.serialization

import de.westnordost.osm_opening_hours.model.Holiday
import de.westnordost.osm_opening_hours.model.OpeningHours
import de.westnordost.osm_opening_hours.model.Range
import de.westnordost.osm_opening_hours.model.Rule
import de.westnordost.osm_opening_hours.model.TwentyFourSeven
import de.westnordost.osm_opening_hours.model.Weekday
import de.westnordost.osm_opening_hours.model.WeekdayRange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class OsmOpeningHoursSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = OsmOpeningHoursSerializer::class) val hours: OpeningHours
  )

  private val json = Json

  @Test
  fun testSerializeTwentyFourSeven() {
    val testData = TestData(OpeningHours(Rule(TwentyFourSeven)))

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val hours = jsonElement.jsonObject["hours"]!!.jsonPrimitive

    assertEquals("24/7", hours.content)
  }

  @Test
  fun testDeserializeTwentyFourSeven() {
    val result = json.decodeFromString(TestData.serializer(), """{"hours":"24/7"}""")

    assertEquals(OpeningHours(Rule(TwentyFourSeven)), result.hours)
    assertTrue(result.hours.containsTimeSpans())
  }

  @Test
  fun testDeserializeOfficialGbfsExample() {
    // GBFS documents opening_hours with unpadded month days, which requires lenient parsing.
    val result =
      json.decodeFromString(TestData.serializer(), """{"hours":"Apr 1-Nov 3 00:00-24:00"}""")

    assertEquals("Apr 01-Nov 03 00:00-24:00", result.hours.toString())
    assertEquals(1, result.hours.rules.size)
    assertTrue(result.hours.rules.single().selector is Range)
  }

  @Test
  fun testDeserializeHolidaysMixedWithWeekdays() {
    val result =
      json.decodeFromString(TestData.serializer(), """{"hours":"Mo-Su,PH 00:00-24:00"}""")

    val selector = result.hours.rules.single().selector as Range
    assertEquals(listOf(WeekdayRange(Weekday.Monday, Weekday.Sunday)), selector.weekdays)
    assertEquals(listOf(Holiday.PublicHoliday), selector.holidays)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(OpeningHours(Rule(TwentyFourSeven)))

    val jsonString = json.encodeToString(TestData.serializer(), original)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testInvalidOpeningHours() {
    assertFails {
      json.decodeFromString(TestData.serializer(), """{"hours":"not opening hours"}""")
    }
  }
}
