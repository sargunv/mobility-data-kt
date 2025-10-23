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
    decodeCsvFile<ServiceCalendar>("amtrak/calendar.txt")
    decodeCsvFile<ServiceCalendarOverride>("amtrak/calendar_dates.txt")
    decodeCsvFile<FareAttribute>("amtrak/fare_attributes.txt")
    decodeCsvFile<FareRule>("amtrak/fare_rules.txt")
    decodeCsvFile<Route>("amtrak/routes.txt")
    decodeCsvFile<Shape>("amtrak/shapes.txt")
    decodeCsvFile<StopTime>("amtrak/stop_times.txt")
    decodeCsvFile<Stop>("amtrak/stops.txt")
  }

  @Test
  fun `puget-sound`() {
    decodeCsvFile<Agency>("puget-sound/agency.txt")
    decodeCsvFile<ServiceCalendar>("puget-sound/calendar.txt")
    decodeCsvFile<ServiceCalendarOverride>("puget-sound/calendar_dates.txt")
    decodeCsvFile<FareAttribute>("puget-sound/fare_attributes.txt")
    decodeCsvFile<FareRule>("puget-sound/fare_rules.txt")
    // decodeCsvFile<FeedInfo>("puget-sound/feed_info.txt")
    decodeCsvFile<Route>("puget-sound/routes.txt")
    decodeCsvFile<Shape>("puget-sound/shapes.txt")
    decodeCsvFile<StopTime>("puget-sound/stop_times.txt")
    decodeCsvFile<Stop>("puget-sound/stops.txt")
    decodeCsvFile<Transfer>("puget-sound/transfers.txt")
    decodeCsvFile<Trip>("puget-sound/trips.txt")
  }

  @Test
  fun mbta() {
    decodeCsvFile<Agency>("mbta/agency.txt")
    decodeCsvFile<Area>("mbta/areas.txt")
    decodeCsvFile<ServiceCalendar>("mbta/calendar.txt")
    // decodeCsvFile<CalendarAttribute>("mbta/calendar_attributes.txt")
    decodeCsvFile<ServiceCalendarOverride>("mbta/calendar_dates.txt")
    // decodeCsvFile<Checkpoint>("mbta/checkpoints.txt")
    // decodeCsvFile<Direction>("mbta/directions.txt")
    // decodeCsvFile<Facility>("mbta/facilities.txt")
    // decodeCsvFile<FacilityProperty>("mbta/facilities_properties.txt")
    // decodeCsvFile<FacilityPropertyDefinition>("mbta/facilities_properties_definitions.txt")
    decodeCsvFile<FareLegJoinRule>("mbta/fare_leg_join_rules.txt")
    decodeCsvFile<FareLegRule>("mbta/fare_leg_rules.txt")
    decodeCsvFile<FareMedia>("mbta/fare_media.txt")
    decodeCsvFile<FareProduct>("mbta/fare_products.txt")
    decodeCsvFile<FareTransferRule>("mbta/fare_transfer_rules.txt")
    // decodeCsvFile<FeedInfo>("mbta/feed_info.txt")
    // decodeCsvFile<Level>("mbta/levels.txt")
    // decodeCsvFile<Line>("mbta/lines.txt")
    // decodeCsvFile<LinkedDataset>("mbta/linked_datasets.txt")
    // decodeCsvFile<MultiRouteTrip>("mbta/multi_route_trips.txt")
    // decodeCsvFile<Pathway>("mbta/pathways.txt")
    // decodeCsvFile<RoutePattern>("mbta/route_patterns.txt")
    decodeCsvFile<Route>("mbta/routes.txt")
    decodeCsvFile<Shape>("mbta/shapes.txt")
    decodeCsvFile<StopArea>("mbta/stop_areas.txt")
    decodeCsvFile<StopTime>("mbta/stop_times.txt")
    decodeCsvFile<Stop>("mbta/stops.txt")
    decodeCsvFile<Timeframe>("mbta/timeframes.txt")
    decodeCsvFile<Transfer>("mbta/transfers.txt")
    decodeCsvFile<Trip>("mbta/trips.txt")
    // decodeCsvFile<TripProperty>("mbta/trips_properties.txt")
    // decodeCsvFile<TripPropertyDefinition>("mbta/trips_properties_definitions.txt")
  }
}

// TODO find datasets that publish:
// - frequencies.txt
// - networks.txt
// - rider_categories.txt
// - route_networks.txt
// - location_groups.txt
// - location_group_stops.txt
