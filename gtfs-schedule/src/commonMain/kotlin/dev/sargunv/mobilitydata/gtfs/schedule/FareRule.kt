package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Rules to apply fares for itineraries.
 *
 * This class represents a record in the fare_rules.txt file (GTFS-Fares V1).
 *
 * The fare_rules.txt table specifies how fares in fare_attributes.txt apply to an itinerary. Most
 * fare structures use some combination of the following rules:
 * - Fare depends on origin or destination stations.
 * - Fare depends on which zones the itinerary passes through.
 * - Fare depends on which route the itinerary uses.
 */
@Serializable
public data class FareRule(
  /** Identifies a fare class. */
  @SerialName("fare_id") public val fareId: Id<FareAttribute>,

  /** Identifies a route associated with the fare class. */
  @SerialName("route_id") public val routeId: Id<Route>? = null,

  /** Identifies an origin zone. */
  @SerialName("origin_id") public val originId: Id<Zone>? = null,

  /** Identifies a destination zone. */
  @SerialName("destination_id") public val destinationId: Id<Zone>? = null,

  /** Identifies the zones that a rider will enter while using a given fare class. */
  @SerialName("contains_id") public val containsId: Id<Zone>? = null,
)
