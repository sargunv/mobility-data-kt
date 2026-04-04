package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the different fare media that can be employed to use fare products.
 *
 * This class represents a record in the fare_media.txt file.
 */
@Serializable
public data class FareMedia(
  /** Identifies a fare media. */
  @SerialName("fare_media_id") public val fareMediaId: String,

  /** Name of the fare media. */
  @SerialName("fare_media_name") public val fareMediaName: String? = null,

  /** The type of fare media. */
  @SerialName("fare_media_type") public val fareMediaType: FareMediaType,
)

/** Type of fare media. */
@Serializable
@JvmInline
public value class FareMediaType
private constructor(
  /** The integer value representing the fare media type. */
  public val value: Int
) {
  /** Companion object containing predefined fare media type constants. */
  public companion object {
    /**
     * None. Used when there is no fare media involved in purchasing or validating a fare product.
     */
    public val None: FareMediaType = FareMediaType(0)

    /** Physical paper ticket. */
    public val PhysicalPaperTicket: FareMediaType = FareMediaType(1)

    /** Physical transit card. */
    public val PhysicalTransitCard: FareMediaType = FareMediaType(2)

    /** cEMV (contactless Europay, Mastercard and Visa). */
    public val cEMV: FareMediaType = FareMediaType(3)

    /** Mobile app. */
    public val MobileApp: FareMediaType = FareMediaType(4)
  }
}
