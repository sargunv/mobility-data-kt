package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.BasicLocalDate
import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Exceptions for the services defined in the calendar.txt.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#calendar_datestxt)
 */
@Serializable
public data class CalendarDate(
  /** Identifies a set of dates when a service exception occurs. */
  @SerialName("service_id") public val serviceId: Id<ServiceCalendar>,

  /** Date when service exception occurs. */
  @SerialName("date") public val date: BasicLocalDate,

  /** Indicates whether service is available on the date specified. */
  @SerialName("exception_type") public val exceptionType: ExceptionType,
)

/**
 * Indicates whether service is available on the date specified.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#calendar_datestxt)
 */
@Serializable
public enum class ExceptionType {
  /** Service has been added for the specified date. */
  @SerialName("1") SERVICE_ADDED,

  /** Service has been removed for the specified date. */
  @SerialName("2") SERVICE_REMOVED,
}
