package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  area_id,stop_id
  zone1,stop1
  zone2,stop2
  """
    .trimIndent()

private val expected =
  listOf(StopArea(areaId = "zone1", stopId = "stop1"), StopArea(areaId = "zone2", stopId = "stop2"))

class StopAreaTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<StopArea>(csvContent)
    assertEquals(expected, decoded)
  }
}
