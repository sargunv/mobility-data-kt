package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

private val csvContent = // language=CSV
  """
  trip_id,start_time,end_time,headway_secs,exact_times
  STBA,6:00:00,22:00:00,1800,
  CITY1,6:00:00,7:59:59,300,0
  CITY2,8:00:00,9:59:59,300,0
  CITY2,10:00:00,15:59:59,600,0
  CITY2,16:00:00,18:59:59,300,0
  CITY2,19:00:00,22:00:00,1800,0
  AB1,7:00:00,12:00:00,900,1
  AB2,13:00:00,22:00:00,1200,1
  """
    .trimIndent()

private val expected =
  listOf(
    Frequency(
      tripId = "STBA",
      startTime = ServiceTime(6, 0, 0),
      endTime = ServiceTime(22, 0, 0),
      headwaySecs = 1800.seconds,
      exactTimes = null,
    ),
    Frequency(
      tripId = "CITY1",
      startTime = ServiceTime(6, 0, 0),
      endTime = ServiceTime(7, 59, 59),
      headwaySecs = 300.seconds,
      exactTimes = false,
    ),
    Frequency(
      tripId = "CITY2",
      startTime = ServiceTime(8, 0, 0),
      endTime = ServiceTime(9, 59, 59),
      headwaySecs = 300.seconds,
      exactTimes = false,
    ),
    Frequency(
      tripId = "CITY2",
      startTime = ServiceTime(10, 0, 0),
      endTime = ServiceTime(15, 59, 59),
      headwaySecs = 600.seconds,
      exactTimes = false,
    ),
    Frequency(
      tripId = "CITY2",
      startTime = ServiceTime(16, 0, 0),
      endTime = ServiceTime(18, 59, 59),
      headwaySecs = 300.seconds,
      exactTimes = false,
    ),
    Frequency(
      tripId = "CITY2",
      startTime = ServiceTime(19, 0, 0),
      endTime = ServiceTime(22, 0, 0),
      headwaySecs = 1800.seconds,
      exactTimes = false,
    ),
    Frequency(
      tripId = "AB1",
      startTime = ServiceTime(7, 0, 0),
      endTime = ServiceTime(12, 0, 0),
      headwaySecs = 900.seconds,
      exactTimes = true,
    ),
    Frequency(
      tripId = "AB2",
      startTime = ServiceTime(13, 0, 0),
      endTime = ServiceTime(22, 0, 0),
      headwaySecs = 1200.seconds,
      exactTimes = true,
    ),
  )

class FrequencyTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Frequency>(csvContent)
    assertEquals(expected, decoded)
  }
}
