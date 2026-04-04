package dev.sargunv.mobilitydata.gtfs.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes network IDs.
 *
 * This class represents a record in the networks.txt file.
 */
@Serializable
public data class Network(
  /** Identifies a network. */
  @SerialName("network_id") public val networkId: String,

  /** Name of the network. */
  @SerialName("network_name") public val networkName: String? = null,
)
