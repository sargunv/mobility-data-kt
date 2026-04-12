package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.utils.Url
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

/** HTTP client for fetching GBFS v2 feeds. */
public class GbfsV2Client internal constructor(private val httpClient: HttpClient) : AutoCloseable {

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
  ): Result<GbfsFeedResponse<T>> = runCatching { httpClient.get(url).body<GbfsFeedResponse<T>>() }

  /**
   * Fetches the GBFS manifest (auto-discovery file) from the given URL.
   *
   * The manifest contains URLs for all available feeds in all supported languages.
   *
   * @param discoveryUrl The URL of the gbfs.json auto-discovery file
   * @return Result wrapping the manifest response containing available feeds, or an error
   */
  public suspend fun getSystemManifest(
    discoveryUrl: Url
  ): Result<GbfsFeedResponse<SystemManifest>> = getFeedResponse(discoveryUrl)

  /**
   * Fetches the list of GBFS versions supported by this system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing supported GBFS versions, or an error
   */
  context(service: Service)
  public suspend fun getVersionManifest(): Result<GbfsFeedResponse<VersionManifest>> = runCatching {
    getFeedResponse<VersionManifest>(service.feeds.getValue(FeedType.VersionManifest)).getOrThrow()
  }

  /**
   * Fetches system information including name, operator, timezone, and contact details.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing system information, or an error
   */
  context(service: Service)
  public suspend fun getSystemInformation(): Result<GbfsFeedResponse<SystemInformation>> =
    runCatching {
      getFeedResponse<SystemInformation>(service.feeds.getValue(FeedType.SystemInformation))
        .getOrThrow()
    }

  /**
   * Fetches information about vehicle types available in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing vehicle type definitions, or an error
   */
  context(service: Service)
  public suspend fun getVehicleTypes(): Result<GbfsFeedResponse<VehicleTypes>> = runCatching {
    getFeedResponse<VehicleTypes>(service.feeds.getValue(FeedType.VehicleTypes)).getOrThrow()
  }

  /**
   * Fetches static information about stations in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing station information, or an error
   */
  context(service: Service)
  public suspend fun getStationInformation(): Result<GbfsFeedResponse<StationInformation>> =
    runCatching {
      getFeedResponse<StationInformation>(service.feeds.getValue(FeedType.StationInformation))
        .getOrThrow()
    }

  /**
   * Fetches real-time status of stations including available bikes and docks.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing current station status, or an error
   */
  context(service: Service)
  public suspend fun getStationStatus(): Result<GbfsFeedResponse<StationStatus>> = runCatching {
    getFeedResponse<StationStatus>(service.feeds.getValue(FeedType.StationStatus)).getOrThrow()
  }

  /**
   * Fetches real-time status of free-floating vehicles not currently docked.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing available vehicle locations and status, or an error
   */
  context(service: Service)
  public suspend fun getFreeBikeStatus(): Result<GbfsFeedResponse<FreeBikeStatus>> = runCatching {
    getFeedResponse<FreeBikeStatus>(service.feeds.getValue(FeedType.FreeBikeStatus)).getOrThrow()
  }

  /**
   * Fetches hours of operation for the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing system operating hours, or an error
   */
  context(service: Service)
  public suspend fun getSystemHours(): Result<GbfsFeedResponse<SystemHours>> = runCatching {
    getFeedResponse<SystemHours>(service.feeds.getValue(FeedType.SystemHours)).getOrThrow()
  }

  /**
   * Fetches the operating calendar for seasonal systems.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing system operating calendar, or an error
   */
  context(service: Service)
  public suspend fun getSystemCalendar(): Result<GbfsFeedResponse<SystemCalendar>> = runCatching {
    getFeedResponse<SystemCalendar>(service.feeds.getValue(FeedType.SystemCalendar)).getOrThrow()
  }

  /**
   * Fetches information about geographic regions in the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing system regions, or an error
   */
  context(service: Service)
  public suspend fun getSystemRegions(): Result<GbfsFeedResponse<SystemRegions>> = runCatching {
    getFeedResponse<SystemRegions>(service.feeds.getValue(FeedType.SystemRegions)).getOrThrow()
  }

  /**
   * Fetches pricing plans for the system.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing pricing plan information, or an error
   */
  context(service: Service)
  public suspend fun getSystemPricingPlans(): Result<GbfsFeedResponse<SystemPricingPlans>> =
    runCatching {
      getFeedResponse<SystemPricingPlans>(service.feeds.getValue(FeedType.SystemPricingPlans))
        .getOrThrow()
    }

  /**
   * Fetches current system alerts about service disruptions or changes.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing active system alerts, or an error
   */
  context(service: Service)
  public suspend fun getSystemAlerts(): Result<GbfsFeedResponse<SystemAlerts>> = runCatching {
    getFeedResponse<SystemAlerts>(service.feeds.getValue(FeedType.SystemAlerts)).getOrThrow()
  }

  /**
   * Fetches geofencing zones that define geographic restrictions.
   *
   * @param [service] The GBFS service containing feed URLs
   * @return Result wrapping response containing geofencing zone definitions, or an error
   */
  context(service: Service)
  public suspend fun getGeofencingZones(): Result<GbfsFeedResponse<GeofencingZones>> = runCatching {
    getFeedResponse<GeofencingZones>(service.feeds.getValue(FeedType.GeofencingZones)).getOrThrow()
  }

  override fun close(): Unit = httpClient.close()
}
