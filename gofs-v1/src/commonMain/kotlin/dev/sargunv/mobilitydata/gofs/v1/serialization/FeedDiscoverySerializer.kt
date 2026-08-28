package dev.sargunv.mobilitydata.gofs.v1.serialization

import dev.sargunv.mobilitydata.gofs.v1.FeedType
import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.serialization.MapAsListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder

internal object FeedDiscoverySerializer :
  MapAsListSerializer<FeedDiscoverySerializer.FeedDiscoveryEntry, FeedType, Url>(
    FeedDiscoveryEntry.serializer()
  ) {

  override fun Map.Entry<FeedType, Url>.toDelegate(): FeedDiscoveryEntry =
    FeedDiscoveryEntry(feedType = this.key, url = this.value)

  override fun deserialize(decoder: Decoder): Map<FeedType, Url> {
    val feeds = super.deserialize(decoder)
    // Freebee publishes the discovery name `wait_times` instead of GOFS v1.0 `wait_time`.
    // When both names are present, the spec name wins.
    val specUrl = feeds[FeedType.WaitTimes]
    val aliasUrl = feeds.entries.firstOrNull { it.key.value == "wait_times" }?.value
    val withoutAlias = feeds.filterKeys { it.value != "wait_times" }
    val waitTimesUrl = specUrl ?: aliasUrl
    return if (waitTimesUrl == null) withoutAlias
    else withoutAlias + (FeedType.WaitTimes to waitTimesUrl)
  }

  @Serializable
  internal data class FeedDiscoveryEntry(@SerialName("name") val feedType: FeedType, val url: Url) :
    Map.Entry<FeedType, Url> {
    override val key: FeedType
      get() = feedType

    override val value: Url
      get() = url
  }
}
