package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.Decimal
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonUnquotedLiteral

/**
 * Serializer for [Decimal].
 *
 * JSON encodings emit an unquoted numeric literal from the canonical decimal text and decode from
 * the token's textual content, never through [Double]. Non-JSON textual encodings use the canonical
 * decimal string.
 */
public object DecimalSerializer : KSerializer<Decimal> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("dev.sargunv.mobilitydata.utils.Decimal", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Decimal) {
    val text = value.toString()
    if (encoder is JsonEncoder) {
      encoder.encodeJsonElement(JsonUnquotedLiteral(text))
    } else {
      encoder.encodeString(text)
    }
  }

  override fun deserialize(decoder: Decoder): Decimal {
    val text =
      if (decoder is JsonDecoder) {
        val element = decoder.decodeJsonElement()
        val primitive =
          element as? JsonPrimitive
            ?: throw SerializationException("Expected a JSON primitive for Decimal, got $element")
        if (primitive.isString) {
          throw SerializationException("Expected a JSON number for Decimal, got a string")
        }
        primitive.content
      } else {
        decoder.decodeString()
      }
    return parseDecimalToken(text)
  }
}

private fun parseDecimalToken(text: String): Decimal =
  try {
    Decimal.parse(text)
  } catch (e: NumberFormatException) {
    throw SerializationException("Invalid decimal: $text", e)
  } catch (e: ArithmeticException) {
    throw SerializationException("Invalid decimal: $text", e)
  }
