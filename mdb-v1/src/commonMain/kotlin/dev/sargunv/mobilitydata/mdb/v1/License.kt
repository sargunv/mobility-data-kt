package dev.sargunv.mobilitydata.mdb.v1

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A catalog license, optionally with its rules. */
@Serializable
public data class License(
  /** Unique license identifier, often an SPDX id. */
  public val id: String? = null,

  /** License category reported by the catalog. */
  public val type: String? = null,

  /** Whether [id] is an SPDX identifier. */
  @SerialName("is_spdx") public val isSpdx: Boolean? = null,

  /** User-facing license name. */
  public val name: String? = null,

  /** URL where the license text can be read. */
  public val url: String? = null,

  /** License description. */
  public val description: String? = null,

  /** Instant the license was added to the catalog. */
  @SerialName("created_at") public val createdAt: IsoDateTime? = null,

  /** Instant the license was last updated. */
  @SerialName("updated_at") public val updatedAt: IsoDateTime? = null,

  /** Taxonomy tags associated with the license. */
  @SerialName("license_tags") public val licenseTags: List<String>? = null,

  /** Permission, condition, and limitation rules. Absent on list responses. */
  @SerialName("license_rules") public val licenseRules: List<LicenseRule>? = null,
)

/** One permission, condition, or limitation on a license. */
@Serializable
public data class LicenseRule(
  /** Machine-readable rule name. */
  public val name: String? = null,

  /** User-facing rule label. */
  public val label: String? = null,

  /** Rule description. */
  public val description: String? = null,

  /** Whether this rule is a permission, condition, or limitation. */
  public val type: LicenseRuleType? = null,
)

/** Request body for `POST /v1/licenses:match`. */
@Serializable
public data class LicenseMatchRequest(
  /** License URL to resolve against the catalog. */
  @SerialName("license_url") public val licenseUrl: String
)

/** One catalog license that matched a submitted URL. */
@Serializable
public data class MatchingLicense(
  /** Catalog license id, often an SPDX id. */
  @SerialName("license_id") public val licenseId: String? = null,

  /** License URL submitted for matching. */
  @SerialName("license_url") public val licenseUrl: String? = null,

  /** Normalized form of [licenseUrl]. */
  @SerialName("normalized_url") public val normalizedUrl: String? = null,

  /** How the catalog matched the URL. */
  @SerialName("match_type") public val matchType: LicenseMatchType? = null,

  /** Match confidence from 0.0 to 1.0. */
  public val confidence: Double? = null,

  /** SPDX identifier when the match resolved to one. */
  @SerialName("spdx_id") public val spdxId: String? = null,

  /** User-facing name of the matched license. */
  @SerialName("matched_name") public val matchedName: String? = null,

  /** Canonical catalog URL for the matched license. */
  @SerialName("matched_catalog_url") public val matchedCatalogUrl: String? = null,

  /** Source that produced the match. */
  @SerialName("matched_source") public val matchedSource: String? = null,

  /** Matcher notes such as version normalization or a detected locale. */
  public val notes: String? = null,

  /** Regional or jurisdictional variant id. */
  @SerialName("regional_id") public val regionalId: String? = null,
)

/** Kind of license URL match. */
@Serializable
@JvmInline
public value class LicenseMatchType(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [LicenseMatchType]. */
  public companion object {
    /** Direct match in the catalog. */
    public val Exact: LicenseMatchType = LicenseMatchType("exact")

    /** Pattern-based match such as a Creative Commons resolver. */
    public val Heuristic: LicenseMatchType = LicenseMatchType("heuristic")

    /** Similarity match against licenses on the same host. */
    public val Fuzzy: LicenseMatchType = LicenseMatchType("fuzzy")
  }
}

/** Kind of [LicenseRule]. */
@Serializable
@JvmInline
public value class LicenseRuleType(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [LicenseRuleType]. */
  public companion object {
    /** Something the license allows. */
    public val Permission: LicenseRuleType = LicenseRuleType("permission")

    /** Something the licensee must do. */
    public val Condition: LicenseRuleType = LicenseRuleType("condition")

    /** Something the license forbids or limits. */
    public val Limitation: LicenseRuleType = LicenseRuleType("limitation")
  }
}
