package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.RgbColor
import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
route_id,agency_id,route_short_name,route_long_name,route_type,route_color,route_text_color
R1,DTA,17,Mission,3,FF0000,FFFFFF
"""
    .trimIndent() + "\n"

private val expectedRoute =
  Route(
    routeId = "R1",
    agencyId = "DTA",
    routeShortName = "17",
    routeLongName = "Mission",
    routeType = RouteType.BUS,
    routeColor = RgbColor(0xFF0000),
    routeTextColor = RgbColor(0xFFFFFF),
  )

class RouteTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Route>(csvContent)
    assertEquals(listOf(expectedRoute), decoded)
  }
}
