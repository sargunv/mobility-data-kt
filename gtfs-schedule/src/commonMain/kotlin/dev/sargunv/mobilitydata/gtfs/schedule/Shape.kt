package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Rules for mapping vehicle travel paths, sometimes referred to as route alignments.
 *
 * This class represents a record in the shapes.txt file.
 */
@Serializable
public data class Shape(
  /** Identifies a shape. */
  @SerialName("shape_id") public val shapeId: Id<Shape>,

  /** Latitude of a shape point. */
  @SerialName("shape_pt_lat") public val shapePointLatitude: Double,

  /** Longitude of a shape point. */
  @SerialName("shape_pt_lon") public val shapePointLongitude: Double,

  /** Sequence in which the shape points connect to form the shape. */
  @SerialName("shape_pt_sequence") public val shapePointSequence: Int,

  /**
   * Actual distance traveled along the shape from the first shape point to the point specified in
   * this record.
   */
  @SerialName("shape_dist_traveled") public val shapeDistTraveled: Double? = null,
)
