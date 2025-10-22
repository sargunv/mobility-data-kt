package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.Url
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes various rider categories that can be used for fare eligibility.
 *
 * This class represents a record in the rider_categories.txt file.
 */
@Serializable
public data class RiderCategory(
  /** Identifies a rider category. */
  @SerialName("rider_category_id") public val riderCategoryId: Id<RiderCategory>,

  /** Name of the rider category. */
  @SerialName("rider_category_name") public val riderCategoryName: String,

  /** Indicates if this rider category is the default. */
  @SerialName("is_default_fare_category") public val isDefaultFareCategory: IsDefaultFareCategory,

  /** URL describing the eligibility requirements for the rider category. */
  @SerialName("eligibility_url") public val eligibilityUrl: Url? = null,
)

/** Indicates if a rider category is the default. */
@Serializable
@JvmInline
public value class IsDefaultFareCategory
private constructor(
  /** The integer value representing whether this is the default fare category. */
  public val value: Int
) {
  /** Companion object containing predefined constants. */
  public companion object {
    /** Not the default fare category. */
    public val No: IsDefaultFareCategory = IsDefaultFareCategory(0)

    /** Default fare category. */
    public val Yes: IsDefaultFareCategory = IsDefaultFareCategory(1)
  }
}
