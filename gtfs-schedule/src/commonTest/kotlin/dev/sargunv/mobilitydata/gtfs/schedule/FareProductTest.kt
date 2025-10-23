package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  fare_product_id,fare_product_name,rider_category_id,fare_media_id,amount,currency
  single_ride,Single Ride,,,2.75,USD
  senior_discount,Senior Discount,senior,clipper,1.35,USD
  """
    .trimIndent()

private val expected =
  listOf(
    FareProduct(
      fareProductId = "single_ride",
      fareProductName = "Single Ride",
      riderCategoryId = null,
      fareMediaId = null,
      amount = 2.75,
      currency = "USD",
    ),
    FareProduct(
      fareProductId = "senior_discount",
      fareProductName = "Senior Discount",
      riderCategoryId = "senior",
      fareMediaId = "clipper",
      amount = 1.35,
      currency = "USD",
    ),
  )

class FareProductTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<FareProduct>(csvContent)
    assertEquals(expected, decoded)
  }
}
