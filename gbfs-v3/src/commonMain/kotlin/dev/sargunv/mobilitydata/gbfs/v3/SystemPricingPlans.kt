package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.CurrencyCode
import dev.sargunv.mobilitydata.utils.Decimal
import dev.sargunv.mobilitydata.utils.ExperimentalMobilityDataApi
import dev.sargunv.mobilitydata.utils.LocalizedText
import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.WholeMinutes
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
  public val name: LocalizedText,

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
  public val price: Decimal,

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
  public val description: LocalizedText,

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

  /**
   * The cost, described as a per minute rate, to reserve the vehicle prior to beginning a rental.
   *
   * GBFS 3.1-RC. Charged for each minute of the reservation until the rental is initiated, or until
   * `default_reserve_time` elapses, whichever comes first. MUST NOT be combined in a single pricing
   * plan with [reservationPriceFlatRate].
   */
  @SerialName("reservation_price_per_min")
  @property:ExperimentalMobilityDataApi
  public val reservationPricePerMin: Decimal? = null,

  /**
   * The cost, described as a flat rate, to reserve the vehicle prior to beginning a rental.
   *
   * GBFS 3.1-RC. Charged once to reserve the vehicle for the duration of `default_reserve_time`.
   * MUST NOT be combined in a single pricing plan with [reservationPricePerMin].
   */
  @SerialName("reservation_price_flat_rate")
  @property:ExperimentalMobilityDataApi
  public val reservationPriceFlatRate: Decimal? = null,

  /**
   * A capped fare once a price threshold has been spent within a timeframe.
   *
   * GBFS 3.1-RC. The same fare cap applies to each subsequent timeframe. For example, a fare capped
   * at 15.00 CAD per 12-hour period.
   */
  @SerialName("fare_capping")
  @property:ExperimentalMobilityDataApi
  public val fareCapping: FareCapping? = null,
)

/**
 * A fare cap that applies once a price threshold has been spent within a timeframe.
 *
 * GBFS 3.1-RC. The same cap applies to each subsequent timeframe.
 */
@Serializable
public data class FareCapping
@ExperimentalMobilityDataApi
public constructor(
  /** Amount of time in minutes during which the fare is capped. */
  @property:ExperimentalMobilityDataApi public val duration: WholeMinutes,

  /** The maximum fare threshold for the current timeframe, in the unit specified by `currency`. */
  @property:ExperimentalMobilityDataApi public val price: Decimal,
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
  public val rate: Decimal,

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
