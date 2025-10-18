@file:Suppress("UnusedVariable", "unused")

package dev.sargunv.mobilitydata.gbfs.v2.client

import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class DocsTest {
  @Test
  fun example() = runTest {
    GbfsV2Client().use { gbfs ->
      val manifest =
        gbfs
          .getManifest(
            discoveryUrl = "https://mds.bird.co/gbfs/v2/public/seattle-washington/gbfs.json"
          )
          .data

      context(manifest.getService("en")) {
        val systemInfo = gbfs.getSystemInformation().data

        println("System: ${systemInfo.name}")
        println("Operator: ${systemInfo.operator}")
        println("Timezone: ${systemInfo.timezone}")

        val vehicleTypes = gbfs.getVehicleTypes().data.vehicleTypes

        println("\nVehicle Types:")
        vehicleTypes.forEach { type -> println("  - ${type.formFactor}: (${type.propulsionType})") }

        val freeBikes = gbfs.getFreeBikeStatus().data.bikes

        println("\nAvailable bikes: ${freeBikes.size}")
        freeBikes.take(3).forEach { bike ->
          println("  - ${bike.bikeId} at (${bike.lat}, ${bike.lon})")
        }
      }
    }
  }
}
