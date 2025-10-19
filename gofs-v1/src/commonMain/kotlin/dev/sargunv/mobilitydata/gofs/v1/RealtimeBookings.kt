package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.CurrencyCode
import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.Uri
import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RealtimeBookings(
  @SerialName("realtime_booking") public val realtimeBookings: List<RealtimeBooking>
) : GofsFeedData, List<RealtimeBooking> by realtimeBookings

@Serializable
public data class RealtimeBooking(
  @SerialName("brand_id") public val brandId: Id<Brand>,
  @SerialName("wait_time") public val waitTime: WholeSeconds,
  @SerialName("travel_time") public val travelTime: WholeSeconds? = null,
  @SerialName("travel_cost") public val travelCost: Double? = null,
  @SerialName("travel_cost_currency") public val travelCostCurrency: CurrencyCode? = null,
  @SerialName("booking_detail") public val bookingDetail: BookingDetail? = null,
)

@Serializable
public data class BookingDetail(
  @SerialName("service_name") public val serviceName: String? = null,
  @SerialName("android_uri") public val androidUri: Uri? = null,
  @SerialName("ios_uri") public val iosUri: Uri? = null,
  @SerialName("web_uri") public val webUri: Url? = null,
  @SerialName("phone_number") public val phoneNumber: String? = null,
)
