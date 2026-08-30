@file:Suppress("UnusedVariable", "unused")

package dev.sargunv.mobilitydata.mdb.v1

import io.ktor.client.engine.mock.MockEngine
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class DocsSnippet {
  fun example() = runTest {
    // --8<-- [start:example]
    MdbV1Client(auth = CatalogAuth.Refresh("<refresh token>")).use { mdb -> // (1)!
      val feeds = mdb.getFeeds(FeedQuery(limit = 10)).getOrThrow() // (2)!

      feeds.forEach { feed ->
        when (feed) { // (3)!
          is Feed.Gtfs -> println("GTFS ${feed.id} ${feed.provider}")
          is Feed.GtfsRt -> println("GTFS-RT ${feed.id} ${feed.provider}")
          is Feed.Gbfs -> println("GBFS ${feed.id} ${feed.provider}")
          is Feed.Unknown -> println("${feed.dataType} ${feed.id} ${feed.provider}")
        }
      }
    }
    // --8<-- [end:example]
  }

  @Test
  fun constructsClient() {
    MdbV1Client(MockEngine { error("unused") }, CatalogAuth.Access("unused")).close()
  }
}
