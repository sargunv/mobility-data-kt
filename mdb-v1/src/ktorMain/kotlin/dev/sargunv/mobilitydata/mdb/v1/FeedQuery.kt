package dev.sargunv.mobilitydata.mdb.v1

import io.ktor.http.URLBuilder
import kotlin.jvm.JvmInline

/** Query parameters for `GET /v1/feeds`. */
public data class FeedQuery(
  /** Maximum number of feeds to return. The catalog defaults to 3500. */
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
  appendPage(query.limit, query.offset)
  query.status?.let { parameters.append("status", it.value) }
  appendProvider(query.provider, query.producerUrl)
  query.isOfficial?.let { parameters.append("is_official", it.toString()) }
}

/** Query parameters for `GET /v1/gtfs_feeds`. */
public data class GtfsFeedQuery(
  /** Maximum number of feeds to return. The catalog defaults to 2500. */
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

  /** Bounding box of the latest dataset. */
  public val datasetBbox: DatasetBboxFilter? = null,

  /** How [datasetBbox] is applied. Defaults to completely enclosed on the server. */
  public val boundingFilterMethod: BoundingFilterMethod? = null,
)

internal fun URLBuilder.appendGtfsFeedQuery(query: GtfsFeedQuery) {
  appendPage(query.limit, query.offset)
  appendProvider(query.provider, query.producerUrl)
  appendPlace(query.countryCode, query.subdivisionName, query.municipality)
  query.isOfficial?.let { parameters.append("is_official", it.toString()) }
  query.datasetBbox?.let { box ->
    parameters.append("dataset_latitudes", "${box.minimumLatitude},${box.maximumLatitude}")
    parameters.append("dataset_longitudes", "${box.minimumLongitude},${box.maximumLongitude}")
  }
  query.boundingFilterMethod?.let { parameters.append("bounding_filter_method", it.value) }
}

/** Latitude and longitude extents for `dataset_latitudes` and `dataset_longitudes`. */
public data class DatasetBboxFilter(
  /** Southern edge. */
  public val minimumLatitude: Double,

  /** Northern edge. */
  public val maximumLatitude: Double,

  /** Western edge. */
  public val minimumLongitude: Double,

  /** Eastern edge. */
  public val maximumLongitude: Double,
)

/** How a dataset bounding-box filter selects feeds. */
@JvmInline
public value class BoundingFilterMethod(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [BoundingFilterMethod]. */
  public companion object {
    /** Feeds whose latest dataset is fully inside the box. */
    public val CompletelyEnclosed: BoundingFilterMethod =
      BoundingFilterMethod("completely_enclosed")

    /** Feeds whose latest dataset overlaps the box. */
    public val PartiallyEnclosed: BoundingFilterMethod = BoundingFilterMethod("partially_enclosed")

    /** Feeds whose latest dataset is fully outside the box. */
    public val Disjoint: BoundingFilterMethod = BoundingFilterMethod("disjoint")
  }
}

/** Query parameters for `GET /v1/gtfs_rt_feeds`. */
public data class GtfsRtFeedQuery(
  /** Maximum number of feeds to return. The catalog defaults to 1000. */
  public val limit: Int? = null,

  /** Offset of the first feed to return. */
  public val offset: Int? = null,

  /** Partial, case-insensitive provider name. */
  public val provider: String? = null,

  /** Partial, case-insensitive producer URL. */
  public val producerUrl: String? = null,

  /** Realtime entity types to include. */
  public val entityTypes: List<RealtimeEntityType>? = null,

  /** ISO 3166-1 alpha-2 country code. */
  public val countryCode: String? = null,

  /** ISO 3166-2 subdivision name. */
  public val subdivisionName: String? = null,

  /** Municipality name. */
  public val municipality: String? = null,

  /** When true, only official feeds. */
  public val isOfficial: Boolean? = null,
)

internal fun URLBuilder.appendGtfsRtFeedQuery(query: GtfsRtFeedQuery) {
  appendPage(query.limit, query.offset)
  appendProvider(query.provider, query.producerUrl)
  query.entityTypes?.let { types ->
    parameters.append("entity_types", types.joinToString(",") { it.value })
  }
  appendPlace(query.countryCode, query.subdivisionName, query.municipality)
  query.isOfficial?.let { parameters.append("is_official", it.toString()) }
}

/** Query parameters for `GET /v1/gbfs_feeds`. */
public data class GbfsFeedQuery(
  /** Maximum number of feeds to return. The catalog defaults to 500. */
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

  /** GBFS system id. */
  public val systemId: String? = null,

  /** GBFS version. */
  public val version: String? = null,
)

internal fun URLBuilder.appendGbfsFeedQuery(query: GbfsFeedQuery) {
  appendPage(query.limit, query.offset)
  appendProvider(query.provider, query.producerUrl)
  appendPlace(query.countryCode, query.subdivisionName, query.municipality)
  query.systemId?.let { parameters.append("system_id", it) }
  query.version?.let { parameters.append("version", it) }
}

/** Query parameters for `GET /v1/search`. */
public data class SearchFeedsQuery(
  /** Maximum number of hits to return. The catalog defaults to 3500. */
  public val limit: Int? = null,

  /** Offset of the first hit to return. */
  public val offset: Int? = null,

  /** Status values to include. */
  public val status: List<FeedStatus>? = null,

  /** Restrict to one feed id. */
  public val feedId: FeedId? = null,

  /** Data types to include. */
  public val dataType: List<FeedDataType>? = null,

  /** When true, only official feeds. */
  public val isOfficial: Boolean? = null,

  /**
   * When true, only feeds that hold the Seal of Reliability. The catalog returns 403 unless the
   * caller has the seal-filter flag.
   */
  public val hasSeal: Boolean? = null,

  /** GBFS versions to include. */
  public val version: List<String>? = null,

  /** Full-text query over provider, location, and feed name. */
  public val searchQuery: String? = null,

  /** GTFS features to include. */
  public val feature: List<String>? = null,

  /** License ids to include. */
  public val licenseIds: List<String>? = null,

  /** When true, only SPDX licenses. */
  public val licenseIsSpdx: Boolean? = null,

  /** License taxonomy tags to include. */
  public val licenseTags: List<String>? = null,
)

internal fun URLBuilder.appendSearchFeedsQuery(query: SearchFeedsQuery) {
  appendPage(query.limit, query.offset)
  query.status?.let { statuses ->
    parameters.append("status", statuses.joinToString(",") { it.value })
  }
  query.feedId?.let { parameters.append("feed_id", it.value) }
  query.dataType?.let { types ->
    parameters.append("data_type", types.joinToString(",") { it.value })
  }
  query.isOfficial?.let { parameters.append("is_official", it.toString()) }
  query.hasSeal?.let { parameters.append("has_seal", it.toString()) }
  query.version?.let { parameters.append("version", it.joinToString(",")) }
  query.searchQuery?.let { parameters.append("search_query", it) }
  query.feature?.let { parameters.append("feature", it.joinToString(",")) }
  query.licenseIds?.let { parameters.append("license_ids", it.joinToString(",")) }
  query.licenseIsSpdx?.let { parameters.append("license_is_spdx", it.toString()) }
  query.licenseTags?.let { parameters.append("license_tags", it.joinToString(",")) }
}

/** Query parameters for `GET /v1/locations`. */
public data class LocationQuery(
  /** Maximum number of locations to return. The catalog defaults to 100. */
  public val limit: Int? = null,

  /** Offset of the first location to return. */
  public val offset: Int? = null,

  /** Free-text location search. */
  public val searchQuery: String? = null,

  /** ISO 3166-1 alpha-2 country code. */
  public val countryCode: String? = null,

  /** ISO 3166-2 subdivision code. */
  public val subdivisionCode: String? = null,

  /** Country, subdivision, or municipality. */
  public val locationType: LocationType? = null,
)

internal fun URLBuilder.appendLocationQuery(query: LocationQuery) {
  appendPage(query.limit, query.offset)
  query.searchQuery?.let { parameters.append("search_query", it) }
  query.countryCode?.let { parameters.append("country_code", it) }
  query.subdivisionCode?.let { parameters.append("subdivision_code", it) }
  query.locationType?.let { parameters.append("location_type", it.value) }
}

/** Query parameters for `GET /v1/gtfs_feeds/{id}/datasets`. */
public data class DatasetQuery(
  /** When true, only the latest dataset. */
  public val latest: Boolean? = null,

  /** Maximum number of datasets to return. The catalog defaults to 500. */
  public val limit: Int? = null,

  /** Offset of the first dataset to return. */
  public val offset: Int? = null,

  /** Inclusive lower bound on `downloaded_at`. */
  public val downloadedAfter: IsoDateTime? = null,

  /** Inclusive upper bound on `downloaded_at`. */
  public val downloadedBefore: IsoDateTime? = null,
)

internal fun URLBuilder.appendDatasetQuery(query: DatasetQuery) {
  query.latest?.let { parameters.append("latest", it.toString()) }
  appendPage(query.limit, query.offset)
  query.downloadedAfter?.let { parameters.append("downloaded_after", it.toString()) }
  query.downloadedBefore?.let { parameters.append("downloaded_before", it.toString()) }
}

/** Query parameters for `GET /v1/licenses`. */
public data class LicenseQuery(
  /** Maximum number of licenses to return. The catalog defaults to 100. */
  public val limit: Int? = null,

  /** Offset of the first license to return. */
  public val offset: Int? = null,
)

internal fun URLBuilder.appendLicenseQuery(query: LicenseQuery) {
  appendPage(query.limit, query.offset)
}

/** Query parameters for `GET /v1/gtfs_feeds/{id}/availability`. */
public data class AvailabilityQuery(
  /** Inclusive lower bound on `checked_at`. */
  public val from: IsoDateTime? = null,

  /** Inclusive upper bound on `checked_at`. */
  public val to: IsoDateTime? = null,

  /** Maximum number of checks to return. The catalog defaults to 100. */
  public val limit: Int? = null,

  /** Offset of the first check to return. */
  public val offset: Int? = null,

  /** Sort by `checked_at`. The catalog defaults to newest first. */
  public val sort: AvailabilitySort? = null,
)

internal fun URLBuilder.appendAvailabilityQuery(query: AvailabilityQuery) {
  query.from?.let { parameters.append("from", it.toString()) }
  query.to?.let { parameters.append("to", it.toString()) }
  appendPage(query.limit, query.offset)
  query.sort?.let { parameters.append("sort", it.value) }
}

/** Sort order for availability history. */
@JvmInline
public value class AvailabilitySort(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [AvailabilitySort]. */
  public companion object {
    /** Oldest `checked_at` first. */
    public val Asc: AvailabilitySort = AvailabilitySort("asc")

    /** Newest `checked_at` first. */
    public val Desc: AvailabilitySort = AvailabilitySort("desc")
  }
}

private fun URLBuilder.appendPage(limit: Int?, offset: Int?) {
  limit?.let { parameters.append("limit", it.toString()) }
  offset?.let { parameters.append("offset", it.toString()) }
}

private fun URLBuilder.appendProvider(provider: String?, producerUrl: String?) {
  provider?.let { parameters.append("provider", it) }
  producerUrl?.let { parameters.append("producer_url", it) }
}

private fun URLBuilder.appendPlace(
  countryCode: String?,
  subdivisionName: String?,
  municipality: String?,
) {
  countryCode?.let { parameters.append("country_code", it) }
  subdivisionName?.let { parameters.append("subdivision_name", it) }
  municipality?.let { parameters.append("municipality", it) }
}
