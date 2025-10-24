package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Assigns stops to location groups.
 *
 * This class represents a record in the location_group_stops.txt file.
 */
@Serializable
public data class LocationGroupStop(
  /** Identifies a location group. */
  @SerialName("location_group_id") public val locationGroupId: Id<LocationGroup>,

  /** Identifies a stop. */
  @SerialName("stop_id") public val stopId: Id<Stop>,
)
