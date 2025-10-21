package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
trip_id,route_id,service_id,trip_headsign,direction_id,wheelchair_accessible,bikes_allowed
T1,R1,WD,Downtown,0,1,2
"""
    .trimIndent() + "\n"

private val expectedTrip =
  Trip(
    tripId = "T1",
    routeId = "R1",
    serviceId = "WD",
    tripHeadsign = "Downtown",
    directionId = DirectionId.Outbound,
    wheelchairAccessible = WheelchairAccessibility.Accessible,
    bikesAllowed = BikesAllowed.NotAllowed,
  )

class TripTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Trip>(csvContent)
    assertEquals(listOf(expectedTrip), decoded)
  }
}
