package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.utils.CurrencyCode
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
  @SerialName("plan_id") public val planId: Id<PricingPlan>,

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

  /**
   * Fare price, in the unit specified by currency.
   *
   * In case of non-rate price, this field is the total price. In case of rate price, this field is
   * the base price that is charged only once per trip (typically the price for unlocking) in
   * addition to per_km_pricing and/or per_min_pricing.
   */
  public val price: Double,

  /**
   * Will additional tax be added to the base price?
   * - `true` - Yes.
   * - `false` - No.
   *
   * False MAY be used to indicate that tax is not charged or that tax is included in the base
   * price.
   */
  @SerialName("is_taxable") public val isTaxable: Boolean,

  /**
   * Customer-readable description of the pricing plan.
   *
   * This SHOULD include the duration, price, conditions, etc. that the publisher would like users
   * to see.
   */
  public val description: String,

  /**
   * Array of segments when the price is a function of distance traveled, displayed in kilometers.
   *
   * Total cost is the addition of price and all segments in per_km_pricing and per_min_pricing. If
   * this array is not provided, there are no variable costs based on distance.
   */
  @SerialName("per_km_pricing") public val perKmPricing: List<PricingInterval>? = null,

  /**
   * Array of segments when the price is a function of time traveled, displayed in minutes.
   *
   * Total cost is the addition of price and all segments in per_km_pricing and per_min_pricing. If
   * this array is not provided, there are no variable costs based on time.
   */
  @SerialName("per_min_pricing") public val perMinPricing: List<PricingInterval>? = null,

  /**
   * Is there currently an increase in price in response to increased demand in this pricing plan?
   * - `true` - Surge pricing is in effect.
   * - `false` - Surge pricing is not in effect.
   *
   * If this field is empty, it means there is no surge pricing in effect.
   */
  @SerialName("surge_pricing") public val surgePricing: Boolean? = null,
)

/** A pricing interval defining a rate that is charged over a specific range of distance or time. */
@Serializable
public data class PricingInterval(
  /**
   * The unit (kilometer or minute) at which this segment rate starts being charged (inclusive).
   *
   * REQUIRED if per_km_pricing or per_min_pricing is defined.
   */
  public val start: Int,

  /**
   * Rate that is charged for each unit interval after the start.
   *
   * Can be a negative number, which indicates that the traveler will receive a discount. REQUIRED
   * if per_km_pricing or per_min_pricing is defined.
   */
  public val rate: Double,

  /**
   * Interval in units at which the rate of this segment is either reapplied indefinitely, or if
   * defined, up until (but not including) end.
   *
   * An interval of 0 indicates the rate is only charged once. REQUIRED if per_km_pricing or
   * per_min_pricing is defined.
   */
  public val interval: Int,

  /**
   * The unit (kilometer or minute) at which the rate will no longer apply (exclusive).
   *
   * If this field is empty, the price issued for this segment is charged until the trip ends, in
   * addition to the cost of any subsequent segments.
   */
  public val end: Int? = null,
)
