package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.CurrencyCode
import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines fare calculations for a system.
 *
 * This file defines static fare rules for a system.
 */
@Serializable
public data class Fares(
  /** Array that contains one object per fare definition. */
  public val fares: List<Fare>
) : GofsFeedData, List<Fare> by fares

/** A fare definition with pricing rules. */
@Serializable
public data class Fare(
  /** Unique identifier of the fare. */
  @SerialName("fare_id") public val fareId: Id<Fare>,
  /** The currency of the fare. */
  public val currency: CurrencyCode,
  /**
   * Array of fare objects defining the price of the service per kilometer. Total cost per rider is
   * the base cost defined in rider, plus the addition of all segments in kilometer, minute,
   * active_minute, and idle_minute.
   */
  public val kilometer: List<FareEntry>? = null,
  /**
   * Array of fare objects defining the price of the service per minute, regardless of whether the
   * vehicle is moving or not. Total cost per rider is the base cost defined in rider, plus the
   * addition of all relevant segments in kilometer, minute, active_minute, and idle_minute.
   */
  public val minute: List<FareEntry>? = null,
  /**
   * Array of fare objects defining the price of the service per minute, while the vehicle is
   * actively moving. Total cost per rider is the base cost defined in rider, plus the addition of
   * all relevant segments in kilometer, minute, active_minute, and idle_minute.
   */
  @SerialName("active_minute") public val activeMinute: List<FareEntry>? = null,
  /**
   * Array of fare objects defining the price of the service per minute, while the vehicle is
   * stopped or not moving. Total cost per rider is the base cost defined in rider, plus the
   * addition of all relevant segments in kilometer, minute, active_minute, and idle_minute.
   */
  @SerialName("idle_minute") public val idleMinute: List<FareEntry>? = null,
  /** Array of fare objects defining the base cost per rider. */
  public val rider: List<FareEntry>? = null,
  /** Array of fare objects defining the cost of luggage as a surcharge per piece of luggage. */
  public val luggage: List<FareEntry>? = null,
)

/** A single fare entry defining pricing for a specific segment. */
@Serializable
public data class FareEntry(
  /**
   * Interval, in units of the parent key, at which the amount of the row is applied, from start to
   * end.
   */
  public val interval: Double? = null,
  /**
   * The value, in units of the parent key, at which the amount defined in the object starts being
   * charged.
   */
  public val start: Int? = null,
  /**
   * The value, in units of the parent key, at which the amount defined in the object stops being
   * charged.
   */
  public val end: Int? = null,
  /** The fare cost per each unit of the parent key. */
  public val amount: Double? = null,
)
