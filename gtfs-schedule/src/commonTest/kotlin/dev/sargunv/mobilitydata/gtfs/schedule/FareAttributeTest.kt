package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  fare_id,price,currency_type,payment_method,transfers,agency_id,transfer_duration
  p,1.25,USD,0,0,,
  a,5.25,USD,0,,,
  """
    .trimIndent()

private val expected =
  listOf(
    FareAttribute(
      fareId = "p",
      price = 1.25,
      currencyType = "USD",
      paymentMethod = PaymentMethod.OnBoard,
      transfers = 0,
    ),
    FareAttribute(
      fareId = "a",
      price = 5.25,
      currencyType = "USD",
      paymentMethod = PaymentMethod.OnBoard,
      transfers = null,
    ),
  )

class FareAttributeTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<FareAttribute>(csvContent)
    assertEquals(expected, decoded)
  }
}
