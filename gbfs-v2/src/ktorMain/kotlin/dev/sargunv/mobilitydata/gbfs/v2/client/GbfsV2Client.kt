package dev.sargunv.mobilitydata.gbfs.v2.client

import dev.sargunv.mobilitydata.gbfs.v2.model.FeedType
import dev.sargunv.mobilitydata.gbfs.v2.model.FreeBikeStatus
import dev.sargunv.mobilitydata.gbfs.v2.model.GbfsFeedData
import dev.sargunv.mobilitydata.gbfs.v2.model.GbfsFeedResponse
import dev.sargunv.mobilitydata.gbfs.v2.model.GbfsJson
import dev.sargunv.mobilitydata.gbfs.v2.model.GeofencingZones
import dev.sargunv.mobilitydata.gbfs.v2.model.Manifest
import dev.sargunv.mobilitydata.gbfs.v2.model.Service
import dev.sargunv.mobilitydata.gbfs.v2.model.StationInformation
import dev.sargunv.mobilitydata.gbfs.v2.model.StationStatus
import dev.sargunv.mobilitydata.gbfs.v2.model.SystemAlerts
import dev.sargunv.mobilitydata.gbfs.v2.model.SystemCalendar
import dev.sargunv.mobilitydata.gbfs.v2.model.SystemHours
import dev.sargunv.mobilitydata.gbfs.v2.model.SystemInformation
import dev.sargunv.mobilitydata.gbfs.v2.model.SystemPricingPlans
import dev.sargunv.mobilitydata.gbfs.v2.model.SystemRegions
import dev.sargunv.mobilitydata.gbfs.v2.model.Url
import dev.sargunv.mobilitydata.gbfs.v2.model.VehicleTypes
import dev.sargunv.mobilitydata.gbfs.v2.model.Versions
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

/**
 * Creates a [GbfsV2Client] with a specific HTTP client engine.
 *
 * @param engineFactory The Ktor HTTP client engine factory to use
 * @param block Configuration block for the HTTP client
 * @return A configured GBFS v2 client instance
 */
public fun <T : HttpClientEngineConfig> GbfsV2Client(
  engineFactory: HttpClientEngineFactory<T>,
  block: HttpClientConfig<T>.() -> Unit = {},
): GbfsV2Client =
  GbfsV2Client(
    HttpClient(engineFactory) {
      block()
      expectSuccess = true
      install(ContentNegotiation) { json(GbfsJson) }
    }
  )

/**
 * HTTP client for fetching GBFS v2 feeds.
 *
 * This client provides convenient methods to fetch all GBFS v2 feed types with automatic JSON
 * deserialization. The client is configured to use the GBFS v2 JSON configuration and expects
 * successful HTTP responses.
 *
 * Example usage:
 * ```kotlin
 * val client = GbfsV2Client()
 * val manifest = client.getManifest("https://example.com/gbfs.json")
 * val service = manifest.data.getForLanguage("en")
 * with(service) {
 *   val systemInfo = client.getSystemInformation()
 *   val stations = client.getStationInformation()
 * }
 * client.close()
 * ```
 */
public class GbfsV2Client internal constructor(private val httpClient: HttpClient) : AutoCloseable {

  /**
   * Creates a [GbfsV2Client] with the default HTTP client engine.
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
  public suspend fun getManifest(discoveryUrl: Url): GbfsFeedResponse<Manifest> =
    getFeedResponse(discoveryUrl)

  /**
   * Fetches the list of GBFS versions supported by this system.
   *
   * @return Response containing supported GBFS versions
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getVersions(): GbfsFeedResponse<Versions> =
    getFeedResponse(service.feeds.getValue(FeedType.Versions))

  /**
   * Fetches system information including name, operator, timezone, and contact details.
   *
   * @return Response containing system information
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getSystemInformation(): GbfsFeedResponse<SystemInformation> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemInformation))

  /**
   * Fetches information about vehicle types available in the system.
   *
   * @return Response containing vehicle type definitions
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getVehicleTypes(): GbfsFeedResponse<VehicleTypes> =
    getFeedResponse(service.feeds.getValue(FeedType.VehicleTypes))

  /**
   * Fetches static information about stations in the system.
   *
   * @return Response containing station information
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getStationInformation(): GbfsFeedResponse<StationInformation> =
    getFeedResponse(service.feeds.getValue(FeedType.StationInformation))

  /**
   * Fetches real-time status of stations including available bikes and docks.
   *
   * @return Response containing current station status
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getStationStatus(): GbfsFeedResponse<StationStatus> =
    getFeedResponse(service.feeds.getValue(FeedType.StationStatus))

  /**
   * Fetches real-time status of free-floating vehicles not currently docked.
   *
   * @return Response containing available vehicle locations and status
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getFreeBikeStatus(): GbfsFeedResponse<FreeBikeStatus> =
    getFeedResponse(service.feeds.getValue(FeedType.FreeBikeStatus))

  /**
   * Fetches hours of operation for the system.
   *
   * @return Response containing system operating hours
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getSystemHours(): GbfsFeedResponse<SystemHours> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemHours))

  /**
   * Fetches the operating calendar for seasonal systems.
   *
   * @return Response containing system operating calendar
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getSystemCalendar(): GbfsFeedResponse<SystemCalendar> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemCalendar))

  /**
   * Fetches information about geographic regions in the system.
   *
   * @return Response containing system regions
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getSystemRegions(): GbfsFeedResponse<SystemRegions> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemRegions))

  /**
   * Fetches pricing plans for the system.
   *
   * @return Response containing pricing plan information
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getSystemPricingPlans(): GbfsFeedResponse<SystemPricingPlans> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemPricingPlans))

  /**
   * Fetches current system alerts about service disruptions or changes.
   *
   * @return Response containing active system alerts
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getSystemAlerts(): GbfsFeedResponse<SystemAlerts> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemAlerts))

  /**
   * Fetches geofencing zones that define geographic restrictions.
   *
   * @return Response containing geofencing zone definitions
   * @receiver The GBFS service containing feed URLs
   */
  context(service: Service)
  public suspend fun getGeofencingZones(): GbfsFeedResponse<GeofencingZones> =
    getFeedResponse(service.feeds.getValue(FeedType.GeofencingZones))

  override fun close(): Unit = httpClient.close()
}
