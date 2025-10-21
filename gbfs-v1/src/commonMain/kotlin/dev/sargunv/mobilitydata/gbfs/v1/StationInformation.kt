package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.Uri
import dev.sargunv.mobilitydata.utils.Url
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Information about stations in the system.
 *
 * All stations included are considered public (meaning they can be shown on a map for public use).
 * Private stations SHOULD NOT be included.
 */
@Serializable
public data class StationInformation(
  /** Array that contains one object per station. */
  public val stations: List<Station>
) : GbfsFeedData, List<Station> by stations

/** Information about a single station. */
@Serializable
public data class Station(
  /** Identifier of a station. */
  @SerialName("station_id") public val stationId: Id<Station>,

  /**
   * The public name of the station for display in maps, digital signage, and other text
   * applications.
   */
  public val name: String,

  /** Short name or other type of identifier. */
  @SerialName("short_name") public val shortName: String? = null,

  /** Latitude of the station in decimal degrees. */
  public val lat: Double,

  /** Longitude of the station in decimal degrees. */
  public val lon: Double,

  /**
   * Address (street number and name) where station is located.
   *
   * This MUST be a valid address, not a free-form text description.
   */
  public val address: String? = null,

  /** Cross street or landmark where the station is located. */
  @SerialName("cross_street") public val crossStreet: String? = null,

  /** Identifier of the region where station is located. See system_regions.json. */
  @SerialName("region_id") public val regionId: Id<Region>? = null,

  /** Postal code where the station is located. */
  @SerialName("post_code") public val postalCode: String? = null,

  /** Payment methods accepted at this station. */
  @SerialName("rental_methods") public val rentalMethods: List<RentalMethod>? = null,

  /** Number of total docking points installed at this station, both available and unavailable. */
  public val capacity: Int? = null,

  /** Contains rental URIs for Android, iOS, and web. */
  @SerialName("rental_uris") public val rentalUris: RentalUris? = null,
)

/** Payment methods accepted at a station. */
@Serializable
@JvmInline
public value class RentalMethod(
  /** The string value representing the rental method. */
  public val value: String
) {
  /** Companion object containing predefined rental method constants. */
  public companion object {
    /** Operator issued vehicle key / fob / card. */
    public val Key: RentalMethod = RentalMethod("KEY")

    /** Credit card payment. */
    public val CreditCard: RentalMethod = RentalMethod("CREDITCARD")

    /** PayPass payment. */
    public val PayPass: RentalMethod = RentalMethod("PAYPASS")

    /** Apple Pay payment. */
    public val ApplePay: RentalMethod = RentalMethod("APPLEPAY")

    /** Android Pay payment. */
    public val AndroidPay: RentalMethod = RentalMethod("ANDROIDPAY")

    /** Transit card payment. */
    public val TransitCard: RentalMethod = RentalMethod("TRANSITCARD")

    /** Account number payment. */
    public val AccountNumber: RentalMethod = RentalMethod("ACCOUNTNUMBER")

    /** Phone payment. */
    public val Phone: RentalMethod = RentalMethod("PHONE")
  }
}

/** Contains rental URIs for different platforms to support deep linking. */
@Serializable
public data class RentalUris(
  /**
   * URI that can be passed to an Android app to support Android Deep Links.
   *
   * This URI SHOULD be a deep link specific to this station or vehicle. Android App Links are
   * preferred.
   */
  public val android: Uri? = null,

  /**
   * URI that can be used on iOS to launch the rental app.
   *
   * This URI SHOULD be a deep link specific to this station or vehicle. iOS Universal Links are
   * preferred.
   */
  public val ios: Uri? = null,

  /**
   * URL that can be used by a web browser to show more information about renting.
   *
   * This URL SHOULD be a deep link specific to this station or vehicle.
   */
  public val web: Url? = null,
)
