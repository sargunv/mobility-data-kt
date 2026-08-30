package dev.sargunv.mobilitydata.mdb.v1

import io.ktor.http.URLBuilder

/** Query parameters for `GET /v1/feeds`. */
public data class FeedQuery(
  /** Maximum number of feeds to return. */
  public val limit: Int? = null,
  /** Offset of the first feed to return. */
  public val offset: Int? = null,
  /** Filter by publication status. */
  public val status: FeedStatus? = null,
  /** Partial, case-insensitive provider name. */
  public val provider: String? = null,
  /** Partial, case-insensitive producer URL. */
  public val producerUrl: String? = null,
  /** When true, only official feeds. */
  public val isOfficial: Boolean? = null,
)

internal fun URLBuilder.appendFeedQuery(query: FeedQuery) {
  query.limit?.let { parameters.append("limit", it.toString()) }
  query.offset?.let { parameters.append("offset", it.toString()) }
  query.status?.let { parameters.append("status", it.value) }
  query.provider?.let { parameters.append("provider", it) }
  query.producerUrl?.let { parameters.append("producer_url", it) }
  query.isOfficial?.let { parameters.append("is_official", it.toString()) }
}

/** Query parameters for typed feed list endpoints. */
public data class TypedFeedQuery(
  /** Maximum number of feeds to return. */
  public val limit: Int? = null,
  /** Offset of the first feed to return. */
  public val offset: Int? = null,
  /** Partial, case-insensitive provider name. */
  public val provider: String? = null,
  /** Partial, case-insensitive producer URL. */
  public val producerUrl: String? = null,
  /** ISO 3166-1 alpha-2 country code. */
  public val countryCode: String? = null,
  /** ISO 3166-2 subdivision name. */
  public val subdivisionName: String? = null,
  /** Municipality name. */
  public val municipality: String? = null,
  /** When true, only official feeds. */
  public val isOfficial: Boolean? = null,
  /** Realtime entity types, for GTFS-RT lists. */
  public val entityTypes: List<RealtimeEntityType>? = null,
  /** GBFS system id, for GBFS lists. */
  public val systemId: String? = null,
  /** GBFS version, for GBFS lists. */
  public val version: String? = null,
)

internal fun URLBuilder.appendTypedFeedQuery(query: TypedFeedQuery) {
  query.limit?.let { parameters.append("limit", it.toString()) }
  query.offset?.let { parameters.append("offset", it.toString()) }
  query.provider?.let { parameters.append("provider", it) }
  query.producerUrl?.let { parameters.append("producer_url", it) }
  query.countryCode?.let { parameters.append("country_code", it) }
  query.subdivisionName?.let { parameters.append("subdivision_name", it) }
  query.municipality?.let { parameters.append("municipality", it) }
  query.isOfficial?.let { parameters.append("is_official", it.toString()) }
  query.entityTypes?.let { types ->
    parameters.append("entity_types", types.joinToString(",") { it.value })
  }
  query.systemId?.let { parameters.append("system_id", it) }
  query.version?.let { parameters.append("version", it) }
}

/** Query parameters for `GET /v1/search`. */
public data class SearchFeedsQuery(
  /** Full-text query over provider, location, and feed name. */
  public val searchQuery: String? = null,
  /** Maximum number of hits to return. */
  public val limit: Int? = null,
  /** Offset of the first hit to return. */
  public val offset: Int? = null,
  /** Status values to include. */
  public val status: List<FeedStatus>? = null,
  /** Restrict to one feed id. */
  public val feedId: FeedId? = null,
  /** Comma-separated data types such as `gtfs,gtfs_rt`. */
  public val dataType: String? = null,
  /** When true, only official feeds. */
  public val isOfficial: Boolean? = null,
)

internal fun URLBuilder.appendSearchFeedsQuery(query: SearchFeedsQuery) {
  query.searchQuery?.let { parameters.append("search_query", it) }
  query.limit?.let { parameters.append("limit", it.toString()) }
  query.offset?.let { parameters.append("offset", it.toString()) }
  query.status?.let { statuses ->
    parameters.append("status", statuses.joinToString(",") { it.value })
  }
  query.feedId?.let { parameters.append("feed_id", it.value) }
  query.dataType?.let { parameters.append("data_type", it) }
  query.isOfficial?.let { parameters.append("is_official", it.toString()) }
}

/** Query parameters for `GET /v1/locations`. */
public data class LocationQuery(
  /** Free-text location search. */
  public val searchQuery: String? = null,
  /** Maximum number of locations to return. */
  public val limit: Int? = null,
  /** Offset of the first location to return. */
  public val offset: Int? = null,
  /** ISO 3166-1 alpha-2 country code. */
  public val countryCode: String? = null,
  /** ISO 3166-2 subdivision code. */
  public val subdivisionCode: String? = null,
  /** Country, subdivision, or municipality. */
  public val locationType: LocationType? = null,
)

internal fun URLBuilder.appendLocationQuery(query: LocationQuery) {
  query.searchQuery?.let { parameters.append("search_query", it) }
  query.limit?.let { parameters.append("limit", it.toString()) }
  query.offset?.let { parameters.append("offset", it.toString()) }
  query.countryCode?.let { parameters.append("country_code", it) }
  query.subdivisionCode?.let { parameters.append("subdivision_code", it) }
  query.locationType?.let { parameters.append("location_type", it.value) }
}

/** Query parameters for GTFS dataset lists. */
public data class DatasetQuery(
  /** When true, only the latest dataset. */
  public val latest: Boolean? = null,
  /** Maximum number of datasets to return. */
  public val limit: Int? = null,
  /** Offset of the first dataset to return. */
  public val offset: Int? = null,
)

internal fun URLBuilder.appendDatasetQuery(query: DatasetQuery) {
  query.latest?.let { parameters.append("latest", it.toString()) }
  query.limit?.let { parameters.append("limit", it.toString()) }
  query.offset?.let { parameters.append("offset", it.toString()) }
}

/** Query parameters for license lists. */
public data class LicenseQuery(
  /** Maximum number of licenses to return. */
  public val limit: Int? = null,
  /** Offset of the first license to return. */
  public val offset: Int? = null,
)

internal fun URLBuilder.appendLicenseQuery(query: LicenseQuery) {
  query.limit?.let { parameters.append("limit", it.toString()) }
  query.offset?.let { parameters.append("offset", it.toString()) }
}

/** Query parameters for GTFS availability history. */
public data class AvailabilityQuery(
  /** Maximum number of checks to return. */
  public val limit: Int? = null,
  /** Offset of the first check to return. */
  public val offset: Int? = null,
)

internal fun URLBuilder.appendAvailabilityQuery(query: AvailabilityQuery) {
  query.limit?.let { parameters.append("limit", it.toString()) }
  query.offset?.let { parameters.append("offset", it.toString()) }
}
