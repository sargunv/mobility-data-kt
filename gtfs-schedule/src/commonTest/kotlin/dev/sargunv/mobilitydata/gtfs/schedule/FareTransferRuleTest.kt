package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

private val csvContent = // language=CSV
  """
  from_leg_group_id,to_leg_group_id,transfer_count,duration_limit,duration_limit_type,fare_transfer_type,fare_product_id
  local,express,,-1,,0,
  express,local,1,3600,2,1,transfer_product
  """
    .trimIndent()

private val expected =
  listOf(
    FareTransferRule(
      fromLegGroupId = "local",
      toLegGroupId = "express",
      transferCount = null,
      durationLimit = (-1).seconds,
      durationLimitType = null,
      fareTransferType = FareTransferType.FromLegPlusTransfer,
      fareProductId = null,
    ),
    FareTransferRule(
      fromLegGroupId = "express",
      toLegGroupId = "local",
      transferCount = 1,
      durationLimit = 3600.seconds,
      durationLimitType = DurationLimitType.ArrivalToDeparture,
      fareTransferType = FareTransferType.FromLegPlusTransferPlusToLeg,
      fareProductId = "transfer_product",
    ),
  )

class FareTransferRuleTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<FareTransferRule>(csvContent)
    assertEquals(expected, decoded)
  }
}
