package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.AbbreviatedWeekdaySerializer
import dev.sargunv.mobilitydata.utils.serialization.EpochSecondsSerializer
import dev.sargunv.mobilitydata.utils.serialization.IntBooleanSerializer
import dev.sargunv.mobilitydata.utils.serialization.IsoBasicLocalDateSerializer
import dev.sargunv.mobilitydata.utils.serialization.LocalizedTextSerializer
import dev.sargunv.mobilitydata.utils.serialization.MonthNumberSerializer
import dev.sargunv.mobilitydata.utils.serialization.RgbColorCodeSerializer
import dev.sargunv.mobilitydata.utils.serialization.RgbColorTripletSerializer
import dev.sargunv.mobilitydata.utils.serialization.ServiceTimeSerializer
import dev.sargunv.mobilitydata.utils.serialization.TimestampSerializer
import dev.sargunv.mobilitydata.utils.serialization.WholeMinutesSerializer
import dev.sargunv.mobilitydata.utils.serialization.WholeSecondsSerializer
import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.atTime
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.parse
import kotlinx.datetime.serializers.LocalDateIso8601Serializer
import kotlinx.datetime.serializers.LocalTimeIso8601Serializer
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

/** A string that identifies that particular entity of type [T]. */
public typealias Id<@Suppress("unused") T> = String

/** A string that identifies that particular entity of type [T1] or [T2]. */
public typealias Id2<@Suppress("unused") T1, @Suppress("unused") T2> = String

/** Currency code following the [ISO 4217 standard](https://en.wikipedia.org/wiki/ISO_4217). */
public typealias CurrencyCode = String

/**
 * Country code following the
 * [ISO 3166-1 alpha-2 notation](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2).
 */
public typealias CountryCode = String

/** An [IETF BCP 47 language code](https://en.wikipedia.org/wiki/IETF_language_tag). */
public typealias LanguageCode = String

/**
 * A fully qualified URI that includes the scheme (for example, `com.example.android://`). Any
 * special characters in the URI MUST be correctly escaped.
 */
public typealias Uri = String

/**
 * A fully qualified URL that includes `http://` or `https://`. Any special characters in the URL
 * MUST be correctly escaped.
 */
public typealias Url = String

/**
 * An integer representing the number of seconds that have elapsed since 00:00:00 UTC on 1 January
 * 1970 (Unix epoch).
 */
@OptIn(ExperimentalTime::class)
public typealias EpochSeconds = @Serializable(with = EpochSecondsSerializer::class) Instant

/**
 * A 24-bit color with eight bits each for red, green, and blue components.
 *
 * @see RgbColorCode
 * @see RgbColorTriplet
 */
@JvmInline
public value class RgbColor(
  /** The 24-bit integer representation of this color (0x000000 to 0xFFFFFF). */
  public val value: Int
) {
  init {
    require(value in 0x000000..0xFFFFFF) { "Color value must be a 6-digit hexadecimal number." }
  }

  /** The red component of this color (0-255). */
  public val red: Byte
    get() = (value shr 16).toByte()

  /** The green component of this color (0-255). */
  public val green: Byte
    get() = (value shr 8).toByte()

  /** The blue component of this color (0-255). */
  public val blue: Byte
    get() = value.toByte()

  public override fun toString(): String = "#${value.toHexString(HexFormat.UpperCase)}"
}

/**
 * An [RgbColor] encoded as a six-digit hexadecimal number. The leading "#" MUST NOT be included.
 * Example: FFFFFF for white or 000000 for black.
 */
public typealias RgbColorTriplet = @Serializable(with = RgbColorTripletSerializer::class) RgbColor

/**
 * An [RgbColor] encoded as a six-digit hexadecimal number. The leading "#" MUST be included.
 * Example: #FFFFFF for white or #000000 for black.
 */
public typealias RgbColorCode = @Serializable(with = RgbColorCodeSerializer::class) RgbColor

/**
 * English name of a color. All words must be in lower case, without special characters, quotation
 * marks, hyphens, underscores, commas, or dots. Spaces are allowed in case of a compound name.
 *
 * Examples:
 * - green
 * - dark blue
 */
public typealias ColorName = String

/** [LocalDate] in the ISO 8601 basic format (YYYYMMDD). */
public typealias BasicLocalDate = @Serializable(with = IsoBasicLocalDateSerializer::class) LocalDate

/** [LocalDate] in the ISO 8601 extended format (YYYY-MM-DD). */
public typealias ExtendedLocalDate =
  @Serializable(with = LocalDateIso8601Serializer::class) LocalDate

/** [LocalTime] in the ISO 8601 extended format (HH:MM:SS). */
public typealias ExtendedLocalTime =
  @Serializable(with = LocalTimeIso8601Serializer::class) LocalTime

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
   * Converts this service time to an [Instant] on the given [serviceDate] in the specified
   * [timezone]. If the service time represents a time after midnight ([hours] > 23), the resulting
   * instant will fall on the next calendar day.
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

/** Duration as an integer of minutes. */
public typealias WholeMinutes = @Serializable(with = WholeMinutesSerializer::class) Duration

/** Duration as an integer of seconds. */
public typealias WholeSeconds = @Serializable(with = WholeSecondsSerializer::class) Duration

/** Month of the year as a number from 1 to 12. */
public typealias MonthNumber = @Serializable(with = MonthNumberSerializer::class) Month

/** Abbreviated (first 3 letters) English name of a day of the week. */
public typealias AbbreviatedWeekday =
  @Serializable(with = AbbreviatedWeekdaySerializer::class) DayOfWeek

/** Boolean value represented as an integer (1 for true, 0 for false). */
public typealias IntBoolean = @Serializable(with = IntBooleanSerializer::class) Boolean

/** Text localized to multiple languages. */
public typealias LocalizedText =
  @Serializable(with = LocalizedTextSerializer::class) Map<LanguageCode, String>

/** [Url] localized to multiple languages. */
public typealias LocalizedUrl =
  @Serializable(with = LocalizedTextSerializer::class) Map<LanguageCode, Url>

/** Opening hours in the [OSM format](https://wiki.openstreetmap.org/wiki/Key:opening_hours). */
public typealias OsmOpeningHours = String

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
      format: DateTimeFormat<DateTimeComponents> = ISO_DATE_TIME_OFFSET,
    ): Timestamp {
      val components = DateTimeComponents.parse(input, format)
      val offset = components.toUtcOffset()
      return Timestamp(components.toLocalDateTime().toInstant(offset), offset)
    }
  }
}
