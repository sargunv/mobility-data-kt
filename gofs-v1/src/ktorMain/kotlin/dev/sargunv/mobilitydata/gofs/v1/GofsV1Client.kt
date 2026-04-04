package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Url
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

  internal suspend inline fun <reified T : GofsFeedData> getFeedResponse(url: Url) =
    httpClient.get(url).body<GofsFeedResponse<T>>()

  internal suspend inline fun <reified T : GofsFeedData> getFeedResponse(url: KtorUrl) =
    httpClient.get(url).body<GofsFeedResponse<T>>()

  /**
   * Fetches the GOFS manifest (auto-discovery file) from the given URL.
   *
   * The manifest contains URLs for all available feeds in all supported languages.
   *
   * @param discoveryUrl The URL of the gofs.json auto-discovery file
   * @return The manifest response containing available feeds
   */
  public suspend fun getSystemManifest(discoveryUrl: Url): GofsFeedResponse<SystemManifest> =
    getFeedResponse(discoveryUrl)

  /**
   * Fetches the list of GOFS versions supported by this system.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Response containing supported GOFS versions
   */
  context(service: Service)
  public suspend fun getVersionManifest(): GofsFeedResponse<VersionManifest> =
    getFeedResponse(service.feeds.getValue(FeedType.VersionManifest))

  /**
   * Fetches system information including name, operator, timezone, and contact details.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Response containing system information
   */
  context(service: Service)
  public suspend fun getSystemInformation(): GofsFeedResponse<SystemInformation> =
    getFeedResponse(service.feeds.getValue(FeedType.SystemInformation))

  /**
   * Fetches information about service brands used in the system.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Response containing service brand definitions
   */
  context(service: Service)
  public suspend fun getServiceBrands(): GofsFeedResponse<ServiceBrands> =
    getFeedResponse(service.feeds.getValue(FeedType.ServiceBrands))

  /**
   * Fetches information about vehicle types available in the system.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Response containing vehicle type definitions
   */
  context(service: Service)
  public suspend fun getVehicleTypes(): GofsFeedResponse<VehicleTypes> =
    getFeedResponse(service.feeds.getValue(FeedType.VehicleTypes))

  /**
   * Fetches zone definitions where services operate.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Response containing zone definitions
   */
  context(service: Service)
  public suspend fun getZones(): GofsFeedResponse<Zones> =
    getFeedResponse(service.feeds.getValue(FeedType.Zones))

  /**
   * Fetches operating rules governing the service.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Response containing operating rules
   */
  context(service: Service)
  public suspend fun getOperatingRules(): GofsFeedResponse<OperatingRules> =
    getFeedResponse(service.feeds.getValue(FeedType.OperatingRules))

  /**
   * Fetches calendar definitions for service availability.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Response containing calendar definitions
   */
  context(service: Service)
  public suspend fun getCalendars(): GofsFeedResponse<Calendars> =
    getFeedResponse(service.feeds.getValue(FeedType.Calendars))

  /**
   * Fetches fare structures and pricing information.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Response containing fare information
   */
  context(service: Service)
  public suspend fun getFares(): GofsFeedResponse<Fares> =
    getFeedResponse(service.feeds.getValue(FeedType.Fares))

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
   * @return Response containing wait time information
   */
  context(service: Service)
  public suspend fun getWaitTimes(
    pickupLat: Double,
    pickupLon: Double,
    dropOffLat: Double? = null,
    dropOffLon: Double? = null,
    brandIds: List<String> = emptyList(),
  ): GofsFeedResponse<WaitTimes> {
    val url = URLBuilder(service.feeds.getValue(FeedType.WaitTimes))

    require(
      dropOffLat == null && dropOffLon == null || (dropOffLat != null && dropOffLon != null)
    ) {
      "Both dropOffLat and dropOffLon must be provided together"
    }

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

    return getFeedResponse(url.build())
  }

  /**
   * Fetches booking rules for the service.
   *
   * @param [service] The GOFS service containing feed URLs
   * @return Response containing booking rules
   */
  context(service: Service)
  public suspend fun getBookingRules(): GofsFeedResponse<BookingRules> =
    getFeedResponse(service.feeds.getValue(FeedType.BookingRules))

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
   * @return Response containing real-time booking information
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
  ): GofsFeedResponse<RealtimeBookings> {
    val url = URLBuilder(service.feeds.getValue(FeedType.RealtimeBookings))

    require(
      dropOffLat == null && dropOffLon == null || (dropOffLat != null && dropOffLon != null)
    ) {
      "Both dropOffLat and dropOffLon must be provided together"
    }

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

    return getFeedResponse(url.build())
  }

  override fun close(): Unit = httpClient.close()
}
