package dev.sargunv.mobilitydata.mdb.v1

import dev.sargunv.mobilitydata.utils.ExperimentalMobilityDataApi
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodedPath
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalMobilityDataApi::class)
class BetaEndpointsTest {
  @Test
  fun availabilityAndReliabilityRequireOptIn() = runTest {
    val paths = mutableListOf<String>()
    val engine = MockEngine { request ->
      paths += request.url.encodedPath
      when {
        request.url.encodedPath.endsWith("/availability") ->
          respond(
            """{"feed_id":"mdb-1210","total":0,"offset":0,"limit":100,"checks":[]}""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
        else ->
          respond(
            """{"feed_id":"mdb-1210","has_seal":false,"on_probation":false,"criteria":[]}""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
      }
    }
    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val availability = client.getGtfsFeedAvailability(FeedId("mdb-1210")).getOrThrow()
    val reliability = client.getGtfsFeedReliability(FeedId("mdb-1210")).getOrThrow()
    assertEquals(
      listOf("/v1/gtfs_feeds/mdb-1210/availability", "/v1/gtfs_feeds/mdb-1210/reliability"),
      paths,
    )
    assertEquals(0, availability.total)
    assertEquals(false, reliability.hasSeal)
    client.close()
  }
}
