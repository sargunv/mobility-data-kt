package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.CountryCode
import dev.sargunv.mobilitydata.utils.ExperimentalMobilityDataApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.maplibre.spatialk.geojson.MultiPolygon

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
  @SerialName("system_id") public val systemId: String,

  /**
   * Contains one object for each of the available versions of a feed. The array MUST be sorted by
   * increasing MAJOR and MINOR version number.
   */
  public val versions: List<VersionInfo>,

  /**
   * A GeoJSON MultiPolygon that describes the operating area.
   *
   * GBFS 3.1-RC. If `area` is supplied, the record describes the general operating area of the
   * system for the purpose of discovery. Geographic details of the system's operating restrictions
   * must be specified using station locations and geofencing zones, where appropriate.
   */
  @SerialName("area") @property:ExperimentalMobilityDataApi public val area: MultiPolygon? = null,

  /**
   * The ISO 3166-1 alpha-2 country code of the operating area.
   *
   * GBFS 3.1-RC. MUST NOT be specified if the operating area spans multiple countries.
   */
  @SerialName("country_code")
  @property:ExperimentalMobilityDataApi
  public val countryCode: CountryCode? = null,
)
