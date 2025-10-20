package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.gofs.v1.serialization.FeedDiscoverySerializer
import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.Url
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Auto-discovery file that links to all of the other files published by the data producer.
 *
 * The gofs.json discovery file should represent a single system or geographic area in which
 * vehicles are operated. The location (URL) of the gofs.json file should be made available to the
 * public using the specification's auto-discovery function.
 */
@JvmInline
@Serializable
public value class SystemManifest(
  @SerialName("feeds") private val services: Map<LanguageCode, Service>
) : GofsFeedData, Map<LanguageCode, Service> by services {
  public constructor(vararg entries: Pair<LanguageCode, Service>) : this(mapOf(*entries))

  /**
   * Gets the service for the specified language.
   *
   * @param language The language code for the desired service.
   * @return The service for the specified language.
   * @throws NoSuchElementException if no service exists for the specified language.
   */
  public fun getService(language: LanguageCode): Service = services.getValue(language)

  /**
   * Gets the service for the specified language, or null if not found.
   *
   * @param language The language code for the desired service.
   * @return The service for the specified language, or null if not found.
   */
  public fun getServiceOrNull(language: LanguageCode): Service? = services[language]
}

/**
 * A service containing feeds for a specific language.
 *
 * Represents all feeds published for a specific language in the GOFS system.
 */
@Serializable
public data class Service(
  /**
   * An array of all of the feeds that are published by this auto-discovery file. Maps feed types to
   * their URLs.
   */
  @Serializable(with = FeedDiscoverySerializer::class) val feeds: Map<FeedType, Url>
) : Map<FeedType, Url> by feeds {
  public constructor(vararg entries: Pair<FeedType, Url>) : this(mapOf(*entries))
}

/**
 * The type of GOFS feed.
 *
 * Identifies the type of feed. The key must be the base file name defined in the spec for the
 * corresponding feed type.
 */
@Serializable
public enum class FeedType {
  /** Auto-discovery file linking to all other feeds. */
  @SerialName("gofs") SystemManifest,
  /** Shows different versions available for the same GOFS feed. */
  @SerialName("gofs_versions") VersionManifest,
  /** Defines attributes of the on-demand service system. */
  @SerialName("system_information") SystemInformation,
  /** Details the different on-demand service brands available. */
  @SerialName("service_brands") ServiceBrands,
  /** Describes vehicle types used for operating the on-demand services. */
  @SerialName("vehicle_types") VehicleTypes,
  /** Geographically defines zones where on-demand services are available. */
  @SerialName("zones") Zones,
  /** Defines rules for intra-zone and inter-zone trips. */
  @SerialName("operating_rules") OperatingRules,
  /** Defines dates and days when on-demand services are available. */
  @SerialName("calendars") Calendars,
  /** Defines static fare rules for a system. */
  @SerialName("fares") Fares,
  /** Returns wait time for queried areas. */
  @SerialName("wait_time") WaitTimes,
  /** Returns rules for booking in queried areas. */
  @SerialName("booking_rules") BookingRules,
  /** Returns details for available booking when static booking details can't be provided. */
  @SerialName("realtime_booking") RealtimeBookings,
}
