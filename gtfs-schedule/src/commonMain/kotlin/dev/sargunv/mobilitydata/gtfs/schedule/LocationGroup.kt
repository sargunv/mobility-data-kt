package dev.sargunv.mobilitydata.gtfs.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines location groups, which are groups of stops where a rider may request pickup or drop off.
 *
 * This class represents a record in the location_groups.txt file.
 */
@Serializable
public data class LocationGroup(
  /** Identifies a location group. */
  @SerialName("location_group_id") public val locationGroupId: String,

  /** Name of the location group as displayed to the rider. */
  @SerialName("location_group_name") public val locationGroupName: String? = null,
)
