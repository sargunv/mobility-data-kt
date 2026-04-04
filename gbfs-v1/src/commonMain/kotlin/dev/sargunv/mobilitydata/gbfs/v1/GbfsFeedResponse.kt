package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.EpochSeconds
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlin.time.ExperimentalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Standard GBFS feed response wrapper.
 *
 * All GBFS feeds are wrapped in this structure that provides metadata about the feed.
 */
@Serializable
@OptIn(ExperimentalTime::class)
public data class GbfsFeedResponse<T : GbfsFeedData>(
  /** POSIX timestamp indicating the last time the data in this feed was updated. */
  @SerialName("last_updated") public val lastUpdated: EpochSeconds,

  /**
   * Number of seconds representing how long before the data in this feed will be updated again.
   *
   * Represents the minimum amount of time the client should wait before polling the feed again.
   */
  public val ttl: WholeSeconds,

  /** GBFS version number to which the feed conforms, according to the versioning framework. */
  public val version: String? = null,

  /** Response data in the form of the specific feed type being accessed. */
  public val data: T,
)

/** A possible value of [GbfsFeedResponse.data]. */
public sealed interface GbfsFeedData
