package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.gbfs.v3.serialization.FeedDiscoverySerializer
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The gbfs.json auto-discovery file that represents a single system or geographic area in which
 * vehicles are operated.
 *
 * Contains URLs for all available feed types in this GBFS system.
 */
@Serializable
public data class ServiceManifest(
  /** Map of feed types to their corresponding URLs. */
  @Serializable(with = FeedDiscoverySerializer::class) val feeds: Map<FeedType, Url>
) : GbfsFeedData, Map<FeedType, Url> by feeds {
  public constructor(vararg entries: Pair<FeedType, Url>) : this(mapOf(*entries))
}

/**
 * Types of feeds that can be published in a GBFS system.
 *
 * Each feed type corresponds to a specific JSON file defined in the GBFS specification.
 */
@Serializable
public enum class FeedType {

  /** The gbfs_versions.json file listing available GBFS versions for this system. */
  @SerialName("gbfs_versions") VersionManifest,

  /** The system_information.json file with system details. */
  @SerialName("system_information") SystemInformation,

  /** The vehicle_types.json file describing available vehicle types. */
  @SerialName("vehicle_types") VehicleTypes,

  /** The station_information.json file with station details. */
  @SerialName("station_information") StationInformation,

  /** The station_status.json file with real-time station status. */
  @SerialName("station_status") StationStatus,

  /** The vehicle_status.json file with available vehicles. */
  @SerialName("vehicle_status") VehicleStatus,

  /** The system_regions.json file with geographic regions. */
  @SerialName("system_regions") SystemRegions,

  /** The system_pricing_plans.json file with pricing information. */
  @SerialName("system_pricing_plans") SystemPricingPlans,

  /** The system_alerts.json file with system alerts. */
  @SerialName("system_alerts") SystemAlerts,

  /** The geofencing_zones.json file with geofencing zones. */
  @SerialName("geofencing_zones") GeofencingZones,
}
