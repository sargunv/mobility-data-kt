package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.AbbreviatedWeekdaySerializer
import dev.sargunv.mobilitydata.utils.serialization.EpochSecondsSerializer
import dev.sargunv.mobilitydata.utils.serialization.IsoBasicLocalDateSerializer
import dev.sargunv.mobilitydata.utils.serialization.MonthNumberSerializer
import dev.sargunv.mobilitydata.utils.serialization.WholeMinutesSerializer
import dev.sargunv.mobilitydata.utils.serialization.WholeSecondsSerializer
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
 * An integer representing the number of seconds that have elapsed since 00:00:00 UTC on 1 January
 * 1970 (Unix epoch).
 */
@OptIn(ExperimentalTime::class)
public typealias EpochSeconds = @Serializable(with = EpochSecondsSerializer::class) Instant

/** [LocalDate] in the ISO 8601 basic format (YYYYMMDD). */
public typealias BasicLocalDate = @Serializable(with = IsoBasicLocalDateSerializer::class) LocalDate

/** [LocalDate] in the ISO 8601 extended format (YYYY-MM-DD). */
public typealias ExtendedLocalDate =
  @Serializable(with = LocalDateIso8601Serializer::class) LocalDate

/** [LocalTime] in the ISO 8601 extended format (HH:MM:SS). */
public typealias ExtendedLocalTime =
  @Serializable(with = LocalTimeIso8601Serializer::class) LocalTime

/** Duration as an integer of minutes. */
public typealias WholeMinutes = @Serializable(with = WholeMinutesSerializer::class) Duration

/** Duration as an integer of seconds. */
public typealias WholeSeconds = @Serializable(with = WholeSecondsSerializer::class) Duration

/** Month of the year as a number from 1 to 12. */
public typealias MonthNumber = @Serializable(with = MonthNumberSerializer::class) Month

/** Abbreviated (first 3 letters) English name of a day of the week. */
public typealias AbbreviatedWeekday =
  @Serializable(with = AbbreviatedWeekdaySerializer::class) DayOfWeek
