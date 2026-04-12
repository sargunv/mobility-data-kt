package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.IntBoolean
import dev.sargunv.mobilitydata.utils.Url
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
  @SerialName("rider_category_id") public val riderCategoryId: String,

  /** Name of the rider category. */
  @SerialName("rider_category_name") public val riderCategoryName: String,

  /** Indicates if this rider category is the default. */
  @SerialName("is_default_fare_category") public val isDefaultFareCategory: IntBoolean,

  /** URL describing the eligibility requirements for the rider category. */
  @SerialName("eligibility_url") public val eligibilityUrl: Url? = null,
)
