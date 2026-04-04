package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  location_group_id,stop_id
  group1,stop1
  group2,stop2
  """
    .trimIndent()

private val expected =
  listOf(
    LocationGroupStop(locationGroupId = "group1", stopId = "stop1"),
    LocationGroupStop(locationGroupId = "group2", stopId = "stop2"),
  )

class LocationGroupStopTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<LocationGroupStop>(csvContent)
    assertEquals(expected, decoded)
  }
}
