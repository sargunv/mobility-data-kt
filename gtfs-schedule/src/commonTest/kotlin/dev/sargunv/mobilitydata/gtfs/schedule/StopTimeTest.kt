package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_time,shape_dist_traveled
  STBA,6:00:00,6:00:00,STAGECOACH,1,,,,
  STBA,6:20:00,6:20:00,BEATTY_AIRPORT,2,,,,
  CITY1,6:00:00,6:00:00,STAGECOACH,1,,,,
  CITY1,6:05:00,6:07:00,NANAA,2,,,,
  CITY1,6:12:00,6:14:00,NADAV,3,,,,
  CITY1,6:19:00,6:21:00,DADAN,4,,,,
  CITY1,6:26:00,6:28:00,EMSI,5,,,,
  CITY2,6:28:00,6:30:00,EMSI,1,,,,
  CITY2,6:35:00,6:37:00,DADAN,2,,,,
  CITY2,6:42:00,6:44:00,NADAV,3,,,,
  CITY2,6:49:00,6:51:00,NANAA,4,,,,
  CITY2,6:56:00,6:58:00,STAGECOACH,5,,,,
  AB1,8:00:00,8:00:00,BEATTY_AIRPORT,1,,,,
  AB1,8:10:00,8:15:00,BULLFROG,2,,,,
  AB2,12:05:00,12:05:00,BULLFROG,1,,,,
  AB2,12:15:00,12:15:00,BEATTY_AIRPORT,2,,,,
  BFC1,8:20:00,8:20:00,BULLFROG,1,,,,
  BFC1,9:20:00,9:20:00,FUR_CREEK_RES,2,,,,
  BFC2,11:00:00,11:00:00,FUR_CREEK_RES,1,,,,
  BFC2,12:00:00,12:00:00,BULLFROG,2,,,,
  AAMV1,8:00:00,8:00:00,BEATTY_AIRPORT,1,,,,
  AAMV1,9:00:00,9:00:00,AMV,2,,,,
  AAMV2,10:00:00,10:00:00,AMV,1,,,,
  AAMV2,11:00:00,11:00:00,BEATTY_AIRPORT,2,,,,
  AAMV3,13:00:00,13:00:00,BEATTY_AIRPORT,1,,,,
  AAMV3,14:00:00,14:00:00,AMV,2,,,,
  AAMV4,15:00:00,15:00:00,AMV,1,,,,
  AAMV4,16:00:00,16:00:00,BEATTY_AIRPORT,2,,,,
  """
    .trimIndent()

private val expected =
  listOf(
    StopTime(
      tripId = "STBA",
      arrivalTime = ServiceTime(6, 0, 0),
      departureTime = ServiceTime(6, 0, 0),
      stopId = "STAGECOACH",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "STBA",
      arrivalTime = ServiceTime(6, 20, 0),
      departureTime = ServiceTime(6, 20, 0),
      stopId = "BEATTY_AIRPORT",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "CITY1",
      arrivalTime = ServiceTime(6, 0, 0),
      departureTime = ServiceTime(6, 0, 0),
      stopId = "STAGECOACH",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "CITY1",
      arrivalTime = ServiceTime(6, 5, 0),
      departureTime = ServiceTime(6, 7, 0),
      stopId = "NANAA",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "CITY1",
      arrivalTime = ServiceTime(6, 12, 0),
      departureTime = ServiceTime(6, 14, 0),
      stopId = "NADAV",
      stopSequence = 3,
    ),
    StopTime(
      tripId = "CITY1",
      arrivalTime = ServiceTime(6, 19, 0),
      departureTime = ServiceTime(6, 21, 0),
      stopId = "DADAN",
      stopSequence = 4,
    ),
    StopTime(
      tripId = "CITY1",
      arrivalTime = ServiceTime(6, 26, 0),
      departureTime = ServiceTime(6, 28, 0),
      stopId = "EMSI",
      stopSequence = 5,
    ),
    StopTime(
      tripId = "CITY2",
      arrivalTime = ServiceTime(6, 28, 0),
      departureTime = ServiceTime(6, 30, 0),
      stopId = "EMSI",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "CITY2",
      arrivalTime = ServiceTime(6, 35, 0),
      departureTime = ServiceTime(6, 37, 0),
      stopId = "DADAN",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "CITY2",
      arrivalTime = ServiceTime(6, 42, 0),
      departureTime = ServiceTime(6, 44, 0),
      stopId = "NADAV",
      stopSequence = 3,
    ),
    StopTime(
      tripId = "CITY2",
      arrivalTime = ServiceTime(6, 49, 0),
      departureTime = ServiceTime(6, 51, 0),
      stopId = "NANAA",
      stopSequence = 4,
    ),
    StopTime(
      tripId = "CITY2",
      arrivalTime = ServiceTime(6, 56, 0),
      departureTime = ServiceTime(6, 58, 0),
      stopId = "STAGECOACH",
      stopSequence = 5,
    ),
    StopTime(
      tripId = "AB1",
      arrivalTime = ServiceTime(8, 0, 0),
      departureTime = ServiceTime(8, 0, 0),
      stopId = "BEATTY_AIRPORT",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "AB1",
      arrivalTime = ServiceTime(8, 10, 0),
      departureTime = ServiceTime(8, 15, 0),
      stopId = "BULLFROG",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "AB2",
      arrivalTime = ServiceTime(12, 5, 0),
      departureTime = ServiceTime(12, 5, 0),
      stopId = "BULLFROG",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "AB2",
      arrivalTime = ServiceTime(12, 15, 0),
      departureTime = ServiceTime(12, 15, 0),
      stopId = "BEATTY_AIRPORT",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "BFC1",
      arrivalTime = ServiceTime(8, 20, 0),
      departureTime = ServiceTime(8, 20, 0),
      stopId = "BULLFROG",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "BFC1",
      arrivalTime = ServiceTime(9, 20, 0),
      departureTime = ServiceTime(9, 20, 0),
      stopId = "FUR_CREEK_RES",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "BFC2",
      arrivalTime = ServiceTime(11, 0, 0),
      departureTime = ServiceTime(11, 0, 0),
      stopId = "FUR_CREEK_RES",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "BFC2",
      arrivalTime = ServiceTime(12, 0, 0),
      departureTime = ServiceTime(12, 0, 0),
      stopId = "BULLFROG",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "AAMV1",
      arrivalTime = ServiceTime(8, 0, 0),
      departureTime = ServiceTime(8, 0, 0),
      stopId = "BEATTY_AIRPORT",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "AAMV1",
      arrivalTime = ServiceTime(9, 0, 0),
      departureTime = ServiceTime(9, 0, 0),
      stopId = "AMV",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "AAMV2",
      arrivalTime = ServiceTime(10, 0, 0),
      departureTime = ServiceTime(10, 0, 0),
      stopId = "AMV",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "AAMV2",
      arrivalTime = ServiceTime(11, 0, 0),
      departureTime = ServiceTime(11, 0, 0),
      stopId = "BEATTY_AIRPORT",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "AAMV3",
      arrivalTime = ServiceTime(13, 0, 0),
      departureTime = ServiceTime(13, 0, 0),
      stopId = "BEATTY_AIRPORT",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "AAMV3",
      arrivalTime = ServiceTime(14, 0, 0),
      departureTime = ServiceTime(14, 0, 0),
      stopId = "AMV",
      stopSequence = 2,
    ),
    StopTime(
      tripId = "AAMV4",
      arrivalTime = ServiceTime(15, 0, 0),
      departureTime = ServiceTime(15, 0, 0),
      stopId = "AMV",
      stopSequence = 1,
    ),
    StopTime(
      tripId = "AAMV4",
      arrivalTime = ServiceTime(16, 0, 0),
      departureTime = ServiceTime(16, 0, 0),
      stopId = "BEATTY_AIRPORT",
      stopSequence = 2,
    ),
  )

class StopTimeTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<StopTime>(csvContent)
    assertEquals(expected, decoded)
  }
}
