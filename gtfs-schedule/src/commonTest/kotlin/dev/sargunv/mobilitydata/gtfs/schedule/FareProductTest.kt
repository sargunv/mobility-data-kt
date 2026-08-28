package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Decimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.serialization.SerializationException

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
      amount = Decimal.parse("2.75"),
      currency = "USD",
    ),
    FareProduct(
      fareProductId = "senior_discount",
      fareProductName = "Senior Discount",
      riderCategoryId = "senior",
      fareMediaId = "clipper",
      amount = Decimal.parse("1.35"),
      currency = "USD",
    ),
  )

class FareProductTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<FareProduct>(csvContent)
    assertEquals(expected, decoded)
  }

  @Test
  fun decodeAllowsUnpaddedAmount() {
    val csv = // language=CSV
      """
      fare_product_id,amount,currency
      p,2.5,USD
      """
        .trimIndent()

    val decoded = GtfsCsv.decodeFromString<FareProduct>(csv).single()
    assertEquals(Decimal.parse("2.5"), decoded.amount)
    assertEquals("USD", decoded.currency)
  }

  @Test
  fun encodeUsesIso4217MinorUnits() {
    val cases =
      listOf(
        Triple(Decimal.parse("2.5"), "USD", "2.50"),
        Triple(Decimal.of(250), "JPY", "250"),
        Triple(Decimal.parse("1.5"), "KWD", "1.500"),
        Triple(Decimal.parse("0.1234"), "CLF", "0.1234"),
        Triple(Decimal.parse("0.1234"), "UYW", "0.1234"),
      )
    for ((amount, currency, expectedAmount) in cases) {
      val encoded =
        encodeAmountAndCurrency(
          FareProduct(fareProductId = "p1", amount = amount, currency = currency)
        )
      assertEquals(expectedAmount to currency, encoded, currency)
    }
  }

  @Test
  fun encodeRejectsExcessPrecision() {
    assertFailsWith<SerializationException> {
      GtfsCsv.encodeToString(
        listOf(FareProduct(fareProductId = "p1", amount = Decimal.parse("2.501"), currency = "USD"))
      )
    }
  }

  @Test
  fun encodeRejectsUnknownCurrency() {
    assertFailsWith<SerializationException> {
      GtfsCsv.encodeToString(
        listOf(FareProduct(fareProductId = "p1", amount = Decimal.of(1), currency = "ZZZ"))
      )
    }
  }

  private fun encodeAmountAndCurrency(product: FareProduct): Pair<String, String> {
    val csv = GtfsCsv.encodeToString(listOf(product)).trim()
    val lines = csv.lines()
    val headers = lines[0].split(',')
    val values = lines[1].split(',')
    return values[headers.indexOf("amount")] to values[headers.indexOf("currency")]
  }
}
