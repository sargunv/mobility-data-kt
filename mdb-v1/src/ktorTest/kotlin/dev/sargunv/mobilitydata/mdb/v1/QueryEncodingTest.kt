package dev.sargunv.mobilitydata.mdb.v1

import dev.sargunv.mobilitydata.utils.ExperimentalMobilityDataApi
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalMobilityDataApi::class)
class QueryEncodingTest {
  @Test
  fun gtfsFeedQuerySendsBboxAndOfficial() = runTest {
    val seen = mutableMapOf<String, String>()
    val client = clientRecording(seen)
    client
      .getGtfsFeeds(
        GtfsFeedQuery(
          limit = 5,
          isOfficial = true,
          datasetBbox =
            DatasetBboxFilter(
              minimumLatitude = 33.5,
              maximumLatitude = 34.5,
              minimumLongitude = -119.0,
              maximumLongitude = -118.0,
            ),
          boundingFilterMethod = BoundingFilterMethod.PartiallyEnclosed,
        )
      )
      .getOrThrow()
    assertEquals("5", seen["limit"])
    assertEquals("true", seen["is_official"])
    // JS Double.toString() drops a trailing .0; compare the numbers, not the wire text.
    assertEquals(listOf(33.5, 34.5), csvDoubles(seen["dataset_latitudes"]))
    assertEquals(listOf(-119.0, -118.0), csvDoubles(seen["dataset_longitudes"]))
    assertEquals("partially_enclosed", seen["bounding_filter_method"])
    assertNull(seen["entity_types"])
    assertNull(seen["system_id"])
    client.close()
  }

  @Test
  fun gtfsRtFeedQuerySendsEntityTypes() = runTest {
    val seen = mutableMapOf<String, String>()
    val client = clientRecording(seen)
    client
      .getGtfsRtFeeds(
        GtfsRtFeedQuery(
          entityTypes = listOf(RealtimeEntityType.VehiclePositions, RealtimeEntityType.TripUpdates),
          isOfficial = true,
        )
      )
      .getOrThrow()
    assertEquals("vp,tu", seen["entity_types"])
    assertEquals("true", seen["is_official"])
    assertNull(seen["system_id"])
    assertNull(seen["dataset_latitudes"])
    client.close()
  }

  @Test
  fun gbfsFeedQuerySendsSystemAndVersion() = runTest {
    val seen = mutableMapOf<String, String>()
    val client = clientRecording(seen)
    client.getGbfsFeeds(GbfsFeedQuery(systemId = "system-1", version = "3.0")).getOrThrow()
    assertEquals("system-1", seen["system_id"])
    assertEquals("3.0", seen["version"])
    assertNull(seen["is_official"])
    assertNull(seen["entity_types"])
    client.close()
  }

  @Test
  fun searchFeedsQuerySendsCatalogFilters() = runTest {
    val seen = mutableMapOf<String, String>()
    val client = clientRecording(seen, """{"total":0,"results":[]}""")
    client
      .searchFeeds(
        SearchFeedsQuery(
          searchQuery = "dash",
          dataType = listOf(FeedDataType.Gtfs, FeedDataType.GtfsRt),
          hasSeal = true,
          version = listOf("2.3", "3.0"),
          feature = listOf("fares"),
          licenseIds = listOf("0BSD", "MIT"),
          licenseIsSpdx = true,
          licenseTags = listOf("family:ODC"),
        )
      )
      .getOrThrow()
    assertEquals("dash", seen["search_query"])
    assertEquals("gtfs,gtfs_rt", seen["data_type"])
    assertEquals("true", seen["has_seal"])
    assertEquals("2.3,3.0", seen["version"])
    assertEquals("fares", seen["feature"])
    assertEquals("0BSD,MIT", seen["license_ids"])
    assertEquals("true", seen["license_is_spdx"])
    assertEquals("family:ODC", seen["license_tags"])
    client.close()
  }

  @Test
  fun datasetQuerySendsDownloadWindow() = runTest {
    val seen = mutableMapOf<String, String>()
    val client = clientRecording(seen)
    val after = Instant.parse("2023-07-10T22:06:00Z")
    val before = Instant.parse("2023-07-20T22:06:00Z")
    client
      .getGtfsFeedDatasets(
        FeedId("mdb-10"),
        DatasetQuery(latest = true, downloadedAfter = after, downloadedBefore = before),
      )
      .getOrThrow()
    assertEquals("true", seen["latest"])
    assertEquals(after.toString(), seen["downloaded_after"])
    assertEquals(before.toString(), seen["downloaded_before"])
    client.close()
  }

  @Test
  fun availabilityQuerySendsRangeAndSort() = runTest {
    val seen = mutableMapOf<String, String>()
    val client =
      clientRecording(seen, """{"feed_id":"mdb-10","total":0,"offset":0,"limit":10,"checks":[]}""")
    val from = Instant.parse("2026-04-01T00:00:00Z")
    client
      .getGtfsFeedAvailability(
        FeedId("mdb-10"),
        AvailabilityQuery(from = from, sort = AvailabilitySort.Asc),
      )
      .getOrThrow()
    assertEquals(from.toString(), seen["from"])
    assertEquals("asc", seen["sort"])
    client.close()
  }

  private fun clientRecording(
    seen: MutableMap<String, String>,
    respondJson: String = "[]",
  ): MdbV1Client {
    val engine = MockEngine { request ->
      seen.clear()
      request.url.parameters.names().forEach { name ->
        seen[name] = request.url.parameters.getAll(name).orEmpty().joinToString(",")
      }
      respond(
        respondJson,
        HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }
    return MdbV1Client(engine, CatalogAuth.Access("access-1"))
  }
}

private fun csvDoubles(value: String?): List<Double> =
  value.orEmpty().split(",").filter { it.isNotEmpty() }.map { it.toDouble() }
