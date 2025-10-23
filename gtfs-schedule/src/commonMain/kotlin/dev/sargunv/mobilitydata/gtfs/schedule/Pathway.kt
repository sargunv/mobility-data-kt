package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Pathways linking together locations within stations.
 *
 * This class represents a record in the pathways.txt file.
 */
@Serializable
public data class Pathway(
  /** Uniquely identifies a pathway. */
  @SerialName("pathway_id") public val pathwayId: Id<Pathway>,

  /** Location at which the pathway begins. */
  @SerialName("from_stop_id") public val fromStopId: Id<Stop>,

  /** Location at which the pathway ends. */
  @SerialName("to_stop_id") public val toStopId: Id<Stop>,

  /** Type of pathway between the specified from_stop_id and to_stop_id. */
  @SerialName("pathway_mode") public val pathwayMode: PathwayMode,

  /** Indicates whether the pathway can be used in both directions. */
  @SerialName("is_bidirectional") public val isBidirectional: Boolean,

  /** Horizontal length in meters of the pathway from the origin location to the destination. */
  @SerialName("length") public val length: Double? = null,

  /** Average time in seconds needed to walk through the pathway from the origin to destination. */
  @SerialName("traversal_time") public val traversalTime: Int? = null,

  /** Number of stairs of the pathway. */
  @SerialName("stair_count") public val stairCount: Int? = null,

  /** Maximum slope ratio of the pathway. */
  @SerialName("max_slope") public val maxSlope: Double? = null,

  /** Minimum width of the pathway in meters. */
  @SerialName("min_width") public val minWidth: Double? = null,

  /** Public-facing text from physical signage visible to riders. */
  @SerialName("signposted_as") public val signpostedAs: String? = null,

  /**
   * Text from physical signage visible to riders when the pathway is used from the to_stop to the
   * from_stop.
   */
  @SerialName("reversed_signposted_as") public val reversedSignpostedAs: String? = null,
)

/** Type of pathway. */
@Serializable
@JvmInline
public value class PathwayMode
private constructor(
  /** The integer value representing the pathway mode. */
  public val value: Int
) {
  /** Companion object containing predefined pathway mode constants. */
  public companion object {
    /** Walkway. */
    public val Walkway: PathwayMode = PathwayMode(1)

    /** Stairs. */
    public val Stairs: PathwayMode = PathwayMode(2)

    /** Moving sidewalk/travelator. */
    public val MovingSidewalk: PathwayMode = PathwayMode(3)

    /** Escalator. */
    public val Escalator: PathwayMode = PathwayMode(4)

    /** Elevator. */
    public val Elevator: PathwayMode = PathwayMode(5)

    /** Fare gate (or payment gate). */
    public val FareGate: PathwayMode = PathwayMode(6)

    /** Exit gate. */
    public val ExitGate: PathwayMode = PathwayMode(7)
  }
}
