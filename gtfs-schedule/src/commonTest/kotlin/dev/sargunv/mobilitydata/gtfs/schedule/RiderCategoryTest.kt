package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  rider_category_id,rider_category_name,is_default_fare_category,eligibility_url
  regular,Regular Fare,1,
  senior,Senior Citizen,0,https://example.com/senior
  """
    .trimIndent()

private val expected =
  listOf(
    RiderCategory(
      riderCategoryId = "regular",
      riderCategoryName = "Regular Fare",
      isDefaultFareCategory = IsDefaultFareCategory.Yes,
      eligibilityUrl = null,
    ),
    RiderCategory(
      riderCategoryId = "senior",
      riderCategoryName = "Senior Citizen",
      isDefaultFareCategory = IsDefaultFareCategory.No,
      eligibilityUrl = "https://example.com/senior",
    ),
  )

class RiderCategoryTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<RiderCategory>(csvContent)
    assertEquals(expected, decoded)
  }
}
