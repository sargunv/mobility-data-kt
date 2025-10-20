package dev.sargunv.mobilitydata.utils.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public abstract class MapAsListSerializer<Delegate : Map.Entry<Key, Value>, Key, Value>(
  delegateSerializer: KSerializer<Delegate>
) : KSerializer<Map<Key, Value>> {
  private val delegate = ListSerializer(delegateSerializer)

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun deserialize(decoder: Decoder): Map<Key, Value> =
    delegate.deserialize(decoder).associate { it.key to it.value }

  override fun serialize(encoder: Encoder, value: Map<Key, Value>): Unit =
    delegate.serialize(encoder, value.map { it.toDelegate() })

  protected abstract fun Map.Entry<Key, Value>.toDelegate(): Delegate
}
