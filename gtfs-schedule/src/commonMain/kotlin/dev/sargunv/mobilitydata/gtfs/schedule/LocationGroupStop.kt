package dev.sargunv.mobilitydata.gtfs.schedule

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
  @SerialName("location_group_id") public val locationGroupId: String,

  /** Identifies a stop. */
  @SerialName("stop_id") public val stopId: String,
)
