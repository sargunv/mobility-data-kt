package dev.sargunv.mobilitydata.mdb.v1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Build metadata for the catalog API process. */
@Serializable
public data class Metadata(
  /** API version string reported by the service. */
  public val version: String? = null,

  /** Git commit of the running service. */
  @SerialName("commit_hash") public val commitHash: String? = null,
)
