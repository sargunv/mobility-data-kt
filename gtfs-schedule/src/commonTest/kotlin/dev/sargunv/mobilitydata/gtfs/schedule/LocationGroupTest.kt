package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  location_group_id,location_group_name
  group1,Zone A Pickup
  group2,
  """
    .trimIndent()

private val expected =
  listOf(
    LocationGroup(locationGroupId = "group1", locationGroupName = "Zone A Pickup"),
    LocationGroup(locationGroupId = "group2", locationGroupName = null),
  )

class LocationGroupTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<LocationGroup>(csvContent)
    assertEquals(expected, decoded)
  }
}
