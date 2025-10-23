package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class ProducerTest {

  private inline fun <reified T> decodeCsvFile(path: String) {
    val cwd = SystemFileSystem.resolve(Path("."))
    val projectDir =
      when (cwd.name) {
        "mobility-data-gtfs-schedule-test" -> cwd.parent!!.parent!!.parent!!.parent!!
        "gtfs-schedule" -> cwd.parent!!
        else -> error("unexpected: $cwd")
      }
    SystemFileSystem.source(Path(projectDir, "sample-data", "gtfs-schedule", path))
      .buffered()
      .use { source ->
        val count = GtfsCsv.decodeFromSource<T>(source).count()
        println("${T::class.simpleName}: decoded $count records")
      }
  }

  @Test
  fun amtrak() {
    decodeCsvFile<Agency>("amtrak/agency.txt")
    decodeCsvFile<Route>("amtrak/routes.txt")
    decodeCsvFile<Trip>("amtrak/trips.txt")
    decodeCsvFile<Stop>("amtrak/stops.txt")
    decodeCsvFile<StopTime>("amtrak/stop_times.txt")
    decodeCsvFile<ServiceCalendar>("amtrak/calendar.txt")
    decodeCsvFile<ServiceCalendarOverride>("amtrak/calendar_dates.txt")
    decodeCsvFile<FareAttribute>("amtrak/fare_attributes.txt")
    decodeCsvFile<FareRule>("amtrak/fare_rules.txt")
  }

  @Test
  fun `puget-sound`() {
    decodeCsvFile<Agency>("puget-sound/agency.txt")
    decodeCsvFile<Route>("puget-sound/routes.txt")
    decodeCsvFile<Trip>("puget-sound/trips.txt")
    decodeCsvFile<Stop>("puget-sound/stops.txt")
    decodeCsvFile<StopTime>("puget-sound/stop_times.txt")
    decodeCsvFile<ServiceCalendar>("puget-sound/calendar.txt")
    decodeCsvFile<ServiceCalendarOverride>("puget-sound/calendar_dates.txt")
    decodeCsvFile<FareAttribute>("puget-sound/fare_attributes.txt")
    decodeCsvFile<FareRule>("puget-sound/fare_rules.txt")
  }
}
