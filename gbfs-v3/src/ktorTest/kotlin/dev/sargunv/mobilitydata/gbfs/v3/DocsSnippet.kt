@file:Suppress("UnusedVariable", "unused")

package dev.sargunv.mobilitydata.gbfs.v3

import kotlinx.coroutines.test.runTest

class DocsSnippet {

  fun example() = runTest {
    // --8<-- [start:example]
    GbfsV3Client().use { gbfs -> // (1)!
      val service =
        gbfs
          .getServiceManifest( // (2)!
            discoveryUrl = "https://locomotion.app/api/gbfs/3.0/montreal/gbfs"
          )
          .data

      context(service) { // (3)!
        val systemInfo = gbfs.getSystemInformation().data // (4)!

        println("System: ${systemInfo.name["en-CA"]}")
        println("Operator: ${systemInfo.operator?.get("en-CA")}")
        println("Timezone: ${systemInfo.timezone}")

        val vehicleTypes = gbfs.getVehicleTypes().data // (5)!

        println("\nVehicle Types:")
        vehicleTypes.forEach { type -> println("  - ${type.formFactor}: (${type.propulsionType})") }

        val freeBikes = gbfs.getVehicleStatus().data // (6)!

        println("\nAvailable bikes: ${freeBikes.size}")
        freeBikes.take(3).forEach { bike ->
          println("  - ${bike.vehicleId} at (${bike.lat}, ${bike.lon})")
        }
      }
    }
    // --8<-- [end:example]
  }
}
