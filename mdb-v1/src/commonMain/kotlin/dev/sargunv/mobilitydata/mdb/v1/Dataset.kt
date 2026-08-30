package dev.sargunv.mobilitydata.mdb.v1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A GTFS dataset hosted by the catalog. */
@Serializable
public data class GtfsDataset(
  /** Unique dataset identifier. */
  public val id: String? = null,
  /** Identifier of the parent feed. */
  @SerialName("feed_id") public val feedId: FeedId? = null,
  /** URL of the dataset as hosted by MobilityData. */
  @SerialName("hosted_url") public val hostedUrl: String? = null,
  /** Note that clarifies complex use cases. */
  public val note: String? = null,
  /** Instant the dataset was downloaded from the producer. */
  @SerialName("downloaded_at") public val downloadedAt: IsoDateTime? = null,
  /** SHA-256 hash of the dataset. */
  public val hash: String? = null,
  /** MD5 hash of the dataset. */
  @SerialName("hash_md5") public val hashMd5: String? = null,
  /** Bounding box of the dataset. */
  @SerialName("bounding_box") public val boundingBox: BoundingBox? = null,
  /** Latest validation report. */
  @SerialName("validation_report") public val validationReport: ValidationReport? = null,
  /** Start of the service date range in UTC. */
  @SerialName("service_date_range_start") public val serviceDateRangeStart: IsoDateTime? = null,
  /** End of the service date range in UTC. */
  @SerialName("service_date_range_end") public val serviceDateRangeEnd: IsoDateTime? = null,
  /** IANA timezone of the agency. */
  @SerialName("agency_timezone") public val agencyTimezone: String? = null,
  /** Size of the zipped folder in megabytes. */
  @SerialName("zipped_folder_size_mb") public val zippedFolderSizeMb: Double? = null,
  /** Size of the unzipped folder in megabytes. */
  @SerialName("unzipped_folder_size_mb") public val unzippedFolderSizeMb: Double? = null,
)

/** Latest dataset summary embedded on a GTFS feed. */
@Serializable
public data class LatestDataset(
  /** Identifier of the latest dataset for this feed. */
  public val id: String? = null,
  /** Hosted URL of the latest uploaded dataset. */
  @SerialName("hosted_url") public val hostedUrl: String? = null,
  /** Bounding box of the latest dataset. */
  @SerialName("bounding_box") public val boundingBox: BoundingBox? = null,
  /** Instant the dataset was downloaded from the producer. */
  @SerialName("downloaded_at") public val downloadedAt: IsoDateTime? = null,
  /** SHA-256 hash of the dataset. */
  public val hash: String? = null,
  /** MD5 hash of the dataset. */
  @SerialName("hash_md5") public val hashMd5: String? = null,
  /** Start of the service date range in UTC. */
  @SerialName("service_date_range_start") public val serviceDateRangeStart: IsoDateTime? = null,
  /** End of the service date range in UTC. */
  @SerialName("service_date_range_end") public val serviceDateRangeEnd: IsoDateTime? = null,
  /** IANA timezone of the agency. */
  @SerialName("agency_timezone") public val agencyTimezone: String? = null,
  /** Size of the zipped folder in megabytes. */
  @SerialName("zipped_folder_size_mb") public val zippedFolderSizeMb: Double? = null,
  /** Size of the unzipped folder in megabytes. */
  @SerialName("unzipped_folder_size_mb") public val unzippedFolderSizeMb: Double? = null,
  /** Latest validation report. */
  @SerialName("validation_report") public val validationReport: ValidationReport? = null,
)

/** Geographic bounding box of a dataset. */
@Serializable
public data class BoundingBox(
  /** Minimum latitude. */
  @SerialName("minimum_latitude") public val minimumLatitude: Double? = null,
  /** Maximum latitude. */
  @SerialName("maximum_latitude") public val maximumLatitude: Double? = null,
  /** Minimum longitude. */
  @SerialName("minimum_longitude") public val minimumLongitude: Double? = null,
  /** Maximum longitude. */
  @SerialName("maximum_longitude") public val maximumLongitude: Double? = null,
)

/** GTFS validation report summary. */
@Serializable
public data class ValidationReport(
  /** Instant the report was generated. */
  @SerialName("validated_at") public val validatedAt: IsoDateTime? = null,
  /** GTFS features detected in the dataset. */
  public val features: List<String>? = null,
  /** Validator version that produced the report. */
  @SerialName("validator_version") public val validatorVersion: String? = null,
  /** Total error count. */
  @SerialName("total_error") public val totalError: Int? = null,
  /** Total warning count. */
  @SerialName("total_warning") public val totalWarning: Int? = null,
  /** Total info count. */
  @SerialName("total_info") public val totalInfo: Int? = null,
  /** Unique error count. */
  @SerialName("unique_error_count") public val uniqueErrorCount: Int? = null,
  /** Unique warning count. */
  @SerialName("unique_warning_count") public val uniqueWarningCount: Int? = null,
  /** Unique info count. */
  @SerialName("unique_info_count") public val uniqueInfoCount: Int? = null,
  /** URL of the JSON validation report. */
  @SerialName("url_json") public val urlJson: String? = null,
  /** URL of the HTML validation report. */
  @SerialName("url_html") public val urlHtml: String? = null,
)
