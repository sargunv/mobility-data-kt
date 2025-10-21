package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.BasicLocalDate
import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.IntBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Service dates specified using a weekly schedule with start and end dates.
 *
 * This file represents the calendar.txt file in GTFS.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#calendartxt)
 */
@Serializable
public data class ServiceCalendar(
  /** Uniquely identifies a set of dates when service is available for one or more routes. */
  @SerialName("service_id") public val serviceId: Id<ServiceCalendar>,

  /** Indicates whether the service operates on all Mondays in the date range. */
  @SerialName("monday") public val monday: IntBoolean,

  /** Indicates whether the service operates on all Tuesdays in the date range. */
  @SerialName("tuesday") public val tuesday: IntBoolean,

  /** Indicates whether the service operates on all Wednesdays in the date range. */
  @SerialName("wednesday") public val wednesday: IntBoolean,

  /** Indicates whether the service operates on all Thursdays in the date range. */
  @SerialName("thursday") public val thursday: IntBoolean,

  /** Indicates whether the service operates on all Fridays in the date range. */
  @SerialName("friday") public val friday: IntBoolean,

  /** Indicates whether the service operates on all Saturdays in the date range. */
  @SerialName("saturday") public val saturday: IntBoolean,

  /** Indicates whether the service operates on all Sundays in the date range. */
  @SerialName("sunday") public val sunday: IntBoolean,

  /** Start service day for the service interval. */
  @SerialName("start_date") public val startDate: BasicLocalDate,

  /** End service day for the service interval. */
  @SerialName("end_date") public val endDate: BasicLocalDate,
)
