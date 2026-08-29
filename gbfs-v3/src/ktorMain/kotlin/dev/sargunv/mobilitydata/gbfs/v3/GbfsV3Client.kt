package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.ExperimentalMobilityDataApi
import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.suspendRunCatching
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

  internal suspend inline fun <reified T : GbfsFeedData> getFeedResponse(
    url: Url
  ): Result<GbfsFeedResponse<T>> = suspendRunCatching {
    httpClient.get(url).body<GbfsFeedResponse<T>>()
  }

  /**
   * Fetches the dataset manifest from the given URL.
   *
   * The manifest contains information about all GBFS datasets published by a provider, including
   * their system IDs and available versions.
   *
   * @param manifestUrl The URL of the manifest.json file
   * @return Result wrapping the manifest response containing dataset information, or an error
   */
  public suspend fun getDatasetManifest(
    manifestUrl: Url
  ): Result<GbfsFeedResponse<DatasetManifest>> = getFeedResponse(manifestUrl)

  /**
   * Fetches the GBFS manifest (auto-discovery file) from the given URL.
   *
   * The manifest contains URLs for all available feeds.
   *
   * @param discoveryUrl The URL of the gbfs.json auto-discovery file
   * @return Result wrapping the manifest response containing available feeds, or an error
   */
  public suspend fun getServiceManifest(
    discoveryUrl: Url
  ): Result<GbfsFeedResponse<ServiceManifest>> = getFeedResponse(discoveryUrl)

  /**
   * Fetches the list of GBFS versions supported by this system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing supported GBFS versions, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getVersionManifest(): Result<GbfsFeedResponse<VersionManifest>> =
    suspendRunCatching {
      getFeedResponse<VersionManifest>(service.feeds.getValue(FeedType.VersionManifest))
        .getOrThrow()
    }

  /**
   * Fetches system information including name, operator, timezone, and contact details.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing system information, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getSystemInformation(): Result<GbfsFeedResponse<SystemInformation>> =
    suspendRunCatching {
      getFeedResponse<SystemInformation>(service.feeds.getValue(FeedType.SystemInformation))
        .getOrThrow()
    }

  /**
   * Fetches information about vehicle types available in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing vehicle type definitions, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getVehicleTypes(): Result<GbfsFeedResponse<VehicleTypes>> =
    suspendRunCatching {
      getFeedResponse<VehicleTypes>(service.feeds.getValue(FeedType.VehicleTypes)).getOrThrow()
    }

  /**
   * Fetches static information about stations in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing station information, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getStationInformation(): Result<GbfsFeedResponse<StationInformation>> =
    suspendRunCatching {
      getFeedResponse<StationInformation>(service.feeds.getValue(FeedType.StationInformation))
        .getOrThrow()
    }

  /**
   * Fetches real-time status of stations including available bikes and docks.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing current station status, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getStationStatus(): Result<GbfsFeedResponse<StationStatus>> =
    suspendRunCatching {
      getFeedResponse<StationStatus>(service.feeds.getValue(FeedType.StationStatus)).getOrThrow()
    }

  /**
   * Fetches real-time status of free-floating vehicles not currently docked.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing available vehicle locations and status, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getVehicleStatus(): Result<GbfsFeedResponse<VehicleStatus>> =
    suspendRunCatching {
      getFeedResponse<VehicleStatus>(service.feeds.getValue(FeedType.VehicleStatus)).getOrThrow()
    }

  /**
   * Fetches future availability of vehicles that can be reserved in advance.
   *
   * GBFS 3.1-RC. This feed is optional for station-based vehicles and is not used for free-floating
   * vehicles.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing vehicle availability slots, or an error
   */
  @ExperimentalMobilityDataApi
  @OptIn(ExperimentalMobilityDataApi::class)
  context(service: ServiceManifest)
  public suspend fun getVehicleAvailability(): Result<GbfsFeedResponse<VehicleAvailability>> =
    suspendRunCatching {
      getFeedResponse<VehicleAvailability>(service.feeds.getValue(FeedType.VehicleAvailability))
        .getOrThrow()
    }

  /**
   * Fetches information about geographic regions in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing system regions, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getSystemRegions(): Result<GbfsFeedResponse<SystemRegions>> =
    suspendRunCatching {
      getFeedResponse<SystemRegions>(service.feeds.getValue(FeedType.SystemRegions)).getOrThrow()
    }

  /**
   * Fetches pricing plans for the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing pricing plan information, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getSystemPricingPlans(): Result<GbfsFeedResponse<SystemPricingPlans>> =
    suspendRunCatching {
      getFeedResponse<SystemPricingPlans>(service.feeds.getValue(FeedType.SystemPricingPlans))
        .getOrThrow()
    }

  /**
   * Fetches current system alerts about service disruptions or changes.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing active system alerts, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getSystemAlerts(): Result<GbfsFeedResponse<SystemAlerts>> =
    suspendRunCatching {
      getFeedResponse<SystemAlerts>(service.feeds.getValue(FeedType.SystemAlerts)).getOrThrow()
    }

  /**
   * Fetches geofencing zones that define geographic restrictions.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing geofencing zone definitions, or an error
   */
  context(service: ServiceManifest)
  public suspend fun getGeofencingZones(): Result<GbfsFeedResponse<GeofencingZones>> =
    suspendRunCatching {
      getFeedResponse<GeofencingZones>(service.feeds.getValue(FeedType.GeofencingZones))
        .getOrThrow()
    }

  override fun close(): Unit = httpClient.close()
}
