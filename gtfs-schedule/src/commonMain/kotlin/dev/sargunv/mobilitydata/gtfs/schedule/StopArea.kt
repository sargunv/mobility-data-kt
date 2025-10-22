package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Assigns stops to areas.
 *
 * This class represents a record in the stop_areas.txt file.
 */
@Serializable
public data class StopArea(
  /** Identifies an area. */
  @SerialName("area_id") public val areaId: Id<Area>,

  /** Identifies a stop. */
  @SerialName("stop_id") public val stopId: Id<Stop>,
)
