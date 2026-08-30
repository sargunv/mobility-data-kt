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
