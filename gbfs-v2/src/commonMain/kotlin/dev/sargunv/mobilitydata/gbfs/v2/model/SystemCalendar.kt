package dev.sargunv.mobilitydata.gbfs.v2.model

import dev.sargunv.mobilitydata.gbfs.v2.serialization.MonthNumberSerializer
import kotlinx.datetime.Month
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the operating calendar for a system.
 *
 * This OPTIONAL file SHOULD be published by systems that operate seasonally or do not offer
 * continuous year-round service.
 */
@Serializable
public data class SystemCalendar(
  /**
   * Array of objects describing the system operational calendar.
   *
   * A minimum of one calendar object is REQUIRED. If start and end dates are the same every year,
   * then start_year and end_year SHOULD be omitted.
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
  @SerialName("start_month")
  public val startMonth: @Serializable(with = MonthNumberSerializer::class) Month,

  /** Starting date for the system operations (1-31). */
  @SerialName("start_day") public val startDay: Int,

  /**
   * Ending year for the system operations.
   *
   * If start and end dates are the same every year, this SHOULD be omitted.
   */
  @SerialName("end_year") public val endYear: Int? = null,

  /** Ending month for the system operations (1-12). */
  @SerialName("end_month")
  public val endMonth: @Serializable(with = MonthNumberSerializer::class) Month,

  /** Ending date for the system operations (1-31). */
  @SerialName("end_day") public val endDay: Int,
)
