package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines area IDs.
 *
 * This class represents a record in the areas.txt file.
 */
@Serializable
public data class Area(
  /** Identifies an area. */
  @SerialName("area_id") public val areaId: Id<Area>,

  /** Name of the area. */
  @SerialName("area_name") public val areaName: String? = null,
)
