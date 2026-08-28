package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.CurrencyCode
import dev.sargunv.mobilitydata.utils.Decimal
import dev.sargunv.mobilitydata.utils.defaultFractionDigits
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Describes the range of fares available for purchase by riders.
 *
 * This class represents a record in the fare_products.txt file.
 *
 * On encode, [amount] is written with the ISO 4217 minor-unit scale of [currency]. Decode accepts
 * any exact [Decimal] representation.
 */
@Serializable(with = FareProductSerializer::class)
public data class FareProduct(
  /** Identifies a fare product or set of fare products. */
  @SerialName("fare_product_id") public val fareProductId: String,

  /** The name of the fare product as displayed to riders. */
  @SerialName("fare_product_name") public val fareProductName: String? = null,

  /** Identifies a rider category eligible for the fare product. */
  @SerialName("rider_category_id") public val riderCategoryId: String? = null,

  /** Identifies a fare media that can be employed to use the fare product. */
  @SerialName("fare_media_id") public val fareMediaId: String? = null,

  /** The cost of the fare product. May be negative to represent transfer discounts. */
  @SerialName("amount") public val amount: Decimal,

  /** The currency of the cost of the fare product. */
  @SerialName("currency") public val currency: CurrencyCode,
)

@Serializable
private data class FareProductRecord(
  @SerialName("fare_product_id") val fareProductId: String,
  @SerialName("fare_product_name") val fareProductName: String? = null,
  @SerialName("rider_category_id") val riderCategoryId: String? = null,
  @SerialName("fare_media_id") val fareMediaId: String? = null,
  @SerialName("amount") val amount: String,
  @SerialName("currency") val currency: String,
)

internal object FareProductSerializer : KSerializer<FareProduct> {
  private val delegate = FareProductRecord.serializer()

  override val descriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: FareProduct) {
    encoder.encodeSerializableValue(
      delegate,
      FareProductRecord(
        fareProductId = value.fareProductId,
        fareProductName = value.fareProductName,
        riderCategoryId = value.riderCategoryId,
        fareMediaId = value.fareMediaId,
        amount = formatFareAmount(value.amount, value.currency),
        currency = value.currency,
      ),
    )
  }

  override fun deserialize(decoder: Decoder): FareProduct {
    val record = decoder.decodeSerializableValue(delegate)
    return FareProduct(
      fareProductId = record.fareProductId,
      fareProductName = record.fareProductName,
      riderCategoryId = record.riderCategoryId,
      fareMediaId = record.fareMediaId,
      amount = parseFareAmount(record.amount),
      currency = record.currency,
    )
  }
}

private fun formatFareAmount(amount: Decimal, currency: CurrencyCode): String {
  val digits =
    currency.defaultFractionDigits
      ?: throw SerializationException("Unknown or unusable currency code: $currency")
  return try {
    amount.toString(decimalPlaces = digits)
  } catch (e: ArithmeticException) {
    throw SerializationException("Amount $amount has excess precision for $currency", e)
  }
}

private fun parseFareAmount(text: String): Decimal =
  try {
    Decimal.parse(text)
  } catch (e: NumberFormatException) {
    throw SerializationException("Invalid amount: $text", e)
  } catch (e: ArithmeticException) {
    throw SerializationException("Invalid amount: $text", e)
  }
