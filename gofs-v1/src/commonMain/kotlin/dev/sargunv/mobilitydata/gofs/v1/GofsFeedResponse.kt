package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.EpochSeconds
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlin.time.ExperimentalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalTime::class)
public data class GofsFeedResponse<T : GofsFeedData>(
  @SerialName("last_updated") public val lastUpdated: EpochSeconds,
  public val ttl: WholeSeconds,
  public val version: String,
  public val data: T,
)

public sealed interface GofsFeedData
