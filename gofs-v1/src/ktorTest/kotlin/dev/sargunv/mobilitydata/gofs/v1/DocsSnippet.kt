@file:Suppress("UnusedVariable", "unused")

package dev.sargunv.mobilitydata.gofs.v1

import kotlinx.coroutines.test.runTest

class DocsSnippet {
  fun example() = runTest {
    // --8<-- [start:example]
    GofsV1Client().use { gofs -> // (1)!
      val manifest =
        gofs
          .getSystemManifest( // (2)!
            discoveryUrl = "<discovery url>"
          )
          .getOrThrow()
          .data

      context(manifest.getService("en")) { // (3)!
        val systemInfo = gofs.getSystemInformation().getOrThrow().data // (4)!

        println("System: ${systemInfo.name}")
        println("Operator: ${systemInfo.operator}")
        println("Timezone: ${systemInfo.timezone}")

        val serviceBrands = gofs.getServiceBrands().getOrThrow().data // (5)!

        println("\nService Brands:")
        serviceBrands.forEach { brand -> println("  - ${brand.brandName} (ID: ${brand.brandId})") }

        val waitTimes = // (6)!
          gofs.getWaitTimes(pickupLat = 41.8781, pickupLon = -87.6298).getOrThrow().data

        println("\nWait Times:")
        waitTimes.forEach { waitTime ->
          println("  - Brand ${waitTime.brandId}: ${waitTime.waitTime} seconds")
        }
      }
    }
    // --8<-- [end:example]
  }
}
