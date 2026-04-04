@file:Suppress("UnusedVariable", "unused")

package dev.sargunv.mobilitydata.gtfs.schedule

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class DocsSnippet {

  fun example() {
    // --8<-- [start:example]
    SystemFileSystem.source(Path("path/to/gtfs/agency.txt")).buffered().use { source -> // (1)!
      val agencies = GtfsCsv.decodeFromSource<Agency>(source) // (2)!
      agencies.forEach { agency -> // (3)!
        println("Agency: ${agency.agencyName} (${agency.agencyId})")
      }
    }
    // --8<-- [end:example]
  }
}
