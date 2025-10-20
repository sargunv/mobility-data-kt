@file:Suppress("UnusedVariable", "unused")

package dev.sargunv.mobilitydata.gbfs.v1

import kotlinx.coroutines.test.runTest

class DocsSnippet {
  fun example() = runTest {
    // --8<-- [start:example]
    GbfsV1Client().use { gbfs -> // (1)!
      val manifest =
        gbfs
          .getManifest( // (2)!
            discoveryUrl = "https://mds.bird.co/gbfs/v2/public/seattle-washington/gbfs.json"
          )
          .data

      context(manifest.getService("en")) { // (3)!
        val systemInfo = gbfs.getSystemInformation().data // (4)!

        println("System: ${systemInfo.name}")
        println("Operator: ${systemInfo.operator}")
        println("Timezone: ${systemInfo.timezone}")

        val stations = gbfs.getStationInformation().data // (5)!

        println("\nStations: ${stations.size}")
        stations.take(3).forEach { station ->
          println("  - ${station.name} (${station.capacity} bikes)")
        }

        val freeBikes = gbfs.getFreeBikeStatus().data.bikes // (6)!

        println("\nAvailable bikes: ${freeBikes.size}")
        freeBikes.take(3).forEach { bike ->
          println("  - ${bike.bikeId} at (${bike.lat}, ${bike.lon})")
        }
      }
    }
    // --8<-- [end:example]
  }
}
