package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.CurrencyCode
import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Fare information for a transit agency's routes.
 *
 * This class represents a record in the fare_attributes.txt file (GTFS-Fares V1).
 */
@Serializable
public data class FareAttribute(
  /** Identifies a fare class. */
  @SerialName("fare_id") public val fareId: String,

  /** Fare price, in the unit specified by currency_type. */
  @SerialName("price") public val price: Double,

  /** Currency used to pay the fare. */
  @SerialName("currency_type") public val currencyType: CurrencyCode,

  /** Indicates when the fare must be paid. */
  @SerialName("payment_method") public val paymentMethod: PaymentMethod,

  /**
   * Indicates the number of transfers permitted on this fare. When null, unlimited transfers are
   * permitted.
   */
  @SerialName("transfers") public val transfers: Int?,

  /** Identifies the relevant agency for a fare. */
  @SerialName("agency_id") public val agencyId: String? = null,

  /** Length of time in seconds before a transfer expires. */
  @SerialName("transfer_duration") public val transferDuration: WholeSeconds? = null,
)

/** Indicates when the fare must be paid. */
@Serializable
@JvmInline
public value class PaymentMethod
private constructor(
  /** The integer value representing the payment method. */
  public val value: Int
) {
  /** Companion object containing predefined payment method constants. */
  public companion object {
    /** Fare is paid on board. */
    public val OnBoard: PaymentMethod = PaymentMethod(0)

    /** Fare must be paid before boarding. */
    public val BeforeBoarding: PaymentMethod = PaymentMethod(1)
  }
}
