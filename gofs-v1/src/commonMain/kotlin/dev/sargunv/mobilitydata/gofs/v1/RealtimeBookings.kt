package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.CurrencyCode
import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.Uri
import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Provides time/cost estimates and realtime booking information for specific locations.
 *
 * This dynamic query can be used in situations where a provider expects to have more fine-grained
 * or accurate booking information for a given trip, compared to what is available via other
 * endpoints.
 */
@Serializable
public data class RealtimeBookings(
  /**
   * An array that contains one object per brand_id. Should be empty if no realtime booking is
   * available for the requested location.
   */
  @SerialName("realtime_booking") public val realtimeBookings: List<RealtimeBooking>
) : GofsFeedData, List<RealtimeBooking> by realtimeBookings

/** Real-time booking information for a specific service brand. */
@Serializable
public data class RealtimeBooking(
  /** ID from a service brand defined in service_brands.json. */
  @SerialName("brand_id") public val brandId: Id<Brand>,
  /** Wait time in seconds the rider will need to wait in the location before pickup. */
  @SerialName("wait_time") public val waitTime: WholeSeconds,
  /**
   * The estimated travel time in seconds from the pickup to dropoff location. Cannot be provided if
   * a drop off location is not provided.
   */
  @SerialName("travel_time") public val travelTime: WholeSeconds? = null,
  /**
   * The estimated fare cost of the trip from the pickup to dropoff location. Cannot be provided if
   * a drop off location is not provided.
   */
  @SerialName("travel_cost") public val travelCost: Double? = null,
  /** Currency of the estimated travel cost. Required if estimated_travel_cost is provided. */
  @SerialName("travel_cost_currency") public val travelCostCurrency: CurrencyCode? = null,
  /** Optionally, an object with real time booking details can be provided. */
  @SerialName("booking_detail") public val bookingDetail: BookingDetail? = null,
)

/**
 * Details about how to book the on-demand service.
 *
 * At least one of androidUri, iosUri, webUri, or phoneNumber needs to be provided.
 */
@Serializable
public data class BookingDetail(
  /**
   * If the service name needs to change due to real time booking changes, service_name can be
   * provided to update the name of the on-demand service system to be displayed to the riders.
   */
  @SerialName("service_name") public val serviceName: String? = null,
  /** Android App Links that can open the booking app on Android. */
  @SerialName("android_uri") public val androidUri: Uri? = null,
  /** iOS Universal Links that can open the booking app on iOS. */
  @SerialName("ios_uri") public val iosUri: Uri? = null,
  /** Web url to browse to in order to make the booking request. */
  @SerialName("web_uri") public val webUri: Url? = null,
  /** Phone number to call to make the booking request. */
  @SerialName("phone_number") public val phoneNumber: String? = null,
)
