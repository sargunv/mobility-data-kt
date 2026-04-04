@file:Suppress("UnusedVariable", "unused")

package dev.sargunv.mobilitydata.gofs.v1

import kotlinx.coroutines.test.runTest

class DocsSnippet {
  fun example() = runTest {
    // --8<-- [start:example]
    GofsV1Client().use { gbfs -> // (1)!
      val manifest =
        gbfs
          .getSystemManifest( // (2)!
            discoveryUrl = "<discovery url>"
          )
          .data

      context(manifest.getService("en")) { // (3)!
        val systemInfo = gbfs.getSystemInformation().data // (4)!

        println("System: ${systemInfo.name}")
        println("Operator: ${systemInfo.operator}")
        println("Timezone: ${systemInfo.timezone}")

        val serviceBrands = gbfs.getServiceBrands().data // (5)!

        println("\nService Brands:")
        serviceBrands.forEach { brand -> println("  - ${brand.brandName} (ID: ${brand.brandId})") }

        val waitTimes = // (6)!
          gbfs.getWaitTimes(pickupLat = 41.8781, pickupLon = -87.6298).data

        println("\nWait Times:")
        waitTimes.forEach { waitTime ->
          println("  - Brand ${waitTime.brandId}: ${waitTime.waitTime} seconds")
        }
      }
    }
    // --8<-- [end:example]
  }
}
