package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.ExtendedLocalDate
import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.LocalizedText
import dev.sargunv.mobilitydata.utils.LocalizedUrl
import dev.sargunv.mobilitydata.utils.OsmOpeningHours
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
  @SerialName("system_id") public val systemId: Id<SystemInformation>,

  /** List of langauges used in translated strings. */
  public val languages: List<LanguageCode>,

  /** Name of the system to be displayed to customers. */
  public val name: LocalizedText,

  /** Hours and dates of operation for the system */
  @SerialName("opening_hours") public val openingHours: OsmOpeningHours,

  /** Abbreviation for the system. */
  @SerialName("short_name") public val shortName: LocalizedText? = null,

  /** Name of the system operator. */
  public val operator: LocalizedText? = null,

  /** The URL of the vehicle share system. */
  public val url: Url? = null,

  /** URL where a customer can purchase a membership. */
  @SerialName("purchase_url") public val purchaseUrl: Url? = null,

  /** Date that the system began operations. */
  @SerialName("start_date") public val startDate: ExtendedLocalDate? = null,

  /** Date after which this data source will no longer be available to consuming applications. */
  @SerialName("termination_date") public val terminationDate: ExtendedLocalDate? = null,

  /**
   * A single voice telephone number for the system's customer service department, in the
   * [E.164 format](https://www.itu.int/rec/T-REC-E.164-201011-I/en).
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
  @SerialName("feed_contact_email") public val feedContactEmail: String,

  /** A fully qualified URL pointing to the manifest.json file for the publisher. */
  @SerialName("manifest_url") public val manifestUrl: Url? = null,

  /** The time zone where the system is located. */
  public val timezone: TimeZone,

  /**
   * An identifier for a standard license from the [SPDX License List](https://spdx.org/licenses/).
   * Provide license_id rather than license_url if the license is included in the SPDX License List.
   */
  @SerialName("license_id") public val licenseId: String? = null,

  /**
   * A fully qualified URL of a page that defines the license terms for the GBFS data for this
   * system, as well as any other license terms the system would like to define.
   */
  @SerialName("license_url") public val licenseUrl: Url? = null,

  /**
   * If the feed license requires attribution, name of the organization to which attribution should
   * be provided.
   */
  @SerialName("attribution_organization_name")
  public val attributionOrganizationName: LocalizedText? = null,

  /** URL of the organization to which attribution should be provided. */
  @SerialName("attribution_url") public val attributionUrl: Url? = null,

  /** Brand assets such as images and colors used to represent the system. */
  @SerialName("brand_assets") public val brandAssets: BrandAssets? = null,

  /**
   * A fully qualified URL pointing to the terms of service (also often called "terms of use" or
   * "terms and conditions") for the service.
   */
  @SerialName("terms_url") public val termsUrl: LocalizedUrl? = null,

  /**
   * The date that the terms of service provided at terms_url were last updated.
   *
   * REQUIRED if terms_url is defined.
   */
  @SerialName("terms_last_updated") public val termsLastUpdated: ExtendedLocalDate? = null,

  /** A fully qualified URL pointing to the privacy policy for the service. */
  @SerialName("privacy_url") public val privacyUrl: LocalizedUrl? = null,

  /**
   * The date that the privacy policy provided at privacy_url was last updated.
   *
   * REQUIRED if privacy_url is defined.
   */
  @SerialName("privacy_last_updated") public val privacyLastUpdated: ExtendedLocalDate? = null,

  /** Contains rental app information for Android and iOS platforms. */
  @SerialName("rental_apps") public val rentalApps: RentalAppUris? = null,
) : GbfsFeedData

/** Brand assets such as images and colors used to represent the system. */
@Serializable
public data class BrandAssets(
  /**
   * Date that indicates the last time any included brand assets were updated or modified.
   *
   * REQUIRED if brand_assets object is defined.
   */
  @SerialName("brand_last_modified") public val brandLastModified: ExtendedLocalDate,

  /**
   * A fully qualified URL pointing to the location of a page that defines the license terms of
   * brand icons, colors, or other trademark information.
   *
   * This field MUST NOT take the place of license_url.
   */
  @SerialName("brand_terms_url") public val brandTermsUrl: Url? = null,

  /**
   * A fully qualified URL pointing to the location of a graphic file representing the brand for the
   * service.
   *
   * REQUIRED if brand_assets object is defined. File MUST be in SVG V1.1 format and MUST be either
   * square or round.
   */
  @SerialName("brand_image_url") public val brandImageUrl: Url,

  /**
   * A fully qualified URL pointing to the location of a graphic file representing the brand for the
   * service for use in dark mode applications.
   *
   * File MUST be in SVG V1.1 format and MUST be either square or round.
   */
  @SerialName("brand_image_url_dark") public val brandImageUrlDark: Url? = null,

  /**
   * Color used to represent the brand for the service expressed as a 6 digit hexadecimal color code
   * in the form #000000.
   */
  public val color: String? = null,
)

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
