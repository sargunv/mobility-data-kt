package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
trip_id,arrival_time,departure_time,stop_id,stop_sequence,pickup_type,drop_off_type
T1,08:00:00,08:00:00,S1,1,0,0
"""
    .trimIndent() + "\n"

private val expectedStopTime =
  StopTime(
    tripId = "T1",
    arrivalTime = ServiceTime(8, 0, 0),
    departureTime = ServiceTime(8, 0, 0),
    stopId = "S1",
    stopSequence = 1,
    pickupType = PickupDropOffType.REGULAR,
    dropOffType = PickupDropOffType.REGULAR,
  )

class StopTimeTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<StopTime>(csvContent)
    assertEquals(listOf(expectedStopTime), decoded)
  }
}
