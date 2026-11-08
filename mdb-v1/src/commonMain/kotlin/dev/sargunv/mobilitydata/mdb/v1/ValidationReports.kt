package dev.sargunv.mobilitydata.mdb.v1

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Validation history for a GTFS feed, one entry per dataset. */
@Serializable
public data class GtfsFeedValidationReportsResponse(
  /** Catalog id of the GTFS feed. */
  @SerialName("feed_id") public val feedId: FeedId? = null,

  /** Entry for the feed's current dataset, independent of paging. */
  public val latest: GtfsFeedValidationReport? = null,

  /** Total matching datasets, ignoring limit and offset. */
  public val total: Int? = null,

  /** Offset of the first returned item. */
  public val offset: Int? = null,

  /** Maximum number of items returned. */
  public val limit: Int? = null,

  /** One entry per dataset, newest `validated_at` first. */
  public val items: List<GtfsFeedValidationReport>? = null,
)

/** Most recent validation report of one dataset. */
@Serializable
public data class GtfsFeedValidationReport(
  /** Stable identifier of the validated dataset. */
  @SerialName("dataset_id") public val datasetId: String? = null,

  /** Whether this is the feed's latest dataset. */
  @SerialName("is_latest") public val isLatest: Boolean? = null,

  /** Instant the report was generated. */
  @SerialName("validated_at") public val validatedAt: IsoDateTime? = null,

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

  /** Notice codes raised, ordered by severity then by count. */
  public val notices: List<GtfsFeedValidationNotice>? = null,
)

/** One validator notice code raised in a report. */
@Serializable
public data class GtfsFeedValidationNotice(
  /** Validator notice code. */
  public val code: String? = null,

  /** Notice severity. */
  public val severity: ValidationNoticeSeverity? = null,

  /** How many times this code was raised. */
  public val total: Int? = null,
)

/** Severity of a GTFS validator notice. */
@Serializable
@JvmInline
public value class ValidationNoticeSeverity(
  /** Wire value. */
  public val value: String,
) {
  /** Named constants for [ValidationNoticeSeverity]. */
  public companion object {
    /** Validator error. */
    public val Error: ValidationNoticeSeverity = ValidationNoticeSeverity("ERROR")

    /** Validator warning. */
    public val Warning: ValidationNoticeSeverity = ValidationNoticeSeverity("WARNING")

    /** Validator info. */
    public val Info: ValidationNoticeSeverity = ValidationNoticeSeverity("INFO")
  }
}
