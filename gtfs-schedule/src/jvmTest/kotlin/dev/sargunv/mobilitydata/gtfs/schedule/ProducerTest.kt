package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class ProducerTest {
  private fun getProjectDir(): Path {
    val cwd = SystemFileSystem.resolve(Path("."))
    return when (cwd.name) {
      "mobility-data-gtfs-schedule-test" -> cwd.parent!!.parent!!.parent!!.parent!!
      "gtfs-schedule" -> cwd.parent!!
      else -> error("unexpected: $cwd")
    }
  }

  private fun openCsvFile(fileName: String) =
    SystemFileSystem.source(
        Path("${getProjectDir()}", "sample-data", "gtfs-schedule", "sound-transit", fileName)
      )
      .buffered()

  @Test
  fun testAgency() {
    openCsvFile("agency.txt").use { source ->
      val agencies = GtfsCsv.decodeFromSource<Agency>(source)
      // Consume the sequence to verify decoding works
      agencies.forEach {}
    }
  }

  @Test
  fun testRoutes() {
    openCsvFile("routes.txt").use { source ->
      val routes = GtfsCsv.decodeFromSource<Route>(source)
      routes.forEach {}
    }
  }

  @Test
  fun testTrips() {
    openCsvFile("trips.txt").use { source ->
      val trips = GtfsCsv.decodeFromSource<Trip>(source)
      trips.forEach {}
    }
  }

  @Test
  fun testStops() {
    openCsvFile("stops.txt").use { source ->
      val stops = GtfsCsv.decodeFromSource<Stop>(source)
      stops.forEach {}
    }
  }

  @Test
  fun testStopTimes() {
    openCsvFile("stop_times.txt").use { source ->
      val stopTimes = GtfsCsv.decodeFromSource<StopTime>(source)
      stopTimes.forEach {}
    }
  }

  @Test
  fun testCalendar() {
    openCsvFile("calendar.txt").use { source ->
      val calendars = GtfsCsv.decodeFromSource<ServiceCalendar>(source)
      calendars.forEach {}
    }
  }

  @Test
  fun testCalendarDates() {
    openCsvFile("calendar_dates.txt").use { source ->
      val calendarDates = GtfsCsv.decodeFromSource<CalendarDate>(source)
      calendarDates.forEach {}
    }
  }
}
