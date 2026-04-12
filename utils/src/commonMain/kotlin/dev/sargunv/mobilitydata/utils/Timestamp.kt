package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.TimestampSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.parse
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

/**
 * Timestamp fields represented as strings in
 * [RFC3339 format](https://www.rfc-editor.org/rfc/rfc3339).
 *
 * This type combines an instant in time with its UTC offset, allowing for proper serialization and
 * deserialization of timestamp values that include timezone information.
 */
@ExperimentalTime
@Serializable(with = TimestampSerializer::class)
public data class Timestamp
internal constructor(
  /** The instant in time represented by this timestamp. */
  public val instant: Instant,
  /** The UTC offset from UTC for this timestamp. */
  public val offset: UtcOffset,
) : Comparable<Timestamp> {
  override fun compareTo(other: Timestamp): Int = this.instant.compareTo(other.instant)

  /** Companion object providing factory methods for [Timestamp]. */
  public companion object {
    /**
     * Parses a timestamp string into a [Timestamp] instance.
     *
     * @param input The timestamp string to parse
     * @param format The datetime format to use for parsing (defaults to ISO date-time offset
     *   format)
     * @return A [Timestamp] instance representing the parsed input
     */
    public fun parse(
      input: String,
      format: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET,
    ): Timestamp {
      val components = DateTimeComponents.Companion.parse(input, format)
      val offset = components.toUtcOffset()
      return Timestamp(components.toLocalDateTime().toInstant(offset), offset)
    }
  }
}
