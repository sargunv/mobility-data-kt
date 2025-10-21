package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
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
    val localPath = Path("$projectDir", "sample-data", "gtfs-schedule", "sound-transit", fileName)
    val source = SystemFileSystem.source(localPath)
    return source.buffered().use { it.readString() }
  }

  @Test
  fun testAgency() {
    val content = readCsvFile("agency.txt")
    GtfsCsv.decodeFromString<Agency>(content)
  }

  @Test
  fun testRoutes() {
    val content = readCsvFile("routes.txt")
    GtfsCsv.decodeFromString<Route>(content)
  }

  @Test
  fun testStops() {
    val content = readCsvFile("stops.txt")
    GtfsCsv.decodeFromString<Stop>(content)
  }

  @Test
  fun testCalendar() {
    val content = readCsvFile("calendar.txt")
    GtfsCsv.decodeFromString<ServiceCalendar>(content)
  }

  @Test
  fun testCalendarDates() {
    val content = readCsvFile("calendar_dates.txt")
    GtfsCsv.decodeFromString<CalendarDate>(content)
  }
}
