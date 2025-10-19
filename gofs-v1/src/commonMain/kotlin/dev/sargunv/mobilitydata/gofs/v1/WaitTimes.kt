package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class WaitTimes(@SerialName("wait_times") public val waitTimes: List<WaitTime>) :
  GofsFeedData, List<WaitTime> by waitTimes

@Serializable
public data class WaitTime(
  @SerialName("brand_id") public val brandId: Id<Brand>,
  @SerialName("wait_time") public val waitTime: WholeSeconds,
)
