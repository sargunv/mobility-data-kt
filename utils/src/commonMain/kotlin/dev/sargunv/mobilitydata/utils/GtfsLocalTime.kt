package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.GtfsLocalTimeSerializer
import kotlinx.serialization.Serializable

/**
 * Time in the HH:MM:SS format (H:MM:SS is also accepted). Represents a wall-clock time shown in the
 * local time of the specified location.
 *
 * Unlike [ServiceTime], values greater than 24:00:00 are forbidden. 24:00:00 itself is allowed and
 * represents the end of the local day.
 */
@Serializable(with = GtfsLocalTimeSerializer::class)
public data class GtfsLocalTime(
  /** The hour component (0-24). 24 is valid only as 24:00:00. */
  public val hours: Int,
  /** The minute component (0-59). */
  public val minutes: Int,
  /** The second component (0-59). */
  public val seconds: Int,
) : Comparable<GtfsLocalTime> {
  init {
    require(hours in 0..24) { "Hours must be in the range 0..24, but was $hours." }
    require(minutes in 0..59) { "Minutes must be in the range 0..59, but was $minutes." }
    require(seconds in 0..59) { "Seconds must be in the range 0..59, but was $seconds." }
    require(hours < 24 || (minutes == 0 && seconds == 0)) {
      "Local time must not be greater than 24:00:00, but was $hours:$minutes:$seconds."
    }
  }

  override fun compareTo(other: GtfsLocalTime): Int =
    when {
      this.hours != other.hours -> this.hours.compareTo(other.hours)
      this.minutes != other.minutes -> this.minutes.compareTo(other.minutes)
      else -> this.seconds.compareTo(other.seconds)
    }
}
