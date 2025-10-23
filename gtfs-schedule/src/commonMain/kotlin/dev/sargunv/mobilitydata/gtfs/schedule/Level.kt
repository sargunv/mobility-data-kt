package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Levels in a station.
 *
 * This class represents a record in the levels.txt file.
 */
@Serializable
public data class Level(
  /** Uniquely identifies a level in a station. */
  @SerialName("level_id") public val levelId: Id<Level>,

  /** Numeric index of the level relative to ground level. */
  @SerialName("level_index") public val levelIndex: Double,

  /** Name of the level. */
  @SerialName("level_name") public val levelName: String? = null,
)
