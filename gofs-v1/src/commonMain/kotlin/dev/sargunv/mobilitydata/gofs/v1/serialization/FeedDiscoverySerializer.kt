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

  override fun deserialize(decoder: Decoder): Map<FeedType, Url> =
    super.deserialize(decoder).mapKeys { (feedType, _) ->
      when (feedType.value) {
        // Freebee publishes the discovery name `wait_times` instead of GOFS v1.0 `wait_time`.
        "wait_times" -> FeedType.WaitTimes
        else -> feedType
      }
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
