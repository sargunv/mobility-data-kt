package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString

class ProducerTest {
  private fun getProjectDir(): Path {
    val cwd = SystemFileSystem.resolve(Path("."))
    return when (cwd.name) {
      "mobility-data-gtfs-schedule-test" -> cwd.parent!!.parent!!.parent!!.parent!!
      "gtfs-schedule" -> cwd.parent!!
      else -> error("unexpected: $cwd")
    }
  }

  private fun readCsvFile(fileName: String): String {
    val projectDir = getProjectDir()
    val localPath = Path("$projectDir", "sample-data", "gtfs-schedule", "sample", fileName)
    val source = SystemFileSystem.source(localPath)
    return source.buffered().use { it.readString() }
  }

  @Test
  fun testAgency() {
    val content = readCsvFile("agency.txt")
    val agencies = GtfsCsv.decodeFromString<Agency>(content)
    assertEquals(1, agencies.size)
    assertEquals("DTA", agencies[0].agencyId)
    assertEquals("Demo Transit Authority", agencies[0].agencyName)
  }

  @Test
  fun testRoutes() {
    val content = readCsvFile("routes.txt")
    val routes = GtfsCsv.decodeFromString<Route>(content)
    assertEquals(2, routes.size)
    assertEquals("R1", routes[0].routeId)
    assertEquals("17", routes[0].routeShortName)
  }

  @Test
  fun testTrips() {
    val content = readCsvFile("trips.txt")
    val trips = GtfsCsv.decodeFromString<Trip>(content)
    assertEquals(2, trips.size)
    assertEquals("T1", trips[0].tripId)
    assertEquals("R1", trips[0].routeId)
  }

  @Test
  fun testStops() {
    val content = readCsvFile("stops.txt")
    val stops = GtfsCsv.decodeFromString<Stop>(content)
    assertEquals(3, stops.size)
    assertEquals("S1", stops[0].stopId)
    assertEquals("Main St Station", stops[0].stopName)
  }

  @Test
  fun testStopTimes() {
    val content = readCsvFile("stop_times.txt")
    val stopTimes = GtfsCsv.decodeFromString<StopTime>(content)
    assertEquals(3, stopTimes.size)
    assertEquals("T1", stopTimes[0].tripId)
    assertEquals("S1", stopTimes[0].stopId)
  }

  @Test
  fun testCalendar() {
    val content = readCsvFile("calendar.txt")
    val calendars = GtfsCsv.decodeFromString<ServiceCalendar>(content)
    assertEquals(2, calendars.size)
    assertEquals("WD", calendars[0].serviceId)
    assertEquals(true, calendars[0].monday)
  }

  @Test
  fun testCalendarDates() {
    val content = readCsvFile("calendar_dates.txt")
    val calendarDates = GtfsCsv.decodeFromString<CalendarDate>(content)
    assertEquals(2, calendarDates.size)
    assertEquals("WD", calendarDates[0].serviceId)
    assertEquals(ExceptionType.SERVICE_REMOVED, calendarDates[0].exceptionType)
  }
}
