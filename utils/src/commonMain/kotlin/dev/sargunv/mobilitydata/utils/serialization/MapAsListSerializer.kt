package dev.sargunv.mobilitydata.utils.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Abstract serializer that converts Map<Key, Value> to and from a list representation.
 *
 * This serializer is useful when you need to serialize maps as arrays of objects rather than as
 * key-value pairs, which is common in JSON APIs where maps are represented as arrays of objects
 * with explicit key and value properties.
 *
 * @param Delegate The type that represents a single map entry in the serialized format
 * @param Key The type of keys in the map
 * @param Value The type of values in the map
 * @param delegateSerializer The serializer for the delegate entry type
 */
public abstract class MapAsListSerializer<Delegate : Map.Entry<Key, Value>, Key, Value>(
  delegateSerializer: KSerializer<Delegate>
) : KSerializer<Map<Key, Value>> {
  private val delegate = ListSerializer(delegateSerializer)

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun deserialize(decoder: Decoder): Map<Key, Value> =
    delegate.deserialize(decoder).associate { it.key to it.value }

  override fun serialize(encoder: Encoder, value: Map<Key, Value>): Unit =
    delegate.serialize(encoder, value.map { it.toDelegate() })

  /**
   * Converts a map entry to the delegate type used for serialization.
   *
   * @param this The map entry to convert
   * @return The delegate representation suitable for serialization
   */
  protected abstract fun Map.Entry<Key, Value>.toDelegate(): Delegate
}
