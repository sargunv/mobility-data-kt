package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returns the wait time for a specific location.
 *
 * This dynamic query returns the wait time for a specific location. A wait_time request must be
 * made for each user interaction.
 */
@Serializable
public data class WaitTimes(
  /**
   * An array that contains one object per brand_id. Should be empty if no wait time is available
   * for the requested location.
   */
  @SerialName("wait_times") public val waitTimes: List<WaitTime>
) : GofsFeedData, List<WaitTime> by waitTimes

/** Wait time information for a specific service brand. */
@Serializable
public data class WaitTime(
  /** ID from a service brand defined in service_brands.json. */
  @SerialName("brand_id") public val brandId: Id<Brand>,
  /** Wait time in seconds the rider will need to wait in the location before pickup. */
  @SerialName("wait_time") public val waitTime: WholeSeconds,
)
