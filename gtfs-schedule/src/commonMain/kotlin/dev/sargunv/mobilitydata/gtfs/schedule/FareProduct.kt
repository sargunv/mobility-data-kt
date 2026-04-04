package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.CurrencyCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the range of fares available for purchase by riders.
 *
 * This class represents a record in the fare_products.txt file.
 */
@Serializable
public data class FareProduct(
  /** Identifies a fare product or set of fare products. */
  @SerialName("fare_product_id") public val fareProductId: String,

  /** The name of the fare product as displayed to riders. */
  @SerialName("fare_product_name") public val fareProductName: String? = null,

  /** Identifies a rider category eligible for the fare product. */
  @SerialName("rider_category_id") public val riderCategoryId: String? = null,

  /** Identifies a fare media that can be employed to use the fare product. */
  @SerialName("fare_media_id") public val fareMediaId: String? = null,

  /** The cost of the fare product. May be negative to represent transfer discounts. */
  @SerialName("amount") public val amount: Double,

  /** The currency of the cost of the fare product. */
  @SerialName("currency") public val currency: CurrencyCode,
)
