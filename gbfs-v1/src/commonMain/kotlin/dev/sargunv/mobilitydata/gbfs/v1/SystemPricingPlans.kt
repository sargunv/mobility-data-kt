package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.CurrencyCode
import dev.sargunv.mobilitydata.utils.IntBoolean
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Describes pricing for the system. */
@Serializable
public data class SystemPricingPlans(
  /** Array of pricing plans available in the system. */
  public val plans: List<PricingPlan>
) : GbfsFeedData, List<PricingPlan> by plans

/** A pricing plan for renting vehicles. */
@Serializable
public data class PricingPlan(
  /** Identifier for a pricing plan in the system. */
  @SerialName("plan_id") public val planId: String,

  /** URL where the customer can learn more about this pricing plan. */
  public val url: Url? = null,

  /** Name of this pricing plan. */
  public val name: String,

  /**
   * Currency used to pay the fare.
   *
   * This pricing is in ISO 4217 code (for example, CAD for Canadian dollars, EUR for euros, or JPY
   * for Japanese yen).
   */
  public val currency: CurrencyCode,

  /** Fare price, in the unit specified by currency. */
  public val price: Double,

  /**
   * Will additional tax be added to the base price?
   * - `true` - Yes.
   * - `false` - No.
   *
   * False MAY be used to indicate that tax is not charged or that tax is included in the base
   * price.
   */
  @SerialName("is_taxable") public val isTaxable: IntBoolean,

  /**
   * Customer-readable description of the pricing plan.
   *
   * This SHOULD include the duration, price, conditions, etc. that the publisher would like users
   * to see.
   */
  public val description: String,
)
