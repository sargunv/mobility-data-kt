package dev.sargunv.mobilitydata.gbfs.v1.serialization

import dev.sargunv.mobilitydata.gbfs.v1.FeedType
import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.serialization.MapAsListSerializer
import kotlinx.serialization.Serializable

internal object FeedDiscoverySerializer :
  MapAsListSerializer<FeedDiscoverySerializer.FeedDiscoveryEntry, FeedType, Url>(
    FeedDiscoveryEntry.serializer()
  ) {

  override fun Map.Entry<FeedType, Url>.toDelegate(): FeedDiscoveryEntry =
    FeedDiscoveryEntry(feedType = this.key, url = this.value)

  @Serializable
  internal data class FeedDiscoveryEntry(val feedType: FeedType, val url: Url) :
    Map.Entry<FeedType, Url> {
    override val key: FeedType
      get() = feedType

    override val value: Url
      get() = url
  }
}
