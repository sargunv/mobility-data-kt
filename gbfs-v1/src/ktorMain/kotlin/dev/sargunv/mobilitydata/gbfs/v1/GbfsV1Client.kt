package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.Url
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

/**
 * Creates a [GbfsV1Client] with a specific HTTP client engine.
 *
 * @param engineFactory The Ktor HTTP client engine factory to use
 * @param block Configuration block for the HTTP client
 * @return A configured GBFS v1 client instance
 */
public fun <T : HttpClientEngineConfig> GbfsV1Client(
  engineFactory: HttpClientEngineFactory<T>,
  block: HttpClientConfig<T>.() -> Unit = {},
): GbfsV1Client =
  GbfsV1Client(
    HttpClient(engineFactory) {
      block()
      expectSuccess = true
      install(ContentNegotiation) { json(GbfsJson) }
    }
  )

/** HTTP client for fetching GBFS v1 feeds. */
public class GbfsV1Client internal constructor(private val httpClient: HttpClient) : AutoCloseable {

  /**
   * Creates a [GbfsV1Client] with the default HTTP client engine.
   *
   * @param block Configuration block for the HTTP client
   */
  public constructor(
    block: HttpClientConfig<*>.() -> Unit = {}
  ) : this(
    HttpClient {
      block()
      expectSuccess = true
      install(ContentNegotiation) { json(GbfsJson) }
    }
  )

  internal suspend inline fun <reified T : GbfsFeedData> getFeedResponse(url: Url) =
    httpClient.get(url).body<GbfsFeedResponse<T>>()

  /**
   * Fetches the GBFS manifest (auto-discovery file) from the given URL.
   *
   * The manifest contains URLs for all available feeds in all supported languages.
   *
   * @param discoveryUrl The URL of the gbfs.json auto-discovery file
   * @return The manifest response containing available feeds
   */
  public suspend fun getManifest(discoveryUrl: Url): GbfsFeedResponse<GbfsManifest> =
    getFeedResponse(discoveryUrl)

  /**
   * Fetches the list of GBFS versions supported by this system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing supported GBFS versions
   */
  context(service: Service)
  public suspend fun getVersions(): GbfsFeedResponse<Versions> =
    getFeedResponse(service.feeds.getValue(FeedType.GbfsVersions))

  /**
   * Fetches system information including name, operator, timezone, and contact details.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing system information
   */
  context(service: Service)
  public suspend fun getSystemInformation(): GbfsFeedResponse<SystemInformation> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemInformation))

  /**
   * Fetches static information about stations in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing station information
   */
  context(service: Service)
  public suspend fun getStationInformation(): GbfsFeedResponse<StationInformation> =
    getFeedResponse(service.feeds.getValue(FeedType.StationInformation))

  /**
   * Fetches real-time status of stations including available bikes and docks.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing current station status
   */
  context(service: Service)
  public suspend fun getStationStatus(): GbfsFeedResponse<StationStatus> =
    getFeedResponse(service.feeds.getValue(FeedType.StationStatus))

  /**
   * Fetches real-time status of free-floating vehicles not currently docked.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing available vehicle locations and status
   */
  context(service: Service)
  public suspend fun getFreeBikeStatus(): GbfsFeedResponse<FreeBikeStatus> =
    getFeedResponse(service.feeds.getValue(FeedType.FreeBikeStatus))

  /**
   * Fetches hours of operation for the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing system operating hours
   */
  context(service: Service)
  public suspend fun getSystemHours(): GbfsFeedResponse<SystemHours> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemHours))

  /**
   * Fetches the operating calendar for seasonal systems.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing system operating calendar
   */
  context(service: Service)
  public suspend fun getSystemCalendar(): GbfsFeedResponse<SystemCalendar> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemCalendar))

  /**
   * Fetches information about geographic regions in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing system regions
   */
  context(service: Service)
  public suspend fun getSystemRegions(): GbfsFeedResponse<SystemRegions> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemRegions))

  /**
   * Fetches pricing plans for the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing pricing plan information
   */
  context(service: Service)
  public suspend fun getSystemPricingPlans(): GbfsFeedResponse<SystemPricingPlans> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemPricingPlans))

  /**
   * Fetches current system alerts about service disruptions or changes.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing active system alerts
   */
  context(service: Service)
  public suspend fun getSystemAlerts(): GbfsFeedResponse<SystemAlerts> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemAlerts))

  override fun close(): Unit = httpClient.close()
}
