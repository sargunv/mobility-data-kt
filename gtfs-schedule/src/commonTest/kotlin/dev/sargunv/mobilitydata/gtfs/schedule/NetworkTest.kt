package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  network_id,network_name
  metro,Metro Network
  bus,
  """
    .trimIndent()

private val expected =
  listOf(
    Network(networkId = "metro", networkName = "Metro Network"),
    Network(networkId = "bus", networkName = null),
  )

class NetworkTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Network>(csvContent)
    assertEquals(expected, decoded)
  }
}
