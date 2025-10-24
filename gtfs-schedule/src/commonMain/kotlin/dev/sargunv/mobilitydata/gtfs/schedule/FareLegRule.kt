package dev.sargunv.mobilitydata.gtfs.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Fare rules for individual legs of travel.
 *
 * This class represents a record in the fare_leg_rules.txt file.
 */
@Serializable
public data class FareLegRule(
  /** Identifies a group of fare leg rules. */
  @SerialName("leg_group_id") public val legGroupId: String? = null,

  /** Identifies a route network that applies for the fare leg rule. */
  @SerialName("network_id") public val networkId: String? = null,

  /** Identifies a departure area. */
  @SerialName("from_area_id") public val fromAreaId: String? = null,

  /** Identifies an arrival area. */
  @SerialName("to_area_id") public val toAreaId: String? = null,

  /** Defines the timeframe for the fare validation event at the start of the fare leg. */
  @SerialName("from_timeframe_group_id") public val fromTimeframeGroupId: String? = null,

  /** Defines the timeframe for the fare validation event at the end of the fare leg. */
  @SerialName("to_timeframe_group_id") public val toTimeframeGroupId: String? = null,

  /** The fare product required to travel the leg. */
  @SerialName("fare_product_id") public val fareProductId: String,

  /** Defines the order of priority in which matching rules are applied to legs. */
  @SerialName("rule_priority") public val rulePriority: Int? = null,
)
