package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.AbbreviatedWeekdaySerializer
import dev.sargunv.mobilitydata.utils.serialization.EpochSecondsSerializer
import dev.sargunv.mobilitydata.utils.serialization.IsoBasicLocalDateSerializer
import dev.sargunv.mobilitydata.utils.serialization.MonthNumberSerializer
import dev.sargunv.mobilitydata.utils.serialization.RgbColorCodeSerializer
import dev.sargunv.mobilitydata.utils.serialization.RgbColorTripletSerializer
import dev.sargunv.mobilitydata.utils.serialization.ServiceTimeSerializer
import dev.sargunv.mobilitydata.utils.serialization.WholeMinutesSerializer
import dev.sargunv.mobilitydata.utils.serialization.WholeSecondsSerializer
import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.serializers.LocalDateIso8601Serializer
import kotlinx.datetime.serializers.LocalTimeIso8601Serializer
import kotlinx.serialization.Serializable

/**
 * A string that identifies that particular entity of type [T]. An ID:
 * - MUST be unique within like fields (for example, `Id<Zone>` MUST be unique among zones).
 * - Does not have to be globally unique, unless otherwise specified.
 * - MUST NOT contain spaces.
 * - MUST be persistent for a given entity (zone, station, etc) unless specified otherwise for that
 *   entity.
 */
public typealias Id<@Suppress("unused") T> = String

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
public value class RgbColor(public val value: Int) {
  init {
    require(value in 0x000000..0xFFFFFF) { "Color value must be a 6-digit hexadecimal number." }
  }

  public val red: Byte
    get() = (value shr 16).toByte()

  public val green: Byte
    get() = (value shr 8).toByte()

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
  public val hours: Int,
  public val minutes: Int,
  public val seconds: Int,
) : Comparable<ServiceTime> {
  init {
    require(hours >= 0) { "Hours must be non-negative, but was $hours." }
    require(minutes in 0..59) { "Minutes must be in the range 0..59, but was $minutes." }
    require(seconds in 0..59) { "Seconds must be in the range 0..59, but was $seconds." }
  }

  public fun toLocalTime(): LocalTime = LocalTime(hours % 24, minutes, seconds)

  override fun compareTo(other: ServiceTime): Int =
    when {
      this.hours != other.hours -> this.hours.compareTo(other.hours)
      this.minutes != other.minutes -> this.minutes.compareTo(other.minutes)
      else -> this.seconds.compareTo(other.seconds)
    }
}

public fun LocalTime.toServiceTime(dayOffset: Int = 0): ServiceTime {
  val totalHours = hour + dayOffset * 24
  return ServiceTime(totalHours, minute, second)
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
