package dev.sargunv.mobilitydata.mdb.v1

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
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json

/** HTTP client for the Mobility Database Catalog API v1. */
public class MdbV1Client
internal constructor(
  private val httpClient: HttpClient,
  private val auth: CatalogAuth,
  private val baseUrl: String,
) : AutoCloseable {
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
    catalogGet("v1/feeds") { appendFeedQuery(query) }

  /**
   * Fetches one feed by catalog id.
   *
   * @param id Catalog feed id, such as `mdb-1210`
   * @return The feed, or an error
   */
  public suspend fun getFeed(id: FeedId): Result<Feed> = catalogGet("v1/feeds/${id.value}")

  override fun close(): Unit = httpClient.close()

  private suspend inline fun <reified T> catalogGet(
    path: String,
    crossinline query: URLBuilder.() -> Unit = {},
  ): Result<T> = suspendRunCatching {
    if (accessToken == null && auth is CatalogAuth.Refresh) {
      refreshAccessToken()
    }
    try {
      getOnce<T>(path, query)
    } catch (e: ClientRequestException) {
      if (e.response.status == HttpStatusCode.Unauthorized && auth is CatalogAuth.Refresh) {
        refreshAccessToken()
        getOnce<T>(path, query)
      } else {
        throw e
      }
    }
  }

  private suspend inline fun <reified T> getOnce(
    path: String,
    crossinline query: URLBuilder.() -> Unit,
  ): T =
    httpClient
      .request {
        method = HttpMethod.Get
        url {
          takeFrom(baseUrl)
          appendPathSegments(path.split("/"))
          query()
        }
        accessToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
      }
      .body()

  private suspend fun refreshAccessToken() {
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
    accessToken = token.accessToken ?: error("catalog token response missing access_token")
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
