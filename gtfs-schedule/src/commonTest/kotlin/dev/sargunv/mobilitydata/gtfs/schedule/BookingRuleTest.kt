package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

private val csvContent = // language=CSV
  """
  booking_rule_id,booking_type,prior_notice_duration_min,prior_notice_duration_max,prior_notice_last_day,prior_notice_last_time,prior_notice_start_day,prior_notice_start_time,prior_notice_service_id,message,pickup_message,drop_off_message,phone_number,info_url,booking_url
  same_day_booking,1,60,,,,,,,,Call us to arrange pickup,Call us to arrange drop off,555-1234,https://example.com/info,https://example.com/book
  real_time_booking,0,,,,,,,,,,,555-5678,https://example.com/realtime,
  prior_days_booking,2,120,10080,1,18:00:00,7,08:00:00,weekday_service,Book in advance,,,555-9999,,https://example.com/advance
  """
    .trimIndent()

private val expected =
  listOf(
    BookingRule(
      bookingRuleId = "same_day_booking",
      bookingType = BookingType.SameDay,
      priorNoticeDurationMin = 1.hours,
      priorNoticeDurationMax = null,
      priorNoticeLastDay = null,
      priorNoticeLastTime = null,
      priorNoticeStartDay = null,
      priorNoticeStartTime = null,
      priorNoticeServiceId = null,
      message = null,
      pickupMessage = "Call us to arrange pickup",
      dropOffMessage = "Call us to arrange drop off",
      phoneNumber = "555-1234",
      infoUrl = "https://example.com/info",
      bookingUrl = "https://example.com/book",
    ),
    BookingRule(
      bookingRuleId = "real_time_booking",
      bookingType = BookingType.RealTime,
      priorNoticeDurationMin = null,
      priorNoticeDurationMax = null,
      priorNoticeLastDay = null,
      priorNoticeLastTime = null,
      priorNoticeStartDay = null,
      priorNoticeStartTime = null,
      priorNoticeServiceId = null,
      message = null,
      pickupMessage = null,
      dropOffMessage = null,
      phoneNumber = "555-5678",
      infoUrl = "https://example.com/realtime",
      bookingUrl = null,
    ),
    BookingRule(
      bookingRuleId = "prior_days_booking",
      bookingType = BookingType.PriorDays,
      priorNoticeDurationMin = 2.hours,
      priorNoticeDurationMax = 7.days,
      priorNoticeLastDay = 1,
      priorNoticeLastTime = ServiceTime(18, 0, 0),
      priorNoticeStartDay = 7,
      priorNoticeStartTime = ServiceTime(8, 0, 0),
      priorNoticeServiceId = "weekday_service",
      message = "Book in advance",
      pickupMessage = null,
      dropOffMessage = null,
      phoneNumber = "555-9999",
      infoUrl = null,
      bookingUrl = "https://example.com/advance",
    ),
  )

class BookingRuleTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<BookingRule>(csvContent)
    assertEquals(expected, decoded)
  }
}
