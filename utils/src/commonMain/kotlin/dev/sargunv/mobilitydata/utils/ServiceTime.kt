package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.ServiceTimeSerializer
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

/**
 * Time in the HH:MM:SS format (H:MM:SS is also accepted). The time is measured from "noon minus
 * 12h" of the service day (effectively midnight except for days on which daylight savings time
 * changes occur). For times occurring after midnight, enter the time as a value greater than
 * 24:00:00 in HH:MM:SS local time for the day on which the trip schedule begins. Example: 14:30:00
 * for 2:30PM or 25:35:00 for 1:35AM on the next day.
 */
@Serializable(with = ServiceTimeSerializer::class)
public data class ServiceTime(
  /** The hour component (0 or greater, may exceed 23 for times after midnight). */
  public val hours: Int,
  /** The minute component (0-59). */
  public val minutes: Int,
  /** The second component (0-59). */
  public val seconds: Int,
) : Comparable<ServiceTime> {
  init {
    require(hours >= 0) { "Hours must be non-negative, but was $hours." }
    require(minutes in 0..59) { "Minutes must be in the range 0..59, but was $minutes." }
    require(seconds in 0..59) { "Seconds must be in the range 0..59, but was $seconds." }
  }

  /**
   * Converts this service time to an [kotlin.time.Instant] on the given [serviceDate] in the
   * specified [timezone]. If the service time represents a time after midnight ([hours] > 23), the
   * resulting instant will fall on the next calendar day.
   *
   * @param serviceDate The service date to which this service time belongs
   * @param timezone The timezone in which to interpret the service time
   * @return The corresponding instant in time
   */
  @OptIn(ExperimentalTime::class)
  public fun toInstant(serviceDate: LocalDate, timezone: TimeZone): Instant {
    val noonOnServiceDate = serviceDate.atTime(LocalTime(hour = 12, minute = 0)).toInstant(timezone)
    return noonOnServiceDate - 12.hours + hours.hours + minutes.minutes + seconds.seconds
  }

  override fun compareTo(other: ServiceTime): Int =
    when {
      this.hours != other.hours -> this.hours.compareTo(other.hours)
      this.minutes != other.minutes -> this.minutes.compareTo(other.minutes)
      else -> this.seconds.compareTo(other.seconds)
    }
}
