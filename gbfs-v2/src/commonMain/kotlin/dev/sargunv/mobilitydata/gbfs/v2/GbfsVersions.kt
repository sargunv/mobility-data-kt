package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.Serializable

/**
 * Lists all available versions of a GBFS feed.
 *
 * Each expression of a GBFS feed describes all of the versions that are available.
 */
@Serializable
public data class Versions(
  /**
   * Contains one object for each of the available versions of a feed.
   *
   * The array MUST be sorted by increasing MAJOR and MINOR version number.
   */
  public val versions: List<VersionInfo>
) : GbfsFeedData, List<VersionInfo> by versions

/** Information about a specific version of a GBFS feed. */
@Serializable
public data class VersionInfo(
  /** The semantic version of the feed in the form X.Y. */
  public val version: String,

  /** URL of the corresponding gbfs.json endpoint. */
  public val url: Url,
)
