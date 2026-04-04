package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  fare_id,route_id,origin_id,destination_id,contains_id
  p,AB,,,
  p,STBA,,,
  p,BFC,,,
  a,AAMV,,,
  """
    .trimIndent()

private val expected =
  listOf(
    FareRule(fareId = "p", routeId = "AB"),
    FareRule(fareId = "p", routeId = "STBA"),
    FareRule(fareId = "p", routeId = "BFC"),
    FareRule(fareId = "a", routeId = "AAMV"),
  )

class FareRuleTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<FareRule>(csvContent)
    assertEquals(expected, decoded)
  }
}
