package dev.sargunv.mobilitydata.gbfs.v2.serialization

import dev.sargunv.mobilitydata.gbfs.v2.FeedType
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class FeedDiscoverySerializer : KSerializer<Map<FeedType, Url>> {
  private val delegate = ListSerializer(Delegate.serializer())
  override val descriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Map<FeedType, Url>) =
    delegate.serialize(encoder, value.map { Delegate(it.key, it.value) })

  override fun deserialize(decoder: Decoder) =
    delegate.deserialize(decoder).associate { it.name to it.url }

  @Serializable private data class Delegate(val name: FeedType, val url: Url)
}
