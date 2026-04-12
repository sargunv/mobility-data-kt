package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.WholeSeconds
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Fare rules for transfers between legs of travel.
 *
 * This class represents a record in the fare_transfer_rules.txt file.
 */
@Serializable
public data class FareTransferRule(
  /** Identifies a group of pre-transfer fare leg rules. */
  @SerialName("from_leg_group_id") public val fromLegGroupId: String? = null,

  /** Identifies a group of post-transfer fare leg rules. */
  @SerialName("to_leg_group_id") public val toLegGroupId: String? = null,

  /** Defines how many consecutive transfers the transfer rule may be applied to. */
  @SerialName("transfer_count") public val transferCount: Int? = null,

  /** Defines the duration limit of the transfer. */
  @SerialName("duration_limit") public val durationLimit: WholeSeconds? = null,

  /** Defines the relative start and end of the duration limit. */
  @SerialName("duration_limit_type") public val durationLimitType: DurationLimitType? = null,

  /** Indicates the cost processing method of transferring between legs. */
  @SerialName("fare_transfer_type") public val fareTransferType: FareTransferType,

  /** The fare product required to transfer between two fare legs. */
  @SerialName("fare_product_id") public val fareProductId: String? = null,
)

/** Defines the relative start and end of a transfer duration limit. */
@Serializable
@JvmInline
public value class DurationLimitType
private constructor(
  /** The integer value representing the duration limit type. */
  public val value: Int
) {
  /** Companion object containing predefined duration limit type constants. */
  public companion object {
    /** Between departure of first leg and arrival of last leg. */
    public val DepartureToArrival: DurationLimitType = DurationLimitType(0)

    /** Between departure of first leg and departure of last leg. */
    public val DepartureToDeparture: DurationLimitType = DurationLimitType(1)

    /** Between arrival of first leg and departure of last leg. */
    public val ArrivalToDeparture: DurationLimitType = DurationLimitType(2)

    /** Between arrival of first leg and arrival of last leg. */
    public val ArrivalToArrival: DurationLimitType = DurationLimitType(3)
  }
}

/** Indicates the cost processing method of transferring between legs. */
@Serializable
@JvmInline
public value class FareTransferType
private constructor(
  /** The integer value representing the fare transfer type. */
  public val value: Int
) {
  /** Companion object containing predefined fare transfer type constants. */
  public companion object {
    /** From-leg fare plus transfer fare (A + AB). */
    public val FromLegPlusTransfer: FareTransferType = FareTransferType(0)

    /** From-leg fare plus transfer fare plus to-leg fare (A + AB + B). */
    public val FromLegPlusTransferPlusToLeg: FareTransferType = FareTransferType(1)

    /** Transfer fare only (AB). */
    public val TransferOnly: FareTransferType = FareTransferType(2)
  }
}
