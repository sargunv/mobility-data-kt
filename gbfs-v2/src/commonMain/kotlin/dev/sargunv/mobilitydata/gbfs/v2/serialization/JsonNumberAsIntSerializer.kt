package dev.sargunv.mobilitydata.gbfs.v2.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.double
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

internal object JsonNumberAsIntSerializer : KSerializer<Int> {
  private val delegate = Int.serializer()

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Int): Unit = delegate.serialize(encoder, value)

  override fun deserialize(decoder: Decoder): Int {
    val jsonDecoder = decoder as? JsonDecoder ?: return decoder.decodeInt()
    val primitive = jsonDecoder.decodeJsonElement().jsonPrimitive
    return primitive.intOrNull ?: primitive.double.toInt()
  }
}
