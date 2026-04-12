@file:Suppress("UnusedVariable", "unused")

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.coroutines.test.runTest

class DocsSnippet {

  fun example() = runTest {
    // --8<-- [start:example]
    GtfsRealtimeClient().use { gtfsRt -> // (1)!
      val feed =
        gtfsRt
          .getFeedMessage( // (2)!
            feedUrl = "https://gtfsr.tri-rail.com/download.aspx?file=trip_updates.pb"
          )
          .getOrThrow()

      val tripUpdates = feed.entity.count { it.tripUpdate != null } // (3)!
      val vehiclePositions = feed.entity.count { it.vehicle != null }
      val alerts = feed.entity.count { it.alert != null }

      println("Trip updates: $tripUpdates")
      println("Vehicle positions: $vehiclePositions")
      println("Alerts: $alerts")
    }
    // --8<-- [end:example]
  }
}
