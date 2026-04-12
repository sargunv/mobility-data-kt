package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.suspendRunCatching
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.Url as KtorUrl
import io.ktor.serialization.kotlinx.json.json

/** HTTP client for fetching GOFS v1 feeds. */
public class GofsV1Client internal constructor(private val httpClient: HttpClient) : AutoCloseable {

  public constructor(
    block: HttpClientConfig<*>.() -> Unit = {}
  ) : this(
    HttpClient {
      block()
      expectSuccess = true
      install(ContentNegotiation) { json(GofsJson) }
    }
  )

  public constructor(
    engine: HttpClientEngine,
    block: HttpClientConfig<*>.() -> Unit = {},
  ) : this(
    HttpClient(engine) {
      block()
      expectSuccess = true
      install(ContentNegotiation) { json(GofsJson) }
    }
  )

  internal suspend inline fun <reified T : GofsFeedData> getFeedResponse(
    url: Url
  ): Result<GofsFeedResponse<T>> = suspendRunCatching {
    httpClient.get(url).body<GofsFeedResponse<T>>()
  }

  internal suspend inline fun <reified T : GofsFeedData> getFeedResponse(
    url: KtorUrl
  ): Result<GofsFeedResponse<T>> = suspendRunCatching {
    httpClient.get(url).body<GofsFeedResponse<T>>()
  }

  /**
   * Fetches the GOFS manifest (auto-discovery file) from the given URL.
   *
   * The manifest contains URLs for all available feeds in all supported languages.
   *
   * @param discoveryUrl The URL of the gofs.json auto-discovery file
   * @return Result wrapping the manifest response containing available feeds, or an error
   */
  public suspend fun getSystemManifest(
    discoveryUrl: Url
  ): Result<GofsFeedResponse<SystemManifest>> = getFeedResponse(discoveryUrl)

  /**
   * Fetches the list of GOFS versions supported by this system.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Result wrapping response containing supported GOFS versions, or an error
   */
  context(service: Service)
  public suspend fun getVersionManifest(): Result<GofsFeedResponse<VersionManifest>> =
    suspendRunCatching {
      getFeedResponse<VersionManifest>(service.feeds.getValue(FeedType.VersionManifest))
        .getOrThrow()
    }

  /**
   * Fetches system information including name, operator, timezone, and contact details.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Result wrapping response containing system information, or an error
   */
  context(service: Service)
  public suspend fun getSystemInformation(): Result<GofsFeedResponse<SystemInformation>> =
    suspendRunCatching {
      getFeedResponse<SystemInformation>(service.feeds.getValue(FeedType.SystemInformation))
        .getOrThrow()
    }

  /**
   * Fetches information about service brands used in the system.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Result wrapping response containing service brand definitions, or an error
   */
  context(service: Service)
  public suspend fun getServiceBrands(): Result<GofsFeedResponse<ServiceBrands>> =
    suspendRunCatching {
      getFeedResponse<ServiceBrands>(service.feeds.getValue(FeedType.ServiceBrands)).getOrThrow()
    }

  /**
   * Fetches information about vehicle types available in the system.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Result wrapping response containing vehicle type definitions, or an error
   */
  context(service: Service)
  public suspend fun getVehicleTypes(): Result<GofsFeedResponse<VehicleTypes>> =
    suspendRunCatching {
      getFeedResponse<VehicleTypes>(service.feeds.getValue(FeedType.VehicleTypes)).getOrThrow()
    }

  /**
   * Fetches zone definitions where services operate.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Result wrapping response containing zone definitions, or an error
   */
  context(service: Service)
  public suspend fun getZones(): Result<GofsFeedResponse<Zones>> = suspendRunCatching {
    getFeedResponse<Zones>(service.feeds.getValue(FeedType.Zones)).getOrThrow()
  }

  /**
   * Fetches operating rules governing the service.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Result wrapping response containing operating rules, or an error
   */
  context(service: Service)
  public suspend fun getOperatingRules(): Result<GofsFeedResponse<OperatingRules>> =
    suspendRunCatching {
      getFeedResponse<OperatingRules>(service.feeds.getValue(FeedType.OperatingRules)).getOrThrow()
    }

  /**
   * Fetches calendar definitions for service availability.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Result wrapping response containing calendar definitions, or an error
   */
  context(service: Service)
  public suspend fun getCalendars(): Result<GofsFeedResponse<Calendars>> = suspendRunCatching {
    getFeedResponse<Calendars>(service.feeds.getValue(FeedType.Calendars)).getOrThrow()
  }

  /**
   * Fetches fare structures and pricing information.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Result wrapping response containing fare information, or an error
   */
  context(service: Service)
  public suspend fun getFares(): Result<GofsFeedResponse<Fares>> = suspendRunCatching {
    getFeedResponse<Fares>(service.feeds.getValue(FeedType.Fares)).getOrThrow()
  }

  /**
   * Fetches wait time information for services.
   *
   * If either [dropOffLat] or [dropOffLon] is provided, both must be provided.
   *
   * @param [service] The GOFS service containing feed URLs
   * @param pickupLat Latitude where the user will be picked up
   * @param pickupLon Longitude where the user will be picked up
   * @param dropOffLat Latitude where the user will be dropped off (optional)
   * @param dropOffLon Longitude where the user will be dropped off (optional)
   * @param brandIds List of brand IDs to filter wait times (optional)
   * @return Result wrapping response containing wait time information, or an error
   */
  context(service: Service)
  public suspend fun getWaitTimes(
    pickupLat: Double,
    pickupLon: Double,
    dropOffLat: Double? = null,
    dropOffLon: Double? = null,
    brandIds: List<String> = emptyList(),
  ): Result<GofsFeedResponse<WaitTimes>> {
    require(
      dropOffLat == null && dropOffLon == null || (dropOffLat != null && dropOffLon != null)
    ) {
      "Both dropOffLat and dropOffLon must be provided together"
    }

    return suspendRunCatching {
      val url = URLBuilder(service.feeds.getValue(FeedType.WaitTimes))

      url.parameters.append("pickup_lat", pickupLat.toString())
      url.parameters.append("pickup_lon", pickupLon.toString())

      if (dropOffLat != null && dropOffLon != null) {
        url.parameters.append("drop_off_lat", dropOffLat.toString())
        url.parameters.append("drop_off_lon", dropOffLon.toString())
      }

      if (brandIds.isNotEmpty()) {
        val joined = brandIds.joinToString(",")
        url.parameters.append("brand_id", joined)
      }

      getFeedResponse<WaitTimes>(url.build()).getOrThrow()
    }
  }

  /**
   * Fetches booking rules for the service.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Result wrapping response containing booking rules, or an error
   */
  context(service: Service)
  public suspend fun getBookingRules(): Result<GofsFeedResponse<BookingRules>> =
    suspendRunCatching {
      getFeedResponse<BookingRules>(service.feeds.getValue(FeedType.BookingRules)).getOrThrow()
    }

  /**
   * Fetches real-time booking information.
   *
   * If either [dropOffLat] or [dropOffLon] is provided, both must be provided.
   *
   * Optionally, full addresses for pickup and drop-off locations can be provided. so that the
   * addresses do not get lost during reverse geocoding.
   *
   * @param [service] The GOFS service containing feed URLs
   * @param pickupLat Latitude where the user will be picked up
   * @param pickupLon Longitude where the user will be picked up
   * @param dropOffLat Latitude where the user will be dropped off (optional)
   * @param dropOffLon Longitude where the user will be dropped off (optional)
   * @param brandIds List of brand IDs to filter wait times (optional)
   * @param pickupAddress Full address where the user will be picked up (optional)
   * @param dropOffAddress Full address where the user will be dropped off (optional)
   * @return Result wrapping response containing real-time booking information, or an error
   */
  context(service: Service)
  public suspend fun getRealtimeBookings(
    pickupLat: Double,
    pickupLon: Double,
    dropOffLat: Double? = null,
    dropOffLon: Double? = null,
    brandIds: List<String> = emptyList(),
    pickupAddress: String? = null,
    dropOffAddress: String? = null,
  ): Result<GofsFeedResponse<RealtimeBookings>> {
    require(
      dropOffLat == null && dropOffLon == null || (dropOffLat != null && dropOffLon != null)
    ) {
      "Both dropOffLat and dropOffLon must be provided together"
    }

    return suspendRunCatching {
      val url = URLBuilder(service.feeds.getValue(FeedType.RealtimeBookings))

      url.parameters.append("pickup_lat", pickupLat.toString())
      url.parameters.append("pickup_lon", pickupLon.toString())

      if (dropOffLat != null && dropOffLon != null) {
        url.parameters.append("drop_off_lat", dropOffLat.toString())
        url.parameters.append("drop_off_lon", dropOffLon.toString())
      }

      if (brandIds.isNotEmpty()) {
        val joined = brandIds.joinToString(",")
        url.parameters.append("brand_id", joined)
      }

      if (pickupAddress != null) url.parameters.append("pickup_address", pickupAddress)
      if (dropOffAddress != null) url.parameters.append("drop_off_address", dropOffAddress)

      getFeedResponse<RealtimeBookings>(url.build()).getOrThrow()
    }
  }

  override fun close(): Unit = httpClient.close()
}
