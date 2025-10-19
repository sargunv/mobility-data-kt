package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.gofs.v1.serialization.BookingTypeSerializer
import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.ServiceTime
import dev.sargunv.mobilitydata.utils.WholeMinutes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class BookingRules(
  @SerialName("booking_rules") public val bookingRules: List<BookingRule>
) : GofsFeedData, List<BookingRule> by bookingRules

@Serializable
public data class BookingRule(
  @SerialName("from_zone_ids") public val fromZoneIds: List<Id<Zone>>,
  @SerialName("to_zone_ids") public val toZoneIds: List<Id<Zone>>?,
  @SerialName("booking_type") public val bookingType: BookingType,
  @SerialName("prior_notice_duration_min") public val priorNoticeDurationMin: WholeMinutes? = null,
  @SerialName("prior_notice_duration_max") public val priorNoticeDurationMax: WholeMinutes? = null,
  @SerialName("prior_notice_last_day") public val priorNoticeLastDay: Int? = null,
  @SerialName("prior_notice_last_time") public val priorNoticeLastTime: ServiceTime? = null,
  @SerialName("prior_notice_start_day") public val priorNoticeStartDay: Int? = null,
  @SerialName("prior_notice_start_time") public val priorNoticeStartTime: ServiceTime? = null,
  @SerialName("prior_notice_calendar_id") public val priorNoticeCalendarId: Id<Calendar>? = null,
  public val message: String? = null,
  @SerialName("pickup_message") public val pickupMessage: String? = null,
  @SerialName("drop_off_message") public val dropOffMessage: String? = null,
  @SerialName("phone_number") public val phoneNumber: String? = null,
  @SerialName("info_url") public val infoUrl: String? = null,
  @SerialName("booking_url") public val bookingUrl: String? = null,
)

@Serializable(with = BookingTypeSerializer::class)
public enum class BookingType(public val value: Int) {
  RealTime(0),
  AdvanceSameDay(1),
  AdvancePriorDay(2),
}
