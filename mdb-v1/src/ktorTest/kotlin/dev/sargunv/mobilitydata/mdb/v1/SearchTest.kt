package dev.sargunv.mobilitydata.mdb.v1

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class SearchTest {
  @Test
  fun searchFeedsDecodesEnvelope() = runTest {
    val engine = MockEngine { request ->
      assertEquals("/v1/search?search_query=dash&limit=1", request.url.fullPath)
      respond(
        """
        {
          "total": 1,
          "results": [
            {"id":"mdb-1210","data_type":"gtfs","status":"active","provider":"LADOT"}
          ]
        }
        """
          .trimIndent(),
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }

    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val page = client.searchFeeds(SearchFeedsQuery(searchQuery = "dash", limit = 1)).getOrThrow()
    assertEquals(1, page.total)
    assertEquals(FeedId("mdb-1210"), page.results?.single()?.id)
    client.close()
  }

  @Test
  fun getLocationsSendsSearchQuery() = runTest {
    val engine = MockEngine { request ->
      assertEquals("/v1/locations?search_query=montreal", request.url.fullPath)
      respond(
        """{"total":1,"results":[{"location_id":1,"name":"Montréal","country_code":"CA"}]}""",
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }
    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val page = client.getLocations(LocationQuery(searchQuery = "montreal")).getOrThrow()
    assertEquals("CA", page.results?.single()?.countryCode)
    client.close()
  }
}
