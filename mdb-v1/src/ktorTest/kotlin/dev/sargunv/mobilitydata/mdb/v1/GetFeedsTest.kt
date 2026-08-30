package dev.sargunv.mobilitydata.mdb.v1

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class GetFeedsTest {
  @Test
  fun getFeedsDecodesList() = runTest {
    val engine = MockEngine { request ->
      assertEquals("/v1/feeds?limit=2&status=active", request.url.fullPath)
      respond(
        """
        [
          {"id":"mdb-1","data_type":"gtfs","provider":"One"},
          {"id":"mdb-2","data_type":"gtfs_rt","provider":"Two"}
        ]
        """
          .trimIndent(),
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }

    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val feeds = client.getFeeds(FeedQuery(limit = 2, status = FeedStatus.Active)).getOrThrow()

    assertEquals(2, feeds.size)
    assertIs<Feed.Gtfs>(feeds[0])
    assertIs<Feed.GtfsRt>(feeds[1])
    client.close()
  }

  @Test
  fun getFeedUsesPathId() = runTest {
    val engine = MockEngine { request ->
      assertEquals("/v1/feeds/mdb-1210", request.url.encodedPath)
      respond(
        """{"id":"mdb-1210","data_type":"gtfs","provider":"LADOT"}""",
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }

    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val feed = client.getFeed(FeedId("mdb-1210")).getOrThrow()
    assertEquals(FeedId("mdb-1210"), feed.id)
    client.close()
  }

  @Test
  fun getFeedKeepsSlashInIdAsOneSegment() = runTest {
    val engine = MockEngine { request ->
      assertEquals("/v1/feeds/mdb-1%2Fextra", request.url.encodedPath)
      respond(
        """{"id":"mdb-1/extra","data_type":"gtfs","provider":"LADOT"}""",
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }

    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val feed = client.getFeed(FeedId("mdb-1/extra")).getOrThrow()
    assertEquals(FeedId("mdb-1/extra"), feed.id)
    client.close()
  }
}
