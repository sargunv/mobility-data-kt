package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.Serializable

/**
 * Shows the different versions available for the same GOFS feed.
 *
 * This file should include all the versions available for the same GOFS feed representing the
 * on-demand service system.
 */
@Serializable
public data class GofsVersions(
  /**
   * Contains one object for each of the available versions of a feed. The array must be sorted by
   * increasing version numbers.
   */
  public val versions: List<VersionInfo>
) : GofsFeedData, List<VersionInfo> by versions

/** Information about a specific version of a GOFS feed. */
@Serializable
public data class VersionInfo(
  /** Version number of the feed. */
  public val version: String,
  /** URL of the corresponding gofs.json endpoint. */
  public val url: Url,
)
