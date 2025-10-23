package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  area_id,area_name
  zone1,Zone 1
  zone2,
  """
    .trimIndent()

private val expected =
  listOf(Area(areaId = "zone1", areaName = "Zone 1"), Area(areaId = "zone2", areaName = null))

class AreaTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Area>(csvContent)
    assertEquals(expected, decoded)
  }
}
