package dev.sargunv.mobilitydata.utils.serialization

import de.westnordost.osm_opening_hours.model.OpeningHours
import de.westnordost.osm_opening_hours.parser.toOpeningHours
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for [OpeningHours] as an
 * [OSM opening_hours](https://wiki.openstreetmap.org/wiki/Key:opening_hours) string.
 *
 * Decoding is lenient so that common GBFS values are accepted. Encoding always writes the canonical
 * form produced by [OpeningHours.toString].
 */
public object OsmOpeningHoursSerializer : KSerializer<OpeningHours> {
  private val delegate = String.serializer()

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: OpeningHours): Unit =
    delegate.serialize(encoder, value.toString())

  override fun deserialize(decoder: Decoder): OpeningHours =
    delegate.deserialize(decoder).toOpeningHours(lenient = true)
}
