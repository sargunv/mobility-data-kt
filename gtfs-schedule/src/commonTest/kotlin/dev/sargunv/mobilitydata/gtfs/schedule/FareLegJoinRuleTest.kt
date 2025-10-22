package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  from_network_id,to_network_id,from_stop_id,to_stop_id
  rail,bus,,
  metro,metro,station_a,station_b
  """
    .trimIndent()

private val expected =
  listOf(
    FareLegJoinRule(
      fromNetworkId = "rail",
      toNetworkId = "bus",
      fromStopId = null,
      toStopId = null,
    ),
    FareLegJoinRule(
      fromNetworkId = "metro",
      toNetworkId = "metro",
      fromStopId = "station_a",
      toStopId = "station_b",
    ),
  )

class FareLegJoinRuleTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<FareLegJoinRule>(csvContent)
    assertEquals(expected, decoded)
  }
}
