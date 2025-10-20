package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.LocalizedText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes regions for a system.
 *
 * Regions are a subset of a shared mobility system as defined by system_id in
 * system_information.json. Regions may be defined for any purpose, for example political
 * jurisdictions, neighborhoods or economic zones.
 */
@Serializable
public data class SystemRegions(
  /** Array of regions in the system. */
  public val regions: List<Region>
) : GbfsFeedData

/** A geographic or administrative region within the system. */
@Serializable
public data class Region(
  /** Identifier for the region. */
  @SerialName("region_id") public val regionId: Id<Region>,

  /** Public name for this region. */
  public val name: LocalizedText,
)
