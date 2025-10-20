package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.maplibre.spatialk.geojson.MultiPolygon

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
) : GbfsFeedData

/** Information about a single station. */
@Serializable
public data class Station(
  /** Identifier of a station. */
  @SerialName("station_id") public val stationId: Id<Station>,

  /**
   * The public name of the station for display in maps, digital signage, and other text
   * applications.
   *
   * Names SHOULD reflect the station location through the use of a cross street or local landmark.
   * Abbreviations SHOULD NOT be used unless a location is called by its abbreviated name.
   */
  public val name: String,

  /** Short name or other type of identifier. */
  @SerialName("short_name") public val shortName: String? = null,

  /**
   * Latitude of the station in decimal degrees.
   *
   * This field SHOULD have a precision of 6 decimal places (0.000001).
   */
  public val lat: Double,

  /**
   * Longitude of the station in decimal degrees.
   *
   * This field SHOULD have a precision of 6 decimal places (0.000001).
   */
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

  /** Payment methods accepted at this station. */
  @SerialName("rental_methods") public val rentalMethods: List<RentalMethod>? = null,

  /**
   * Is this station a location with or without smart dock technology?
   * - `true` - The station is a location without smart docking infrastructure. The station may be
   *   defined by a point (lat/lon) and/or station_area.
   * - `false` - The station consists of smart docking infrastructure (docks).
   *
   * This field SHOULD be published by mobility systems that have station locations without
   * standard, internet connected physical docking infrastructure.
   */
  @SerialName("is_virtual_station") public val isVirtualStation: Boolean? = null,

  /**
   * A GeoJSON MultiPolygon that describes the area of a virtual station.
   *
   * If station_area is supplied, then the record describes a virtual station. If lat/lon and
   * station_area are both defined, the lat/lon is the significant coordinate of the station. The
   * station_area takes precedence over any ride_allowed rules in overlapping geofencing_zones.
   */
  @SerialName("station_area") public val stationArea: MultiPolygon? = null,

  /** Type of parking station. */
  @SerialName("parking_type") public val parkingType: ParkingType? = null,

  /**
   * Are parking hoops present at this station?
   * - `true` - Parking hoops are present at this station.
   * - `false` - Parking hoops are not present at this station.
   *
   * Parking hoops are lockable devices that are used to secure a parking space to prevent parking
   * of unauthorized vehicles.
   */
  @SerialName("parking_hoop") public val parkingHoop: Boolean? = null,

  /** Contact phone of the station. */
  @SerialName("contact_phone") public val contactPhone: String? = null,

  /**
   * Number of total docking points installed at this station, both available and unavailable,
   * regardless of what vehicle types are allowed at each dock.
   *
   * If this is a virtual station, this number represents the total number of vehicles of all types
   * that can be parked at the virtual station.
   */
  public val capacity: Int? = null,

  /**
   * An object describing the parking capacity of virtual stations, where each key is a
   * vehicle_type_id and the value is a number representing the total number of vehicles of this
   * type that can park within the virtual station.
   */
  @SerialName("vehicle_capacity") public val vehicleCapacity: Map<String, Int>? = null,

  /**
   * An object describing the docking capacity of a station where each key is a vehicle_type_id and
   * the value is a number representing the total docking points installed at this station for the
   * specified vehicle type.
   */
  @SerialName("vehicle_type_capacity") public val vehicleTypeCapacity: Map<String, Int>? = null,

  /**
   * Are valet services provided at this station?
   * - `true` - Valet services are provided at this station.
   * - `false` - Valet services are not provided at this station.
   *
   * This field's boolean SHOULD be set to true during the hours which valet service is provided at
   * the station. Valet service is defined as providing unlimited capacity at a station.
   */
  @SerialName("is_valet_station") public val isValetStation: Boolean? = null,

  /**
   * Does the station support charging of electric vehicles?
   * - `true` - Electric vehicle charging is available at this station.
   * - `false` - Electric vehicle charging is not available at this station.
   */
  @SerialName("is_charging_station") public val isChargingStation: Boolean? = null,

  /** Contains rental URIs for Android, iOS, and web. */
  @SerialName("rental_uris") public val rentalUris: RentalUris? = null,
)

/** Payment methods accepted at a station. */
@Serializable
public enum class RentalMethod {
  /** Operator issued vehicle key / fob / card. */
  @SerialName("key") Key,

  /** Credit card payment. */
  @SerialName("creditcard") CreditCard,

  /** PayPass payment. */
  @SerialName("paypass") PayPass,

  /** Apple Pay payment. */
  @SerialName("applepay") ApplePay,

  /** Android Pay payment. */
  @SerialName("androidpay") AndroidPay,

  /** Transit card payment. */
  @SerialName("transitcard") TransitCard,

  /** Account number payment. */
  @SerialName("accountnumber") AccountNumber,

  /** Phone payment. */
  @SerialName("phone") Phone,
}

/** Type of parking station. */
@Serializable
public enum class ParkingType {
  /** Off-street parking lot. */
  @SerialName("parking_lot") ParkingLot,

  /** Curbside parking. */
  @SerialName("street_parking") StreetParking,

  /** Parking that is below street level, station may be non-communicating. */
  @SerialName("underground_parking") UndergroundParking,

  /** Park vehicle on sidewalk, out of the pedestrian right of way. */
  @SerialName("sidewalk_parking") SidewalkParking,

  /** Other type of parking. */
  @SerialName("other") Other,
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
  public val android: Url? = null,

  /**
   * URI that can be used on iOS to launch the rental app.
   *
   * This URI SHOULD be a deep link specific to this station or vehicle. iOS Universal Links are
   * preferred.
   */
  public val ios: Url? = null,

  /**
   * URL that can be used by a web browser to show more information about renting.
   *
   * This URL SHOULD be a deep link specific to this station or vehicle.
   */
  public val web: Url? = null,
)
