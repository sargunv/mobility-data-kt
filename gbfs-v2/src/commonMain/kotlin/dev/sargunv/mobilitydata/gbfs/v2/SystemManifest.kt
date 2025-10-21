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
public value class SystemManifest(
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
@JvmInline
public value class FeedType(
  /** The string value representing the feed type. */
  public val value: String
) {
  /** Companion object containing predefined feed type constants. */
  public companion object {
    /** The gbfs_versions.json file listing available GBFS versions. */
    public val VersionManifest: FeedType = FeedType("gbfs_versions")

    /** The system_information.json file with system details. */
    public val SystemInformation: FeedType = FeedType("system_information")

    /** The vehicle_types.json file describing available vehicle types. */
    public val VehicleTypes: FeedType = FeedType("vehicle_types")

    /** The station_information.json file with station details. */
    public val StationInformation: FeedType = FeedType("station_information")

    /** The station_status.json file with real-time station status. */
    public val StationStatus: FeedType = FeedType("station_status")

    /** The free_bike_status.json file with available vehicles. */
    public val FreeBikeStatus: FeedType = FeedType("free_bike_status")

    /** The system_hours.json file with operating hours. */
    public val SystemHours: FeedType = FeedType("system_hours")

    /** The system_calendar.json file with operating calendar. */
    public val SystemCalendar: FeedType = FeedType("system_calendar")

    /** The system_regions.json file with geographic regions. */
    public val SystemRegions: FeedType = FeedType("system_regions")

    /** The system_pricing_plans.json file with pricing information. */
    public val SystemPricingPlans: FeedType = FeedType("system_pricing_plans")

    /** The system_alerts.json file with system alerts. */
    public val SystemAlerts: FeedType = FeedType("system_alerts")

    /** The geofencing_zones.json file with geofencing zones. */
    public val GeofencingZones: FeedType = FeedType("geofencing_zones")
  }
}
