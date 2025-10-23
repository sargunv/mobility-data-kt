package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.ServiceTime
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlin.jvm.JvmInline
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

  /** Indicates the type of service for a trip. */
  @SerialName("exact_times") public val exactTimes: ExactTimes? = null,
)

/** Indicates the type of service for a trip. */
@Serializable
@JvmInline
public value class ExactTimes
private constructor(
  /** The integer value representing the exact times type. */
  public val value: Int
) {
  /** Companion object containing predefined exact times constants. */
  public companion object {
    /**
     * Frequency-based trips. Trips are not scheduled exactly, but the specified headway is
     * maintained.
     */
    public val FrequencyBased: ExactTimes = ExactTimes(0)

    /**
     * Schedule-based trips. Trips follow a fixed schedule with exact departure times that can be
     * derived from start_time and headway_secs.
     */
    public val ScheduleBased: ExactTimes = ExactTimes(1)
  }
}
