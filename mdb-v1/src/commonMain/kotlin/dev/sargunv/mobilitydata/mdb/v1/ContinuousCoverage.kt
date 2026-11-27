package dev.sargunv.mobilitydata.mdb.v1

import dev.sargunv.mobilitydata.utils.ExtendedLocalDate
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Continuous coverage history for a GTFS feed. */
@Serializable
public data class GtfsFeedContinuousCoverageResponse(
  /** Catalog id of the GTFS feed. */
  @SerialName("feed_id") public val feedId: FeedId? = null,

  /** The feed's latest dataset measured against the one before it. */
  @SerialName("latest_state") public val latestState: GtfsFeedContinuousCoverageBoundary? = null,

  /** The same comparison at the criterion's last observed failure. */
  @SerialName("latest_failure")
  public val latestFailure: GtfsFeedContinuousCoverageBoundary? = null,

  /** Total matching datasets, ignoring limit and offset. */
  public val total: Int? = null,

  /** Offset of the first returned item. */
  public val offset: Int? = null,

  /** Maximum number of items returned. */
  public val limit: Int? = null,

  /** One entry per dataset, newest `downloaded_at` first. */
  public val items: List<GtfsFeedContinuousCoverage>? = null,
)

/** Two successive datasets: [newer] and the one downloaded immediately before it. */
@Serializable
public data class GtfsFeedContinuousCoverageBoundary(
  /** The newer dataset of the pair. */
  public val newer: GtfsFeedContinuousCoverage? = null,

  /** The dataset downloaded immediately before [newer], or null for the feed's first dataset. */
  public val older: GtfsFeedContinuousCoverage? = null,
)

/** Coverage one dataset contributes, and how it lines up with the previous dataset. */
@Serializable
public data class GtfsFeedContinuousCoverage(
  /** Stable identifier of the dataset this entry describes. */
  @SerialName("dataset_id") public val datasetId: String? = null,

  /** Whether this is the feed's latest dataset. */
  @SerialName("is_latest") public val isLatest: Boolean? = null,

  /** Instant the dataset was downloaded. */
  @SerialName("downloaded_at") public val downloadedAt: IsoDateTime? = null,

  /** Window the criterion measures by. */
  @SerialName("coverage_window") public val coverageWindow: ServiceDateWindow? = null,

  /** Which input [coverageWindow] was taken from. */
  @SerialName("coverage_window_source")
  public val coverageWindowSource: CoverageWindowSource? = null,

  /** Whether [coverageWindow] stays inside the two-year maximum the seal allows. */
  @SerialName("within_max_coverage_window") public val withinMaxCoverageWindow: Boolean? = null,

  /** Service dates derived from `calendar.txt` and `calendar_dates.txt`. */
  @SerialName("service_window") public val serviceWindow: ServiceDateWindow? = null,

  /** Dates declared in `feed_info.txt`. */
  @SerialName("feed_info_window") public val feedInfoWindow: ServiceDateWindow? = null,

  /** Whether [feedInfoWindow] agrees with [serviceWindow] on both bounds. */
  @SerialName("feed_info_matches") public val feedInfoMatches: Boolean? = null,

  /** Identifier of the dataset downloaded immediately before this one. */
  @SerialName("previous_dataset_id") public val previousDatasetId: String? = null,

  /** Days of overlap with the immediately older dataset's coverage window. */
  @SerialName("overlap_days") public val overlapDays: Int? = null,

  /** Days of uncovered service between the older window and this one. */
  @SerialName("gap_days") public val gapDays: Int? = null,

  /** Files the calculation reads, and whether each was present. */
  public val files: List<GtfsFeedContinuousCoverageFile>? = null,
)

/** Presence of one GTFS file used by the continuous-coverage calculation. */
@Serializable
public data class GtfsFeedContinuousCoverageFile(
  /** Name of the GTFS file. */
  public val name: String? = null,

  /** Whether the file was present in the dataset. */
  public val present: Boolean? = null,
)

/** Closed range of service dates, with its length in days. */
@Serializable
public data class ServiceDateWindow(
  /** First date covered by the window. */
  public val start: ExtendedLocalDate? = null,

  /** Last date covered by the window. */
  public val end: ExtendedLocalDate? = null,

  /** Length of the window in days, counting both bounds. */
  public val days: Int? = null,
)

/** Which input a coverage window was taken from. */
@Serializable
@JvmInline
public value class CoverageWindowSource(
  /** Wire value. */
  public val value: String,
) {
  /** Named constants for [CoverageWindowSource]. */
  public companion object {
    /** Dates declared in `feed_info.txt`. */
    public val FeedInfo: CoverageWindowSource = CoverageWindowSource("feed_info")

    /** Service dates derived by the validator from `calendar.txt` and `calendar_dates.txt`. */
    public val ServiceDates: CoverageWindowSource = CoverageWindowSource("service_dates")
  }
}
