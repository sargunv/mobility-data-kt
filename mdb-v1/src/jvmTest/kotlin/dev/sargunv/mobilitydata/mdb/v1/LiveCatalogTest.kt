package dev.sargunv.mobilitydata.mdb.v1

import io.ktor.client.engine.okhttp.OkHttp
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class LiveCatalogTest {
  @Test
  fun refreshTokenListsOneFeed() = runTest {
    val token = System.getenv("MOBILITY_DATABASE_REFRESH_TOKEN")
    if (token.isNullOrBlank()) {
      println("LiveCatalogTest skipped: MOBILITY_DATABASE_REFRESH_TOKEN is unset")
      return@runTest
    }

    MdbV1Client(OkHttp.create(), CatalogAuth.Refresh(token)).use { client ->
      val feeds = client.getFeeds(FeedQuery(limit = 1)).getOrThrow()
      assertTrue(feeds.isNotEmpty(), "catalog returned no feeds")
    }
  }
}
