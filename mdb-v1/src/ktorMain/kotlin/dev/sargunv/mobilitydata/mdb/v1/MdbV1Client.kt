package dev.sargunv.mobilitydata.mdb.v1

import dev.sargunv.mobilitydata.utils.ExperimentalMobilityDataApi
import dev.sargunv.mobilitydata.utils.suspendRunCatching
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** HTTP client for the Mobility Database Catalog API v1. */
public class MdbV1Client
internal constructor(
  private val httpClient: HttpClient,
  private val auth: CatalogAuth,
  private val baseUrl: String,
) : AutoCloseable {
  private val tokenMutex = Mutex()
  private var accessToken: String? =
    when (auth) {
      is CatalogAuth.Access -> auth.accessToken
      is CatalogAuth.Refresh -> null
    }

  /**
   * Creates a client that uses the platform default engine.
   *
   * @param auth Refresh token or access token
   * @param baseUrl Catalog API root, including a trailing slash
   * @param block Extra Ktor configuration applied before this client sets `expectSuccess`
   */
  public constructor(
    auth: CatalogAuth,
    baseUrl: String = DefaultBaseUrl,
    block: HttpClientConfig<*>.() -> Unit = {},
  ) : this(HttpClient { configureCatalogClient(block) }, auth, baseUrl)

  /**
   * Creates a client that uses the given engine.
   *
   * Tests pass [io.ktor.client.engine.mock.MockEngine]. Applications pass an engine of their
   * choice. This library does not bundle one.
   *
   * @param engine Ktor engine
   * @param auth Refresh token or access token
   * @param baseUrl Catalog API root, including a trailing slash
   * @param block Extra Ktor configuration applied before this client sets `expectSuccess`
   */
  public constructor(
    engine: HttpClientEngine,
    auth: CatalogAuth,
    baseUrl: String = DefaultBaseUrl,
    block: HttpClientConfig<*>.() -> Unit = {},
  ) : this(HttpClient(engine) { configureCatalogClient(block) }, auth, baseUrl)

  /**
   * Lists catalog feeds.
   *
   * @param query Limit, offset, and filters
   * @return The page of feeds, or an error
   */
  public suspend fun getFeeds(query: FeedQuery = FeedQuery()): Result<List<Feed>> =
    catalogGet("v1", "feeds") { appendFeedQuery(query) }

  /**
   * Fetches one feed by catalog id.
   *
   * @param id Catalog feed id, such as `mdb-1210`
   * @return The feed, or an error
   */
  public suspend fun getFeed(id: FeedId): Result<Feed> = catalogGet("v1", "feeds", id.value)

  /**
   * Lists GTFS feeds.
   *
   * @param query Limit, offset, and filters
   * @return The page of GTFS feeds, or an error
   */
  public suspend fun getGtfsFeeds(query: GtfsFeedQuery = GtfsFeedQuery()): Result<List<Feed.Gtfs>> =
    catalogGet("v1", "gtfs_feeds") { appendGtfsFeedQuery(query) }

  /**
   * Fetches one GTFS feed.
   *
   * @param id Catalog feed id
   * @return The GTFS feed, or an error
   */
  public suspend fun getGtfsFeed(id: FeedId): Result<Feed.Gtfs> =
    catalogGet("v1", "gtfs_feeds", id.value)

  /**
   * Lists GTFS Realtime feeds.
   *
   * @param query Limit, offset, and filters
   * @return The page of GTFS Realtime feeds, or an error
   */
  public suspend fun getGtfsRtFeeds(
    query: GtfsRtFeedQuery = GtfsRtFeedQuery()
  ): Result<List<Feed.GtfsRt>> = catalogGet("v1", "gtfs_rt_feeds") { appendGtfsRtFeedQuery(query) }

  /**
   * Fetches one GTFS Realtime feed.
   *
   * @param id Catalog feed id
   * @return The GTFS Realtime feed, or an error
   */
  public suspend fun getGtfsRtFeed(id: FeedId): Result<Feed.GtfsRt> =
    catalogGet("v1", "gtfs_rt_feeds", id.value)

  /**
   * Lists GBFS feeds.
   *
   * @param query Limit, offset, and filters
   * @return The page of GBFS feeds, or an error
   */
  public suspend fun getGbfsFeeds(query: GbfsFeedQuery = GbfsFeedQuery()): Result<List<Feed.Gbfs>> =
    catalogGet("v1", "gbfs_feeds") { appendGbfsFeedQuery(query) }

  /**
   * Fetches one GBFS feed.
   *
   * @param id Catalog feed id
   * @return The GBFS feed, or an error
   */
  public suspend fun getGbfsFeed(id: FeedId): Result<Feed.Gbfs> =
    catalogGet("v1", "gbfs_feeds", id.value)

  /**
   * Lists datasets for a GTFS feed, newest first.
   *
   * @param id Catalog feed id
   * @param query Latest-only, page, and download-date window
   * @return The page of datasets, or an error
   */
  public suspend fun getGtfsFeedDatasets(
    id: FeedId,
    query: DatasetQuery = DatasetQuery(),
  ): Result<List<GtfsDataset>> =
    catalogGet("v1", "gtfs_feeds", id.value, "datasets") { appendDatasetQuery(query) }

  /**
   * Lists GTFS Realtime feeds related to a GTFS feed.
   *
   * @param id Catalog GTFS feed id
   * @return Related GTFS Realtime feeds, or an error
   */
  public suspend fun getGtfsFeedGtfsRtFeeds(id: FeedId): Result<List<Feed.GtfsRt>> =
    catalogGet("v1", "gtfs_feeds", id.value, "gtfs_rt_feeds")

  /**
   * Fetches one hosted GTFS dataset.
   *
   * @param id Dataset id
   * @return The dataset, or an error
   */
  public suspend fun getDatasetGtfs(id: String): Result<GtfsDataset> =
    catalogGet("v1", "datasets", "gtfs", id)

  /**
   * Fetches API process metadata.
   *
   * @return The metadata, or an error
   */
  public suspend fun getMetadata(): Result<Metadata> = catalogGet("v1", "metadata")

  /**
   * Full-text search over feeds.
   *
   * @param query Search text and filters
   * @return Matching feeds with a total count, or an error
   */
  public suspend fun searchFeeds(
    query: SearchFeedsQuery = SearchFeedsQuery()
  ): Result<SearchFeedsResponse> = catalogGet("v1", "search") { appendSearchFeedsQuery(query) }

  /**
   * Searches catalog locations.
   *
   * @param query Free-text query and filters
   * @return Matching locations with a total count, or an error
   */
  public suspend fun getLocations(
    query: LocationQuery = LocationQuery()
  ): Result<LocationSearchResponse> = catalogGet("v1", "locations") { appendLocationQuery(query) }

  /**
   * Lists licenses.
   *
   * @param query Limit and offset
   * @return The page of licenses, or an error
   */
  public suspend fun getLicenses(query: LicenseQuery = LicenseQuery()): Result<List<License>> =
    catalogGet("v1", "licenses") { appendLicenseQuery(query) }

  /**
   * Fetches one license, including its rules.
   *
   * @param id License id, often an SPDX id
   * @return The license, or an error
   */
  public suspend fun getLicense(id: String): Result<LicenseWithRules> =
    catalogGet("v1", "licenses", id)

  /**
   * Resolves a license URL to catalog licenses.
   *
   * @param request License URL to match
   * @return Matching licenses, or an error
   */
  public suspend fun getMatchingLicenses(
    request: LicenseMatchRequest
  ): Result<List<MatchingLicense>> = catalogPost("v1", "licenses:match", body = request)

  /**
   * Returns historical availability checks for a GTFS feed.
   *
   * @param id Catalog GTFS feed id
   * @param query Time window, page, and sort
   * @return Availability history, or an error
   */
  @ExperimentalMobilityDataApi
  public suspend fun getGtfsFeedAvailability(
    id: FeedId,
    query: AvailabilityQuery = AvailabilityQuery(),
  ): Result<GtfsFeedAvailabilityResponse> =
    catalogGet("v1", "gtfs_feeds", id.value, "availability") { appendAvailabilityQuery(query) }

  /**
   * Returns the Seal of Reliability breakdown for a GTFS feed.
   *
   * @param id Catalog GTFS feed id
   * @return The reliability report, or an error
   */
  @ExperimentalMobilityDataApi
  public suspend fun getGtfsFeedReliability(id: FeedId): Result<FeedReliabilityReport> =
    catalogGet("v1", "gtfs_feeds", id.value, "reliability")

  override fun close(): Unit = httpClient.close()

  private suspend inline fun <reified T> catalogGet(
    vararg pathSegments: String,
    crossinline query: URLBuilder.() -> Unit = {},
  ): Result<T> = withCatalogAuth { getOnce<T>(pathSegments, query) }

  private suspend inline fun <reified T, reified B> catalogPost(
    vararg pathSegments: String,
    body: B,
  ): Result<T> = withCatalogAuth { postOnce<T, B>(pathSegments, body) }

  private suspend inline fun <reified T> withCatalogAuth(
    crossinline call: suspend () -> T
  ): Result<T> = suspendRunCatching {
    val tokenAtStart = ensureAccessToken()
    try {
      call()
    } catch (e: ClientRequestException) {
      if (e.response.status == HttpStatusCode.Unauthorized && auth is CatalogAuth.Refresh) {
        refreshAccessTokenIfStale(tokenAtStart)
        call()
      } else {
        throw e
      }
    }
  }

  private suspend inline fun <reified T> getOnce(
    pathSegments: Array<out String>,
    crossinline query: URLBuilder.() -> Unit,
  ): T =
    httpClient
      .request {
        method = HttpMethod.Get
        url {
          takeFrom(baseUrl)
          appendPathSegments(pathSegments.toList(), encodeSlash = true)
          query()
        }
        accessToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
      }
      .body()

  private suspend inline fun <reified T, reified B> postOnce(
    pathSegments: Array<out String>,
    body: B,
  ): T =
    httpClient
      .request {
        method = HttpMethod.Post
        url {
          takeFrom(baseUrl)
          appendPathSegments(pathSegments.toList(), encodeSlash = true)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
        accessToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
      }
      .body()

  private suspend fun ensureAccessToken(): String? {
    accessToken?.let {
      return it
    }
    if (auth !is CatalogAuth.Refresh) return null
    return tokenMutex.withLock {
      accessToken?.let {
        return it
      }
      fetchNewAccessToken()
    }
  }

  private suspend fun refreshAccessTokenIfStale(staleToken: String?) {
    if (auth !is CatalogAuth.Refresh) return
    tokenMutex.withLock {
      if (accessToken != staleToken) return
      fetchNewAccessToken()
    }
  }

  private suspend fun fetchNewAccessToken(): String {
    val refreshToken = (auth as CatalogAuth.Refresh).refreshToken
    val token =
      httpClient
        .post {
          url {
            takeFrom(baseUrl)
            appendPathSegments("v1", "tokens", "access")
          }
          contentType(ContentType.Application.Json)
          setBody(AccessTokenRequest(refreshToken))
        }
        .body<AccessToken>()
    val access = token.accessToken ?: error("catalog token response missing access_token")
    accessToken = access
    return access
  }

  /** Named constants for [MdbV1Client]. */
  public companion object {
    /** Production catalog root. */
    public const val DefaultBaseUrl: String = "https://api.mobilitydatabase.org/"
  }
}

private fun HttpClientConfig<*>.configureCatalogClient(block: HttpClientConfig<*>.() -> Unit) {
  block()
  expectSuccess = true
  followRedirects = false
  install(ContentNegotiation) { json(MdbJson) }
}
