@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Decimal
import dev.sargunv.mobilitydata.utils.defaultFractionDigits
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

/**
 * Serializer for [FareProduct] that formats [FareProduct.amount] using the currency's ISO 4217
 * minor-unit digits on encode. Decoding remains permissive.
 */
public object FareProductSerializer : KSerializer<FareProduct> {
  private val nullableString = String.serializer().nullable

  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("dev.sargunv.mobilitydata.gtfs.schedule.FareProduct") {
      element("fare_product_id", String.serializer().descriptor)
      element("fare_product_name", nullableString.descriptor, isOptional = true)
      element("rider_category_id", nullableString.descriptor, isOptional = true)
      element("fare_media_id", nullableString.descriptor, isOptional = true)
      element("amount", String.serializer().descriptor)
      element("currency", String.serializer().descriptor)
    }

  override fun serialize(encoder: Encoder, value: FareProduct) {
    val digits =
      value.currency.defaultFractionDigits
        ?: throw SerializationException("Unknown or unusable currency code: ${value.currency}")
    val amountText =
      try {
        value.amount.toString(decimalPlaces = digits)
      } catch (e: ArithmeticException) {
        throw SerializationException(
          "Amount ${value.amount} has excess precision for ${value.currency}",
          e,
        )
      }
    encoder.encodeStructure(descriptor) {
      encodeStringElement(descriptor, 0, value.fareProductId)
      encodeNullableSerializableElement(descriptor, 1, String.serializer(), value.fareProductName)
      encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.riderCategoryId)
      encodeNullableSerializableElement(descriptor, 3, String.serializer(), value.fareMediaId)
      encodeStringElement(descriptor, 4, amountText)
      encodeStringElement(descriptor, 5, value.currency)
    }
  }

  override fun deserialize(decoder: Decoder): FareProduct {
    var fareProductId: String? = null
    var fareProductName: String? = null
    var riderCategoryId: String? = null
    var fareMediaId: String? = null
    var amount: Decimal? = null
    var currency: String? = null
    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          CompositeDecoder.DECODE_DONE -> break
          0 -> fareProductId = decodeStringElement(descriptor, 0)
          1 -> fareProductName = decodeNullableSerializableElement(descriptor, 1, nullableString)
          2 -> riderCategoryId = decodeNullableSerializableElement(descriptor, 2, nullableString)
          3 -> fareMediaId = decodeNullableSerializableElement(descriptor, 3, nullableString)
          4 -> {
            val text = decodeStringElement(descriptor, 4)
            amount =
              try {
                Decimal.parse(text)
              } catch (e: NumberFormatException) {
                throw SerializationException("Invalid amount: $text", e)
              } catch (e: ArithmeticException) {
                throw SerializationException("Invalid amount: $text", e)
              }
          }
          5 -> currency = decodeStringElement(descriptor, 5)
          else -> throw SerializationException("Unexpected index $index")
        }
      }
    }
    return FareProduct(
      fareProductId = fareProductId ?: throw SerializationException("Missing fare_product_id"),
      fareProductName = fareProductName,
      riderCategoryId = riderCategoryId,
      fareMediaId = fareMediaId,
      amount = amount ?: throw SerializationException("Missing amount"),
      currency = currency ?: throw SerializationException("Missing currency"),
    )
  }
}
