package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.ExtendedLocalDate
import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.Uri
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.datetime.TimeZone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Details about the vehicle share system including system operator, system location, year
 * implemented, URL, contact info, time zone.
 */
@Serializable
public data class SystemInformation(
  /**
   * Globally unique identifier for the vehicle share system.
   *
   * This value is intended to remain the same over the life of the system. Each distinct system or
   * geographic area in which vehicles are operated SHOULD have its own system_id.
   */
  @SerialName("system_id") public val systemId: String,

  /**
   * The language that will be used throughout the rest of the files.
   *
   * It MUST match the value in the gbfs.json file.
   */
  public val language: LanguageCode,

  /** Name of the system to be displayed to customers. */
  public val name: String,

  /** Abbreviation for the system. */
  @SerialName("short_name") public val shortName: String? = null,

  /** Name of the system operator. */
  public val operator: String? = null,

  /** The URL of the vehicle share system. */
  public val url: Url? = null,

  /** URL where a customer can purchase a membership. */
  @SerialName("purchase_url") public val purchaseUrl: Url? = null,

  /** Date that the system began operations. */
  @SerialName("start_date") public val startDate: ExtendedLocalDate? = null,

  /**
   * A single voice telephone number for the system's customer service department.
   *
   * This field SHOULD contain punctuation marks to group the digits of the number. Dialable text is
   * permitted, but the field MUST NOT contain any other descriptive text.
   */
  @SerialName("phone_number") public val phoneNumber: String? = null,

  /**
   * A single contact email address actively monitored by the operator's customer service
   * department.
   *
   * This email address SHOULD be a direct contact point where riders can reach a customer service
   * representative.
   */
  public val email: String? = null,

  /** A single contact email for feed consumers to report technical issues with the feed. */
  @SerialName("feed_contact_email") public val feedContactEmail: String? = null,

  /** The time zone where the system is located. */
  public val timezone: TimeZone,

  /**
   * A fully qualified URL of a page that defines the license terms for the GBFS data for this
   * system, as well as any other license terms the system would like to define.
   */
  @SerialName("license_url") public val licenseUrl: Url? = null,

  /** Contains rental app information for Android and iOS platforms. */
  @SerialName("rental_apps") public val rentalApps: RentalAppUris? = null,
) : GbfsFeedData

/** Contains rental app information for different platforms. */
@Serializable
public data class RentalAppUris(
  /** Rental app information for the Android platform. */
  public val android: RentalAppPlatformUris? = null,

  /** Rental app information for the iOS platform. */
  public val ios: RentalAppPlatformUris? = null,
)

/** Rental app download and discovery information for a specific platform. */
@Serializable
public data class RentalAppPlatformUris(
  /**
   * URI where the rental app can be downloaded from.
   *
   * Typically this will be a URI to an app store. REQUIRED if a rental_uris field is populated for
   * this platform.
   */
  @SerialName("store_uri") public val storeUri: Uri,

  /**
   * URI that can be used to discover if the rental app is installed on the device.
   *
   * This intent is used by viewing apps to prioritize rental apps for a particular user based on
   * whether they already have a particular rental app installed. REQUIRED if a rental_uris field is
   * populated for this platform.
   */
  @SerialName("discovery_uri") public val discoveryUri: Uri,
)
