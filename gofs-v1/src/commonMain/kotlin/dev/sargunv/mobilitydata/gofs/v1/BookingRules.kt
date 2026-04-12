package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.ServiceTime
import dev.sargunv.mobilitydata.utils.WholeMinutes
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines rules about how to book a ride.
 *
 * If available, users can either book a ride in real-time, for the same day with an advance notice
 * or for a future day.
 */
@Serializable
public data class BookingRules(
  /** Array that contains one object per booking rule. */
  @SerialName("booking_rules") public val bookingRules: List<BookingRule>
) : GofsFeedData, List<BookingRule> by bookingRules

/** A single booking rule defining how to book a ride for specific zones. */
@Serializable
public data class BookingRule(
  /**
   * One or many zone IDs from zones.json that cover the area of the pickup location for this
   * booking rule.
   */
  @SerialName("from_zone_ids") public val fromZoneIds: List<String>,
  /**
   * One or many zone IDs from zones.json that cover the area of the destination for this booking
   * rule.
   */
  @SerialName("to_zone_ids") public val toZoneIds: List<String>?,
  /** Indicates how far in advance booking can be made. */
  @SerialName("booking_type") public val bookingType: BookingType,
  /**
   * Minimum number of minutes before travel to make the request. Required for same-day booking with
   * advance notice (booking_type=1). Forbidden otherwise.
   */
  @SerialName("prior_notice_duration_min") public val priorNoticeDurationMin: WholeMinutes? = null,
  /**
   * Maximum number of minutes before travel to make the booking request. Optional for same-day
   * booking with advance notice (booking_type=1). Forbidden otherwise.
   */
  @SerialName("prior_notice_duration_max") public val priorNoticeDurationMax: WholeMinutes? = null,
  /**
   * Last day before travel to make the booking request (e.g. "Ride must be booked 1 day in advance
   * before 5PM" will be encoded as prior_notice_last_day=1). Required for prior day(s) booking
   * (booking_type=2). Forbidden otherwise.
   */
  @SerialName("prior_notice_last_day") public val priorNoticeLastDay: Int? = null,
  /**
   * Last time on the last day before travel to make the booking request (e.g. "Ride must be booked
   * 1 day in advance before 5PM" will be encoded as prior_notice_last_time=17:00:00). Required if
   * prior_notice_last_day is defined. Forbidden otherwise.
   */
  @SerialName("prior_notice_last_time") public val priorNoticeLastTime: ServiceTime? = null,
  /**
   * Earliest day before travel to make the booking request (e.g. "Ride can be booked at the
   * earliest one week in advance at midnight" will be encoded as prior_notice_start_day=7).
   * Forbidden for real-time booking (booking_type=0). Forbidden for same-day booking with advance
   * notice (booking_type=1) if prior_notice_duration_max is defined. Optional otherwise.
   */
  @SerialName("prior_notice_start_day") public val priorNoticeStartDay: Int? = null,
  /**
   * Earliest time on the earliest day before travel to make the booking request (e.g. "Ride can be
   * booked at the earliest one week in advance at midnight" will be encoded as
   * prior_notice_start_time=00:00:00). Forbidden for real-time booking (booking_type=0). Required
   * if prior_notice_start_day is defined. Forbidden otherwise.
   */
  @SerialName("prior_notice_start_time") public val priorNoticeStartTime: ServiceTime? = null,
  /**
   * Indicates the service days on which prior_notice_last_day or prior_notice_start_day are
   * counted. If empty, prior_notice_start_day=2 will be two calendar days in advance. If defined as
   * a calendar_id containing only business days (weekdays without holidays),
   * prior_notice_start_day=2 will be two business days in advance. Optional if booking_type=2.
   * Forbidden otherwise.
   */
  @SerialName("prior_notice_calendar_id") public val priorNoticeCalendarId: String? = null,
  /**
   * Message to riders utilizing service inside a zone when booking on-demand pickup and drop off.
   * Meant to provide minimal information to be transmitted within a user interface about the action
   * a rider must take in order to utilize the service.
   */
  public val message: String? = null,
  /** Functions in the same way as message but used when riders have on-demand pickup only. */
  @SerialName("pickup_message") public val pickupMessage: String? = null,
  /** Functions in the same way as message but used when riders have on-demand drop off only. */
  @SerialName("drop_off_message") public val dropOffMessage: String? = null,
  /** Phone number to call to make the booking request. */
  @SerialName("phone_number") public val phoneNumber: String? = null,
  /** URL providing information about the booking rule. */
  @SerialName("info_url") public val infoUrl: String? = null,
  /** URL to an online interface or app where the booking request can be made. */
  @SerialName("booking_url") public val bookingUrl: String? = null,
)

/**
 * Indicates how far in advance booking can be made.
 *
 * Valid options are:
 * - Real-time booking: To be used with wait_time
 * - Up to same-day booking with advance notice
 * - Up to prior day(s) booking
 */
@Serializable
@JvmInline
public value class BookingType(
  /** The numeric value used in the GOFS feed for this booking type. */
  public val value: Int
) {
  /** Companion object containing predefined booking type constants. */
  public companion object {
    /** Real-time booking. To be used with wait_time. */
    public val RealTime: BookingType = BookingType(0)
    /** Up to same-day booking with advance notice. */
    public val AdvanceSameDay: BookingType = BookingType(1)
    /** Up to prior day(s) booking. */
    public val AdvancePriorDay: BookingType = BookingType(2)
  }
}
