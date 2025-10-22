package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.CurrencyCode
import dev.sargunv.mobilitydata.utils.Id
import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
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
  @SerialName("fare_id") public val fareId: Id<FareAttribute>,

  /** Fare price, in the unit specified by currency_type. */
  @SerialName("price") public val price: Float,

  /** Currency used to pay the fare. */
  @SerialName("currency_type") public val currencyType: CurrencyCode,

  /** Indicates when the fare must be paid. */
  @SerialName("payment_method") public val paymentMethod: PaymentMethod,

  /**
   * Indicates the number of transfers permitted on this fare. When null, unlimited transfers are
   * permitted.
   */
  @SerialName("transfers") public val transfers: Transfers? = null,

  /** Identifies the relevant agency for a fare. */
  @SerialName("agency_id") public val agencyId: Id<Agency>? = null,

  /** Length of time in seconds before a transfer expires. */
  @SerialName("transfer_duration") public val transferDuration: Int? = null,
) {
  init {
    require(price >= 0) { "Price must be non-negative, but was $price." }
    require(transferDuration == null || transferDuration >= 0) {
      "Transfer duration must be non-negative, but was $transferDuration."
    }
  }

  /** Returns the transfer duration as a [Duration] if specified. */
  public val transferDurationAsDuration: Duration?
    get() = transferDuration?.seconds
}

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

/** Indicates the number of transfers permitted on this fare. */
@Serializable
@JvmInline
public value class Transfers
private constructor(
  /** The integer value representing the number of transfers. */
  public val value: Int
) {
  /** Companion object containing predefined transfer constants. */
  public companion object {
    /** No transfers permitted on this fare. */
    public val None: Transfers = Transfers(0)

    /** Riders may transfer once. */
    public val Once: Transfers = Transfers(1)

    /** Riders may transfer twice. */
    public val Twice: Transfers = Transfers(2)
  }
}
