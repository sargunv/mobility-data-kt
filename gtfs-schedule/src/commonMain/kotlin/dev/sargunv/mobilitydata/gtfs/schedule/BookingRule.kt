package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.ServiceTime
import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.WholeMinutes
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Booking rules for rider-requested services.
 *
 * This class represents a record in the booking_rules.txt file.
 */
@Serializable
public data class BookingRule(
  /** Identifies the booking rule. */
  @SerialName("booking_rule_id") public val bookingRuleId: String,

  /** Indicates when booking can be made. */
  @SerialName("booking_type") public val bookingType: BookingType,

  /** Minimum number of minutes before travel to make the request. */
  @SerialName("prior_notice_duration_min") public val priorNoticeDurationMin: WholeMinutes? = null,

  /** Maximum number of minutes before travel to make the request. */
  @SerialName("prior_notice_duration_max") public val priorNoticeDurationMax: WholeMinutes? = null,

  /** Latest time on the day prior to travel to make the booking request. */
  @SerialName("prior_notice_last_day") public val priorNoticeLastDay: Int? = null,

  /** Latest time on the service day to make the booking request. */
  @SerialName("prior_notice_last_time") public val priorNoticeLastTime: ServiceTime? = null,

  /** Latest time on the day prior to the service day to make the booking request. */
  @SerialName("prior_notice_start_day") public val priorNoticeStartDay: Int? = null,

  /** Earliest time on the service day to make the booking request. */
  @SerialName("prior_notice_start_time") public val priorNoticeStartTime: ServiceTime? = null,

  /** Service days relative to the day of travel to make the request by. */
  @SerialName("prior_notice_service_id") public val priorNoticeServiceId: String? = null,

  /** Message to riders utilizing service at a stop. */
  @SerialName("message") public val message: String? = null,

  /** Message to riders when requesting to be picked up at a stop. */
  @SerialName("pickup_message") public val pickupMessage: String? = null,

  /** Message to riders when requesting to be dropped off at a stop. */
  @SerialName("drop_off_message") public val dropOffMessage: String? = null,

  /** Phone number to call to make the booking request. */
  @SerialName("phone_number") public val phoneNumber: String? = null,

  /** URL providing information about the booking rule. */
  @SerialName("info_url") public val infoUrl: Url? = null,

  /** URL to an online interface or app to make the booking request. */
  @SerialName("booking_url") public val bookingUrl: Url? = null,
)

/** Indicates when booking can be made. */
@Serializable
@JvmInline
public value class BookingType
private constructor(
  /** The integer value representing the booking type. */
  public val value: Int
) {
  /** Companion object containing predefined booking type constants. */
  public companion object {
    /** Real-time booking. */
    public val RealTime: BookingType = BookingType(0)

    /** Up to same-day booking with advance notice. */
    public val SameDay: BookingType = BookingType(1)

    /** Up to prior day(s) booking. */
    public val PriorDays: BookingType = BookingType(2)
  }
}
