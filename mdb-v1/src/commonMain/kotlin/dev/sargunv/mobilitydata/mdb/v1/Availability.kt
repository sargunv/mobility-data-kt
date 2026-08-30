package dev.sargunv.mobilitydata.mdb.v1

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Historical availability checks for a GTFS feed. */
@Serializable
public data class GtfsFeedAvailabilityResponse(
  /** Catalog id of the GTFS feed. */
  @SerialName("feed_id") public val feedId: FeedId? = null,
  /** Total matching checks, ignoring limit and offset. */
  public val total: Int? = null,
  /** Offset of the first returned check. */
  public val offset: Int? = null,
  /** Maximum number of checks returned. */
  public val limit: Int? = null,
  /** Checks matching the requested filters. */
  public val checks: List<GtfsFeedAvailabilityCheck>? = null,
)

/** One scheduled availability check. */
@Serializable
public data class GtfsFeedAvailabilityCheck(
  /** Instant the check ran. */
  @SerialName("checked_at") public val checkedAt: IsoDateTime? = null,
  /** Whether the producer URL responded. */
  public val success: Boolean? = null,
  /** HTTP method used for the check. */
  @SerialName("request_method") public val requestMethod: AvailabilityRequestMethod? = null,
  /** Final HTTP status code, when available. */
  @SerialName("status_code") public val statusCode: Int? = null,
  /** Time taken to receive the response, in milliseconds. */
  @SerialName("latency_ms") public val latencyMs: Double? = null,
  /** Machine-readable error category when the check failed. */
  @SerialName("error_type") public val errorType: String? = null,
)

/** HTTP method used for an availability check. */
@Serializable
@JvmInline
public value class AvailabilityRequestMethod(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [AvailabilityRequestMethod]. */
  public companion object {
    /** HTTP HEAD. */
    public val Head: AvailabilityRequestMethod = AvailabilityRequestMethod("HEAD")

    /** HTTP GET. */
    public val Get: AvailabilityRequestMethod = AvailabilityRequestMethod("GET")
  }
}
