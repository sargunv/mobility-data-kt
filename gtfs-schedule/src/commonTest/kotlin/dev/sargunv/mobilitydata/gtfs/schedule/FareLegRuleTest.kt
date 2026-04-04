package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  leg_group_id,network_id,from_area_id,to_area_id,from_timeframe_group_id,to_timeframe_group_id,fare_product_id,rule_priority
  local,,zone1,zone2,,,single_ride,
  express,express_network,,,peak,,express_fare,1
  """
    .trimIndent()

private val expected =
  listOf(
    FareLegRule(
      legGroupId = "local",
      networkId = null,
      fromAreaId = "zone1",
      toAreaId = "zone2",
      fromTimeframeGroupId = null,
      toTimeframeGroupId = null,
      fareProductId = "single_ride",
      rulePriority = null,
    ),
    FareLegRule(
      legGroupId = "express",
      networkId = "express_network",
      fromAreaId = null,
      toAreaId = null,
      fromTimeframeGroupId = "peak",
      toTimeframeGroupId = null,
      fareProductId = "express_fare",
      rulePriority = 1,
    ),
  )

class FareLegRuleTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<FareLegRule>(csvContent)
    assertEquals(expected, decoded)
  }
}
