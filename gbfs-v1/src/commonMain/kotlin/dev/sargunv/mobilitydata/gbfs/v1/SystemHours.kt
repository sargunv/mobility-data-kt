package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.AbbreviatedWeekday
import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Describes hours and days of operation when vehicles are available for rental. */
@Serializable
public data class SystemHours(
  /**
   * Array of objects defining hours for every day of the week.
   *
   * The array MUST contain a minimum of one object identifying hours for every day of the week or a
   * maximum of two for each day of the week (one for each user type).
   */
  @SerialName("rental_hours") public val rentalHours: List<SystemHoursEntry>
) : GbfsFeedData, List<SystemHoursEntry> by rentalHours

/** Hours of operation for specific user types and days of the week. */
@Serializable
public data class SystemHoursEntry(
  /**
   * An array of member and/or nonmember value(s).
   *
   * This indicates that this set of rental hours applies to either members or non-members only.
   */
  @SerialName("user_types") public val userTypes: List<UserType>,

  /**
   * An array of abbreviations (first 3 letters) of English names of the days of the week for which
   * this object applies.
   *
   * Rental hours MUST NOT be defined more than once for each day and user type.
   */
  public val days: List<AbbreviatedWeekday>,

  /**
   * Start time for the hours of operation of the system in the time zone indicated in
   * system_information.json.
   */
  @SerialName("start_time") public val startTime: ServiceTime,

  /**
   * End time for the hours of operation of the system in the time zone indicated in
   * system_information.json.
   */
  @SerialName("end_time") public val endTime: ServiceTime,
)

/** Type of user (member or non-member). */
@Serializable
public enum class UserType {
  /** Member user type. */
  @SerialName("member") Member,

  /** Non-member user type. */
  @SerialName("nonmember") NonMember,
}
