package dev.sargunv.mobilitydata.mdb.v1

import kotlin.jvm.JvmInline
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

/** Catalog identifier for a feed, such as `mdb-1210`. */
@Serializable
@JvmInline
public value class FeedId(
  /** Wire value. */
  public val value: String
)

/**
 * A Mobility Database catalog feed.
 *
 * The catalog discriminator is `data_type`. GTFS and GTFS-RT extend the full feed object. GBFS
 * extends only the basic feed fields, so those extra properties stay off [Gbfs].
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("data_type")
public sealed class Feed {
  /** Unique catalog identifier. */
  public abstract val id: FeedId?

  /** Instant the feed was added to the catalog. */
  public abstract val createdAt: IsoDateTime?

  /** Identifiers for the same feed in external databases. */
  public abstract val externalIds: List<ExternalId>?

  /** Common name for the transit provider. */
  public abstract val provider: String?

  /** Contact email for the feed producer. */
  public abstract val feedContactEmail: String?

  /** Producer URL and authentication details. */
  public abstract val sourceInfo: SourceInfo?

  /** Replacement feeds when this one should not be used. */
  public abstract val redirects: List<Redirect>?

  /** A static GTFS feed. */
  @Serializable
  @SerialName("gtfs")
  public data class Gtfs(
    override val id: FeedId? = null,
    @SerialName("created_at") override val createdAt: IsoDateTime? = null,
    @SerialName("external_ids") override val externalIds: List<ExternalId>? = null,
    override val provider: String? = null,
    @SerialName("feed_contact_email") override val feedContactEmail: String? = null,
    @SerialName("source_info") override val sourceInfo: SourceInfo? = null,
    override val redirects: List<Redirect>? = null,
    /** Whether the feed is active, deprecated, inactive, in development, or future. */
    public val status: FeedStatus? = null,
    /** Whether the feed is provided by the agency or a trusted source. */
    public val official: Boolean? = null,
    /** Instant the official status was last updated. */
    @SerialName("official_updated_at") public val officialUpdatedAt: IsoDateTime? = null,
    /** Whether the feed only covers a recurring season. */
    public val seasonal: Boolean? = null,
    /** Optional description of the data feed. */
    @SerialName("feed_name") public val feedName: String? = null,
    /** Note that clarifies complex use cases. */
    public val note: String? = null,
    /** Related links for the feed. */
    @SerialName("related_links") public val relatedLinks: List<FeedRelatedLink>? = null,
    /** Locations served by the feed. */
    public val locations: List<Location>? = null,
    /** Latest hosted dataset. */
    @SerialName("latest_dataset") public val latestDataset: LatestDataset? = null,
    /** Bounding box of the feed. */
    @SerialName("bounding_box") public val boundingBox: BoundingBox? = null,
    /** Dataset id used to compute visualization files. */
    @SerialName("visualization_dataset_id") public val visualizationDatasetId: String? = null,
    /** Embedded Seal of Reliability summary. */
    @SerialName("reliability_seal") public val reliabilitySeal: FeedReliabilitySummary? = null,
  ) : Feed()

  /** A GTFS Realtime feed. */
  @Serializable
  @SerialName("gtfs_rt")
  public data class GtfsRt(
    override val id: FeedId? = null,
    @SerialName("created_at") override val createdAt: IsoDateTime? = null,
    @SerialName("external_ids") override val externalIds: List<ExternalId>? = null,
    override val provider: String? = null,
    @SerialName("feed_contact_email") override val feedContactEmail: String? = null,
    @SerialName("source_info") override val sourceInfo: SourceInfo? = null,
    override val redirects: List<Redirect>? = null,
    /** Whether the feed is active, deprecated, inactive, in development, or future. */
    public val status: FeedStatus? = null,
    /** Whether the feed is provided by the agency or a trusted source. */
    public val official: Boolean? = null,
    /** Instant the official status was last updated. */
    @SerialName("official_updated_at") public val officialUpdatedAt: IsoDateTime? = null,
    /** Whether the feed only covers a recurring season. */
    public val seasonal: Boolean? = null,
    /** Optional description of the data feed. */
    @SerialName("feed_name") public val feedName: String? = null,
    /** Note that clarifies complex use cases. */
    public val note: String? = null,
    /** Related links for the feed. */
    @SerialName("related_links") public val relatedLinks: List<FeedRelatedLink>? = null,
    /** Realtime entity types published by this feed. */
    @SerialName("entity_types") public val entityTypes: List<RealtimeEntityType>? = null,
    /** GTFS feed ids this realtime source is associated with. */
    @SerialName("feed_references") public val feedReferences: List<FeedId>? = null,
    /** Locations served by the feed. */
    public val locations: List<Location>? = null,
  ) : Feed()

  /** A GBFS feed. */
  @Serializable
  @SerialName("gbfs")
  public data class Gbfs(
    override val id: FeedId? = null,
    @SerialName("created_at") override val createdAt: IsoDateTime? = null,
    @SerialName("external_ids") override val externalIds: List<ExternalId>? = null,
    override val provider: String? = null,
    @SerialName("feed_contact_email") override val feedContactEmail: String? = null,
    @SerialName("source_info") override val sourceInfo: SourceInfo? = null,
    override val redirects: List<Redirect>? = null,
    /** Locations served by the feed. */
    public val locations: List<Location>? = null,
    /** GBFS system id. */
    @SerialName("system_id") public val systemId: String? = null,
    /** Provider website. */
    @SerialName("provider_url") public val providerUrl: String? = null,
    /** GBFS versions this feed supports. */
    public val versions: List<GbfsVersion>? = null,
    /** Bounding box of the feed. */
    @SerialName("bounding_box") public val boundingBox: BoundingBox? = null,
    /** Instant the bounding box was generated. */
    @SerialName("bounding_box_generated_at") public val boundingBoxGeneratedAt: IsoDateTime? = null,
  ) : Feed()
}

/** Publication status of a feed. */
@Serializable
@JvmInline
public value class FeedStatus(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [FeedStatus]. */
  public companion object {
    /** The feed should be used in public trip planners. */
    public val Active: FeedStatus = FeedStatus("active")

    /** The feed is explicitly deprecated. */
    public val Deprecated: FeedStatus = FeedStatus("deprecated")

    /** The feed has not been updated recently. */
    public val Inactive: FeedStatus = FeedStatus("inactive")

    /** The feed is for development only. */
    public val Development: FeedStatus = FeedStatus("development")

    /** The feed is not yet active. */
    public val Future: FeedStatus = FeedStatus("future")
  }
}

/** GTFS Realtime entity type published by a feed. */
@Serializable
@JvmInline
public value class RealtimeEntityType(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [RealtimeEntityType]. */
  public companion object {
    /** Vehicle positions. */
    public val VehiclePositions: RealtimeEntityType = RealtimeEntityType("vp")

    /** Trip updates. */
    public val TripUpdates: RealtimeEntityType = RealtimeEntityType("tu")

    /** Service alerts. */
    public val ServiceAlerts: RealtimeEntityType = RealtimeEntityType("sa")
  }
}

/** Identifier for the same feed in an external or legacy database. */
@Serializable
public data class ExternalId(
  /** External identifier. */
  @SerialName("external_id") public val externalId: String? = null,
  /** Database that issued [externalId]. */
  public val source: String? = null,
)

/** Producer URL and how to authenticate to it. */
@Serializable
public data class SourceInfo(
  /** URL where the producer hosts the dataset. */
  @SerialName("producer_url") public val producerUrl: String? = null,
  /** Whether [producerUrl] is known to change over time. */
  @SerialName("is_producer_url_unstable") public val isProducerUrlUnstable: Boolean? = null,
  /**
   * Authentication required to access [producerUrl].
   *
   * `0` means none, `1` means an API key query parameter, and `2` means an HTTP header.
   */
  @SerialName("authentication_type") public val authenticationType: Int? = null,
  /** Human-readable page describing how to authenticate. */
  @SerialName("authentication_info_url") public val authenticationInfoUrl: String? = null,
  /** Query parameter or header name that carries the API key. */
  @SerialName("api_key_parameter_name") public val apiKeyParameterName: String? = null,
  /** URL of the feed license. */
  @SerialName("license_url") public val licenseUrl: String? = null,
  /** License id that can be queried from the license endpoint. */
  @SerialName("license_id") public val licenseId: String? = null,
  /** Whether [licenseId] is an SPDX identifier. */
  @SerialName("license_is_spdx") public val licenseIsSpdx: Boolean? = null,
  /** Notes about the feed's license. */
  @SerialName("license_notes") public val licenseNotes: String? = null,
  /** Taxonomy tags associated with the feed's license. */
  @SerialName("license_tags") public val licenseTags: List<String>? = null,
)

/** A replacement feed that should be used instead of the current one. */
@Serializable
public data class Redirect(
  /** Feed id that replaces the current feed. */
  @SerialName("target_id") public val targetId: FeedId? = null,
  /** Explanation of the redirect. */
  public val comment: String? = null,
)

/** A related link attached to a feed. */
@Serializable
public data class FeedRelatedLink(
  /** Short code that identifies the kind of link. */
  public val code: String? = null,
  /** Description of the link. */
  public val description: String? = null,
  /** Link URL. */
  public val url: String? = null,
  /** Instant the link was created. */
  @SerialName("created_at") public val createdAt: IsoDateTime? = null,
)

/** One GBFS specification version supported by a feed. */
@Serializable
public data class GbfsVersion(
  /** GBFS version string. */
  public val version: String? = null,
  /** Instant this version row was saved. */
  @SerialName("created_at") public val createdAt: IsoDateTime? = null,
  /** Instant this version row was last updated. */
  @SerialName("last_updated_at") public val lastUpdatedAt: IsoDateTime? = null,
  /** Whether the version came from autodiscovery or `gbfs_versions`. */
  public val source: GbfsVersionSource? = null,
  /** Endpoints available in this version. */
  public val endpoints: List<GbfsEndpoint>? = null,
  /** Latest validation report for this version. */
  @SerialName("latest_validation_report")
  public val latestValidationReport: GbfsValidationReport? = null,
)

/** Origin of a [GbfsVersion] row. */
@Serializable
@JvmInline
public value class GbfsVersionSource(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [GbfsVersionSource]. */
  public companion object {
    /** Retrieved from the main GBFS autodiscovery URL. */
    public val Autodiscovery: GbfsVersionSource = GbfsVersionSource("autodiscovery")

    /** Retrieved from the `gbfs_versions` endpoint. */
    public val GbfsVersions: GbfsVersionSource = GbfsVersionSource("gbfs_versions")
  }
}

/** One endpoint in a GBFS version. */
@Serializable
public data class GbfsEndpoint(
  /** Endpoint name, such as `system_information`. */
  public val name: String? = null,
  /** Endpoint URL. */
  public val url: String? = null,
  /** Language of the endpoint, for GBFS 2.3 and earlier. */
  public val language: String? = null,
  /** Whether this endpoint is an optional GBFS feature. */
  @SerialName("is_feature") public val isFeature: Boolean? = null,
)

/** Validation report for a GBFS version. */
@Serializable
public data class GbfsValidationReport(
  /** Instant the feed was validated. */
  @SerialName("validated_at") public val validatedAt: IsoDateTime? = null,
  /** Total error count. */
  @SerialName("total_error") public val totalError: Int? = null,
  /** URL of the JSON validation summary. */
  @SerialName("report_summary_url") public val reportSummaryUrl: String? = null,
  /** Validator version. */
  @SerialName("validator_version") public val validatorVersion: String? = null,
)

/** Embedded Seal of Reliability summary on a GTFS feed. */
@Serializable
public data class FeedReliabilitySummary(
  /** Whether the feed currently holds the seal. */
  @SerialName("has_seal") public val hasSeal: Boolean? = null,
  /** Instant the feed most recently earned the seal. */
  @SerialName("earned_at") public val earnedAt: IsoDateTime? = null,
  /** Instant the feed most recently lost the seal. */
  @SerialName("lost_at") public val lostAt: IsoDateTime? = null,
  /** Instant the criteria were last evaluated. */
  @SerialName("evaluated_at") public val evaluatedAt: IsoDateTime? = null,
  /** Whether at least one criterion is serving probation. */
  @SerialName("on_probation") public val onProbation: Boolean? = null,
  /** Earliest instant the feed could regain the seal. */
  @SerialName("probation_ends_at") public val probationEndsAt: IsoDateTime? = null,
)
