package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines when two consecutive legs with a transfer should be considered as a single effective fare
 * leg.
 *
 * This class represents a record in the fare_leg_join_rules.txt file.
 */
@Serializable
public data class FareLegJoinRule(
  /** Matches a pre-transfer leg that uses the specified route network. */
  @SerialName("from_network_id") public val fromNetworkId: Id<Network>,

  /** Matches a post-transfer leg that uses the specified route network. */
  @SerialName("to_network_id") public val toNetworkId: Id<Network>,

  /** Matches a pre-transfer leg that ends at the specified stop or station. */
  @SerialName("from_stop_id") public val fromStopId: Id<Stop>? = null,

  /** Matches a post-transfer leg that starts at the specified stop or station. */
  @SerialName("to_stop_id") public val toStopId: Id<Stop>? = null,
)
