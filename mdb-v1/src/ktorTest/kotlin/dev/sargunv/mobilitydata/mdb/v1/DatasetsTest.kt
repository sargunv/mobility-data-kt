package dev.sargunv.mobilitydata.mdb.v1

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.encodedPath
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest

class DatasetsTest {
  @Test
  fun getGtfsFeedDatasetsUsesPath() = runTest {
    val engine = MockEngine { request ->
      assertEquals("/v1/gtfs_feeds/mdb-10/datasets", request.url.encodedPath)
      respond(
        """[{"id":"mdb-10-202402080058","feed_id":"mdb-10","downloaded_at":"2023-07-10T22:06:00Z"}]""",
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }
    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val datasets = client.getGtfsFeedDatasets(FeedId("mdb-10")).getOrThrow()
    assertEquals(Instant.parse("2023-07-10T22:06:00Z"), datasets.single().downloadedAt)
    client.close()
  }

  @Test
  fun getDatasetGtfsUsesPath() = runTest {
    val engine = MockEngine { request ->
      assertEquals("/v1/datasets/gtfs/mdb-10-202402080058", request.url.encodedPath)
      respond(
        """{"id":"mdb-10-202402080058","feed_id":"mdb-10"}""",
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }
    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val dataset = client.getDatasetGtfs("mdb-10-202402080058").getOrThrow()
    assertEquals("mdb-10-202402080058", dataset.id)
    client.close()
  }

  @Test
  fun getMetadataDecodes() = runTest {
    val engine = MockEngine { request ->
      assertEquals("/v1/metadata", request.url.fullPath)
      respond(
        """{"version":"1.0.0","commit_hash":"abc123"}""",
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }
    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val metadata = client.getMetadata().getOrThrow()
    assertEquals("1.0.0", metadata.version)
    client.close()
  }

  @Test
  fun getLicensesAndGetLicense() = runTest {
    val paths = mutableListOf<String>()
    val engine = MockEngine { request ->
      paths += request.url.encodedPath
      when (request.url.encodedPath) {
        "/v1/licenses" ->
          respond(
            """[{"id":"0BSD","name":"BSD Zero Clause License"}]""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
        else ->
          respond(
            """{"id":"0BSD","name":"BSD Zero Clause License","license_rules":[{"name":"commercial-use","type":"permission"}]}""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
      }
    }
    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val licenses = client.getLicenses().getOrThrow()
    val license = client.getLicense("0BSD").getOrThrow()
    assertEquals(listOf("/v1/licenses", "/v1/licenses/0BSD"), paths)
    assertEquals("0BSD", licenses.single().id)
    assertEquals(1, license.licenseRules?.size)
    client.close()
  }

  @Test
  fun getMatchingLicensesPostsUrl() = runTest {
    var method = ""
    var path = ""
    var body = ""
    val engine = MockEngine { request ->
      method = request.method.value
      path = request.url.encodedPath
      body = (request.body as TextContent).text
      respond(
        """[{"license_id":"CC-BY-4.0","match_type":"heuristic","confidence":0.99}]""",
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }
    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    val matches =
      client
        .getMatchingLicenses(
          LicenseMatchRequest("https://creativecommons.org/licenses/by/4.0/deed.nl")
        )
        .getOrThrow()
    assertEquals("POST", method)
    assertEquals("/v1/licenses:match", path)
    assertEquals("""{"license_url":"https://creativecommons.org/licenses/by/4.0/deed.nl"}""", body)
    assertEquals("CC-BY-4.0", matches.single().licenseId)
    assertEquals(LicenseMatchType.Heuristic, matches.single().matchType)
    client.close()
  }

  @Test
  fun feedQueryOffsetPages() = runTest {
    val offsets = mutableListOf<String>()
    val engine = MockEngine { request ->
      offsets += request.url.parameters["offset"].orEmpty()
      respond("[]", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
    }
    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    client.getFeeds(FeedQuery(limit = 10, offset = 0)).getOrThrow()
    client.getFeeds(FeedQuery(limit = 10, offset = 10)).getOrThrow()
    assertEquals(listOf("0", "10"), offsets)
    client.close()
  }
}
