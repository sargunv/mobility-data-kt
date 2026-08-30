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
