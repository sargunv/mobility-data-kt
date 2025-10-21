package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.Url
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

/** HTTP client for fetching GBFS v3 feeds. */
public class GbfsV3Client internal constructor(private val httpClient: HttpClient) : AutoCloseable {

  public constructor(
    block: HttpClientConfig<*>.() -> Unit = {}
  ) : this(
    HttpClient {
      block()
      expectSuccess = true
      install(ContentNegotiation) { json(GbfsJson) }
    }
  )

  public constructor(
    engine: HttpClientEngine,
    block: HttpClientConfig<*>.() -> Unit = {},
  ) : this(
    HttpClient(engine) {
      block()
      expectSuccess = true
      install(ContentNegotiation) { json(GbfsJson) }
    }
  )

  internal suspend inline fun <reified T : GbfsFeedData> getFeedResponse(url: Url) =
    httpClient.get(url).body<GbfsFeedResponse<T>>()

  /**
   * Fetches the dataset manifest from the given URL.
   *
   * The manifest contains information about all GBFS datasets published by a provider, including
   * their system IDs and available versions.
   *
   * @param manifestUrl The URL of the manifest.json file
   * @return The manifest response containing dataset information
   */
  public suspend fun getDatasetManifest(manifestUrl: Url): GbfsFeedResponse<DatasetManifest> =
    getFeedResponse(manifestUrl)

  /**
   * Fetches the GBFS manifest (auto-discovery file) from the given URL.
   *
   * The manifest contains URLs for all available feeds.
   *
   * @param discoveryUrl The URL of the gbfs.json auto-discovery file
   * @return The manifest response containing available feeds
   */
  public suspend fun getServiceManifest(discoveryUrl: Url): GbfsFeedResponse<ServiceManifest> =
    getFeedResponse(discoveryUrl)

  /**
   * Fetches the list of GBFS versions supported by this system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing supported GBFS versions
   */
  context(service: ServiceManifest)
  public suspend fun getVersionManifest(): GbfsFeedResponse<VersionManifest> =
    getFeedResponse(service.feeds.getValue(FeedType.VersionManifest))

  /**
   * Fetches system information including name, operator, timezone, and contact details.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing system information
   */
  context(service: ServiceManifest)
  public suspend fun getSystemInformation(): GbfsFeedResponse<SystemInformation> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemInformation))

  /**
   * Fetches information about vehicle types available in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing vehicle type definitions
   */
  context(service: ServiceManifest)
  public suspend fun getVehicleTypes(): GbfsFeedResponse<VehicleTypes> =
    getFeedResponse(service.feeds.getValue(FeedType.VehicleTypes))

  /**
   * Fetches static information about stations in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing station information
   */
  context(service: ServiceManifest)
  public suspend fun getStationInformation(): GbfsFeedResponse<StationInformation> =
    getFeedResponse(service.feeds.getValue(FeedType.StationInformation))

  /**
   * Fetches real-time status of stations including available bikes and docks.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing current station status
   */
  context(service: ServiceManifest)
  public suspend fun getStationStatus(): GbfsFeedResponse<StationStatus> =
    getFeedResponse(service.feeds.getValue(FeedType.StationStatus))

  /**
   * Fetches real-time status of free-floating vehicles not currently docked.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing available vehicle locations and status
   */
  context(service: ServiceManifest)
  public suspend fun getVehicleStatus(): GbfsFeedResponse<VehicleStatus> =
    getFeedResponse(service.feeds.getValue(FeedType.VehicleStatus))

  /**
   * Fetches information about geographic regions in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing system regions
   */
  context(service: ServiceManifest)
  public suspend fun getSystemRegions(): GbfsFeedResponse<SystemRegions> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemRegions))

  /**
   * Fetches pricing plans for the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing pricing plan information
   */
  context(service: ServiceManifest)
  public suspend fun getSystemPricingPlans(): GbfsFeedResponse<SystemPricingPlans> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemPricingPlans))

  /**
   * Fetches current system alerts about service disruptions or changes.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing active system alerts
   */
  context(service: ServiceManifest)
  public suspend fun getSystemAlerts(): GbfsFeedResponse<SystemAlerts> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemAlerts))

  /**
   * Fetches geofencing zones that define geographic restrictions.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Response containing geofencing zone definitions
   */
  context(service: ServiceManifest)
  public suspend fun getGeofencingZones(): GbfsFeedResponse<GeofencingZones> =
    getFeedResponse(service.feeds.getValue(FeedType.GeofencingZones))

  override fun close(): Unit = httpClient.close()
}
