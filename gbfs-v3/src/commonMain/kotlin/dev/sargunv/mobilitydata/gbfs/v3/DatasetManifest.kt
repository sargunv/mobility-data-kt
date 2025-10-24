package dev.sargunv.mobilitydata.gbfs.v3

import kotlinx.serialization.Serializable

/**
 * An index of gbfs.json URLs for each GBFS data set produced by a publisher. A single instance of
 * this file should be published at a single stable URL, for example:
 * https://example.com/gbfs/manifest.json
 */
@Serializable
public data class DatasetManifest(
  /** An array of datasets produced by a publisher. */
  public val datasets: List<Dataset>
) : GbfsFeedData, List<Dataset> by datasets

/**
 * Represents a GBFS dataset published by a provider.
 *
 * Each dataset corresponds to a distinct system or geographic area in which vehicles are operated,
 * identified by its system ID and available in multiple GBFS versions.
 */
@Serializable
public data class Dataset(
  /** The system_id from system_information.json for the corresponding data set(s). */
  public val systemId: String,

  /**
   * Contains one object for each of the available versions of a feed. The array MUST be sorted by
   * increasing MAJOR and MINOR version number.
   */
  public val versions: List<VersionInfo>,
)
