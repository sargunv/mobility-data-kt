package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.AbbreviatedWeekday
import dev.sargunv.mobilitydata.utils.BasicLocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines the dates and days when on-demand services are available to the riders.
 *
 * This file defines the dates and days when on-demand services are available to the riders.
 */
@Serializable
public data class Calendars(
  /** Array that contains one object per calendar. */
  public val calendars: List<Calendar>
) : GofsFeedData, List<Calendar> by calendars

/** A calendar defining when on-demand services are available. */
@Serializable
public data class Calendar(
  /** Unique identifier of the calendar. */
  @SerialName("calendar_id") public val calendarId: String,
  /**
   * Array of abbreviations (first 3 letters) of English names of the days of the week for which
   * this object applies (e.g. ["mon", "tue", "wed", "thu", "fri", "sat", "sun"]). If days are not
   * defined, it is assumed that the on-demand service is available all days of the week.
   */
  public val days: List<AbbreviatedWeekday>? = null,
  /** Start date for the calendar. */
  @SerialName("start_date") public val startDate: BasicLocalDate,
  /** End date for the calendar. The end date must be subsequent or equal to the start date. */
  @SerialName("end_date") public val endDate: BasicLocalDate,
  /** Array of dates removing service availability from the calendar. */
  @SerialName("excepted_dates") public val exceptedDates: List<BasicLocalDate>? = null,
)
