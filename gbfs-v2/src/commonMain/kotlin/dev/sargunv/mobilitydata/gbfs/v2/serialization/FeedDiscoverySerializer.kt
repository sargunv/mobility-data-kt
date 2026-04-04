package dev.sargunv.mobilitydata.gbfs.v2.serialization

import dev.sargunv.mobilitydata.gbfs.v2.FeedType
import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.serialization.MapAsListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object FeedDiscoverySerializer :
  MapAsListSerializer<FeedDiscoverySerializer.FeedDiscoveryEntry, FeedType, Url>(
    FeedDiscoveryEntry.serializer()
  ) {

  override fun Map.Entry<FeedType, Url>.toDelegate(): FeedDiscoveryEntry =
    FeedDiscoveryEntry(feedType = this.key, url = this.value)

  @Serializable
  internal data class FeedDiscoveryEntry(@SerialName("name") val feedType: FeedType, val url: Url) :
    Map.Entry<FeedType, Url> {
    override val key: FeedType
      get() = feedType

    override val value: Url
      get() = url
  }
}
