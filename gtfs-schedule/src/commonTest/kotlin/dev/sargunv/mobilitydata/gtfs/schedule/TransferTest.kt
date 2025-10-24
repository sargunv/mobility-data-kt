package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

private val csvContent = // language=CSV
  """
  from_stop_id,to_stop_id,from_route_id,to_route_id,from_trip_id,to_trip_id,transfer_type,min_transfer_time
  STOP1,STOP2,,,,,0,
  STOP3,STOP4,,,,,2,180
  STOP5,STOP6,ROUTE1,ROUTE2,,,1,
  STOP7,STOP8,,,TRIP1,TRIP2,4,
  """
    .trimIndent()

private val expected =
  listOf(
    Transfer(fromStopId = "STOP1", toStopId = "STOP2", transferType = TransferType.Recommended),
    Transfer(
      fromStopId = "STOP3",
      toStopId = "STOP4",
      transferType = TransferType.MinimumTime,
      minTransferTime = 180.seconds,
    ),
    Transfer(
      fromStopId = "STOP5",
      toStopId = "STOP6",
      fromRouteId = "ROUTE1",
      toRouteId = "ROUTE2",
      transferType = TransferType.Timed,
    ),
    Transfer(
      fromStopId = "STOP7",
      toStopId = "STOP8",
      fromTripId = "TRIP1",
      toTripId = "TRIP2",
      transferType = TransferType.InSeat,
    ),
  )

class TransferTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Transfer>(csvContent)
    assertEquals(expected, decoded)
  }
}
