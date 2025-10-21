package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
stop_id,stop_name,stop_lat,stop_lon,location_type,wheelchair_boarding
S1,Main St Station,37.7749,-122.4194,0,1
"""
    .trimIndent() + "\n"

private val expectedStop =
  Stop(
    stopId = "S1",
    stopName = "Main St Station",
    stopLat = 37.7749,
    stopLon = -122.4194,
    locationType = LocationType.STOP,
    wheelchairBoarding = WheelchairBoarding.ACCESSIBLE,
  )

class StopTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Stop>(csvContent)
    assertEquals(listOf(expectedStop), decoded)
  }
}
