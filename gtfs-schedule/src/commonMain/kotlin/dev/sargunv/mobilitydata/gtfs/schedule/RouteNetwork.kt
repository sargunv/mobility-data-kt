package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Assigns routes to networks.
 *
 * This class represents a record in the route_networks.txt file.
 */
@Serializable
public data class RouteNetwork(
  /** Identifies a network. */
  @SerialName("network_id") public val networkId: Id<Network>,

  /** Identifies a route. */
  @SerialName("route_id") public val routeId: Id<Route>,
)
