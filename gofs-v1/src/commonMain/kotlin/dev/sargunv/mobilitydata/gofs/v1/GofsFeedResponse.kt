package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.EpochSeconds
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlin.time.ExperimentalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Common header structure for all GOFS feed responses.
 *
 * Every JSON file in GOFS contains this common header information at the top level of the JSON
 * response object.
 */
@Serializable
@OptIn(ExperimentalTime::class)
public data class GofsFeedResponse<T : GofsFeedData>(
  /**
   * Indicates the last time data in the feed was updated. This timestamp represents the publisher's
   * knowledge of the current state of the system at this point in time.
   */
  @SerialName("last_updated") public val lastUpdated: EpochSeconds,
  /**
   * Number of seconds before the data in the feed will be updated again. If the data should always
   * be refreshed, the value should be 0.
   */
  public val ttl: WholeSeconds,
  /** GOFS version number to which the feed conforms, according to the versioning framework. */
  public val version: String,
  /** Response data in the form of name:value pairs. */
  public val data: T,
)

/** Marker interface for GOFS feed data types. */
public sealed interface GofsFeedData
