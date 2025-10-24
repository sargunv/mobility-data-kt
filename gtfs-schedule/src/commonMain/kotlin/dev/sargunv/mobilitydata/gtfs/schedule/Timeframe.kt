package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes fares that can vary based on the time of day, the day of the week, or a particular day
 * in the year.
 *
 * This class represents a record in the timeframes.txt file.
 */
@Serializable
public data class Timeframe(
  /** Identifies a timeframe or set of timeframes. */
  @SerialName("timeframe_group_id") public val timeframeGroupId: String,

  /** Defines the beginning of a timeframe. An empty value is considered 00:00:00. */
  @SerialName("start_time") public val startTime: ServiceTime? = null,

  /** Defines the end of a timeframe. An empty value is considered 24:00:00. */
  @SerialName("end_time") public val endTime: ServiceTime? = null,

  /** Identifies a set of dates that a timeframe is in effect. */
  @SerialName("service_id") public val serviceId: String,
)
