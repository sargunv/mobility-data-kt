package dev.sargunv.mobilitydata.mdb.v1

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Geographic area associated with a feed. */
@Serializable
public data class Location(
  /** ISO 3166-1 alpha-2 country code. */
  @SerialName("country_code") public val countryCode: String? = null,

  /** English country name. */
  public val country: String? = null,

  /** ISO 3166-2 English subdivision name. */
  @SerialName("subdivision_name") public val subdivisionName: String? = null,

  /** Primary municipality in English. */
  public val municipality: String? = null,
)

/** Paginated location search response. */
@Serializable
public data class LocationSearchResponse(
  /** Total matching locations, ignoring limit and offset. */
  public val total: Int? = null,

  /** Page of matching locations. */
  public val results: List<LocationSearchResult>? = null,
)

/** One location in a search result page. */
@Serializable
public data class LocationSearchResult(
  /** Stable location identifier. */
  @SerialName("location_id") public val locationId: Int? = null,

  /** Identifier of the nearest containing location. */
  @SerialName("parent_location_id") public val parentLocationId: Int? = null,

  /** Primary name, in English when available. */
  public val name: String? = null,

  /** Alternate or local name. */
  @SerialName("alt_name") public val altName: String? = null,

  /** Whether this row is a country, subdivision, or municipality. */
  @SerialName("location_type") public val locationType: LocationType? = null,

  /** Name of the containing country. */
  @SerialName("country_name") public val countryName: String? = null,

  /** ISO 3166-1 alpha-2 code of the containing country. */
  @SerialName("country_code") public val countryCode: String? = null,

  /** Name of the containing subdivision. */
  @SerialName("subdivision_name") public val subdivisionName: String? = null,

  /** ISO 3166-2 code of the containing subdivision. */
  @SerialName("subdivision_code") public val subdivisionCode: String? = null,

  /** Names from the broadest containing area down to this location. */
  @SerialName("path_names") public val pathNames: List<String>? = null,

  /** Human-readable hierarchy joined from the broadest area to this location. */
  @SerialName("display_name") public val displayName: String? = null,
)

/** Kind of catalog location. */
@Serializable
@JvmInline
public value class LocationType(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [LocationType]. */
  public companion object {
    /** A country with an ISO 3166-1 code. */
    public val Country: LocationType = LocationType("country")

    /** A subdivision with an ISO 3166-2 code. */
    public val Subdivision: LocationType = LocationType("subdivision")

    /** A locality below the subdivision level. */
    public val Municipality: LocationType = LocationType("municipality")
  }
}
