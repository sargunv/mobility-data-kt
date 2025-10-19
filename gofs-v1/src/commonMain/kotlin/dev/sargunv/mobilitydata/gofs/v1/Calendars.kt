package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.AbbreviatedWeekday
import dev.sargunv.mobilitydata.utils.BasicLocalDate
import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Calendars(public val calendars: List<Calendar>) :
  GofsFeedData, List<Calendar> by calendars

@Serializable
public data class Calendar(
  @SerialName("calendar_id") public val calendarId: Id<Calendar>,
  public val days: List<AbbreviatedWeekday>? = null,
  @SerialName("start_date") public val startDate: BasicLocalDate,
  @SerialName("end_date") public val endDate: BasicLocalDate,
  @SerialName("excepted_dates") public val exceptedDates: List<BasicLocalDate>? = null,
)
