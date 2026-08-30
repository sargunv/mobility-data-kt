package dev.sargunv.mobilitydata.mdb.v1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Envelope returned by `GET /v1/search`. */
@Serializable
public data class SearchFeedsResponse(
  /** Total matching feeds, ignoring limit and offset. */
  public val total: Int? = null,

  /** Page of matching feeds. */
  public val results: List<SearchFeedItem>? = null,
)

/** One feed in a search result page. */
@Serializable
public data class SearchFeedItem(
  /** Catalog feed id. */
  public val id: FeedId? = null,

  /** Discriminator for this mixed search row. */
  @SerialName("data_type") public val dataType: FeedDataType? = null,

  /** Publication status. */
  public val status: FeedStatus? = null,

  /** Instant the feed was added to the catalog. */
  @SerialName("created_at") public val createdAt: IsoDateTime? = null,

  /** Whether the feed is official. */
  public val official: Boolean? = null,

  /** Whether the feed only covers a recurring season. */
  public val seasonal: Boolean? = false,

  /** External identifiers. */
  @SerialName("external_ids") public val externalIds: List<ExternalId>? = null,

  /** Transit provider name. */
  public val provider: String? = null,

  /** Optional feed description. */
  @SerialName("feed_name") public val feedName: String? = null,

  /** Note that clarifies complex use cases. */
  public val note: String? = null,

  /** Producer contact email. */
  @SerialName("feed_contact_email") public val feedContactEmail: String? = null,

  /** Producer URL and authentication. */
  @SerialName("source_info") public val sourceInfo: SourceInfo? = null,

  /** Replacement feeds. */
  public val redirects: List<Redirect>? = null,

  /** Locations served by the feed. */
  public val locations: List<Location>? = null,

  /** Latest dataset, for GTFS hits. */
  @SerialName("latest_dataset") public val latestDataset: LatestDataset? = null,

  /** Realtime entity types, for GTFS-RT hits. */
  @SerialName("entity_types") public val entityTypes: List<RealtimeEntityType>? = null,

  /** GBFS versions, for GBFS hits. */
  public val versions: List<String>? = null,

  /** Related GTFS feed ids, for GTFS-RT hits. */
  @SerialName("feed_references") public val feedReferences: List<FeedId>? = null,

  /** Embedded Seal of Reliability summary. */
  @SerialName("reliability_seal") public val reliabilitySeal: FeedReliabilitySummary? = null,
)
