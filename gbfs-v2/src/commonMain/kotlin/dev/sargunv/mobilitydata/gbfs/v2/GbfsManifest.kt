package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.gbfs.v2.serialization.FeedDiscoverySerializer
import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.Url
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The gbfs.json auto-discovery file that represents a single system or geographic area in which
 * vehicles are operated.
 *
 * Maps language codes to their corresponding GBFS service feeds.
 */
@JvmInline
@Serializable
public value class GbfsManifest(
  @SerialName("feeds") private val services: Map<LanguageCode, Service>
) : GbfsFeedData, Map<LanguageCode, Service> by services {
  public constructor(vararg entries: Pair<LanguageCode, Service>) : this(mapOf(*entries))

  /**
   * Get the GBFS service for a specific language code.
   *
   * @throws NoSuchElementException if the language code is not found
   */
  public fun getService(language: LanguageCode): Service = services.getValue(language)

  /** Get the GBFS service for a specific language code, or null if not found. */
  public fun getServiceOrNull(language: LanguageCode): Service? = services[language]
}

/**
 * A collection of GBFS feeds for a specific language.
 *
 * Contains URLs for all available feed types in this GBFS system.
 */
@Serializable
public data class Service(
  /** Map of feed types to their corresponding URLs. */
  @Serializable(with = FeedDiscoverySerializer::class) val feeds: Map<FeedType, Url>
) : Map<FeedType, Url> by feeds {
  public constructor(vararg entries: Pair<FeedType, Url>) : this(mapOf(*entries))
}

/**
 * Types of feeds that can be published in a GBFS system.
 *
 * Each feed type corresponds to a specific JSON file defined in the GBFS specification.
 */
@Serializable
public enum class FeedType {
  /** The gbfs.json auto-discovery file. */
  @SerialName("gbfs") GbfsManifest,

  /** The gbfs_versions.json file listing available GBFS versions. */
  @SerialName("gbfs_versions") GbfsVersions,

  /** The system_information.json file with system details. */
  @SerialName("system_information") SystemInformation,

  /** The vehicle_types.json file describing available vehicle types. */
  @SerialName("vehicle_types") VehicleTypes,

  /** The station_information.json file with station details. */
  @SerialName("station_information") StationInformation,

  /** The station_status.json file with real-time station status. */
  @SerialName("station_status") StationStatus,

  /** The free_bike_status.json file with available vehicles. */
  @SerialName("free_bike_status") FreeBikeStatus,

  /** The system_hours.json file with operating hours. */
  @SerialName("system_hours") SystemHours,

  /** The system_calendar.json file with operating calendar. */
  @SerialName("system_calendar") SystemCalendar,

  /** The system_regions.json file with geographic regions. */
  @SerialName("system_regions") SystemRegions,

  /** The system_pricing_plans.json file with pricing information. */
  @SerialName("system_pricing_plans") SystemPricingPlans,

  /** The system_alerts.json file with system alerts. */
  @SerialName("system_alerts") SystemAlerts,

  /** The geofencing_zones.json file with geofencing zones. */
  @SerialName("geofencing_zones") GeofencingZones,
}
