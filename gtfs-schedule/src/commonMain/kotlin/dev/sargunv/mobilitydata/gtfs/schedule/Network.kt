package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
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
  @SerialName("network_id") public val networkId: Id<Network>,

  /** Name of the network. */
  @SerialName("network_name") public val networkName: String? = null,
)
