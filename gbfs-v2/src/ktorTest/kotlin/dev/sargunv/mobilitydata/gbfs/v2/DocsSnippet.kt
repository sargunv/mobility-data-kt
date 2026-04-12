@file:Suppress("UnusedVariable", "unused")

package dev.sargunv.mobilitydata.gbfs.v2

import kotlinx.coroutines.test.runTest

class DocsSnippet {
  fun example() = runTest {
    // --8<-- [start:example]
    GbfsV2Client().use { gbfs -> // (1)!
      val manifest =
        gbfs
          .getSystemManifest( // (2)!
            discoveryUrl = "https://mds.bird.co/gbfs/v2/public/seattle-washington/gbfs.json"
          )
          .getOrThrow()
          .data

      context(manifest.getService("en")) { // (3)!
        val systemInfo = gbfs.getSystemInformation().getOrThrow().data // (4)!

        println("System: ${systemInfo.name}")
        println("Operator: ${systemInfo.operator}")
        println("Timezone: ${systemInfo.timezone}")

        val vehicleTypes = gbfs.getVehicleTypes().getOrThrow().data.vehicleTypes // (5)!

        println("\nVehicle Types:")
        vehicleTypes.forEach { type -> println("  - ${type.formFactor}: (${type.propulsionType})") }

        val freeBikes = gbfs.getFreeBikeStatus().getOrThrow().data.bikes // (6)!

        println("\nAvailable bikes: ${freeBikes.size}")
        freeBikes.take(3).forEach { bike ->
          println("  - ${bike.bikeId} at (${bike.lat}, ${bike.lon})")
        }
      }
    }
    // --8<-- [end:example]
  }
}
