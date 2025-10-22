package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  network_id,route_id
  metro,route1
  bus,route2
  """
    .trimIndent()

private val expected =
  listOf(
    RouteNetwork(networkId = "metro", routeId = "route1"),
    RouteNetwork(networkId = "bus", routeId = "route2"),
  )

class RouteNetworkTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<RouteNetwork>(csvContent)
    assertEquals(expected, decoded)
  }
}
