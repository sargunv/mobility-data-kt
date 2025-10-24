package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.IntBoolean
import dev.sargunv.mobilitydata.utils.ServiceTime
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Headway (time between trips) for routes with variable frequency of service.
 *
 * This class represents a record in the frequencies.txt file.
 */
@Serializable
public data class Frequency(
  /** Identifies a trip to which the specified headway of service applies. */
  @SerialName("trip_id") public val tripId: Id<Trip>,

  /** Time at which the first vehicle departs from the first stop of the trip. */
  @SerialName("start_time") public val startTime: ServiceTime,

  /**
   * Time at which service changes to a different headway (or ceases) at the first stop in the trip.
   */
  @SerialName("end_time") public val endTime: ServiceTime,

  /** Time between departures from the same stop (headway) for the trip. */
  @SerialName("headway_secs") public val headwaySecs: WholeSeconds,

  /**
   * Indicates the type of service for a trip. When false (0), frequency-based trips are not exactly
   * scheduled. When true (1), schedule-based trips with exact times. Defaults to false when null or
   * empty.
   */
  @SerialName("exact_times") public val exactTimes: IntBoolean? = null,
)
