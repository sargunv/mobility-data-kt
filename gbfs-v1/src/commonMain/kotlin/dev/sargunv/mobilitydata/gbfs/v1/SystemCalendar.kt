package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.MonthNumber
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Describes the operating calendar for a system. */
@Serializable
public data class SystemCalendar(
  /**
   * Array of objects describing the system operational calendar.
   *
   * A minimum of one calendar object is REQUIRED. If start/end year are omitted, then assume the
   * start and end months do not change from year to year
   */
  public val calendars: List<SystemCalendarEntry>
) : GbfsFeedData

/** A calendar entry defining an operating period for the system. */
@Serializable
public data class SystemCalendarEntry(
  /**
   * Starting year for the system operations.
   *
   * If start and end dates are the same every year, this SHOULD be omitted.
   */
  // Can't use LocalDate because years can be null
  @SerialName("start_year") public val startYear: Int? = null,

  /** Starting month for the system operations (1-12). */
  @SerialName("start_month") public val startMonth: MonthNumber,

  /** Starting date for the system operations (1-31). */
  @SerialName("start_day") public val startDay: Int,

  /** Ending year for the system operations. */
  @SerialName("end_year") public val endYear: Int? = null,

  /** Ending month for the system operations (1-12). */
  @SerialName("end_month") public val endMonth: MonthNumber,

  /** Ending date for the system operations (1-31). */
  @SerialName("end_day") public val endDay: Int,
)
