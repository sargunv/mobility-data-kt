package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.utils.serialization.EpochSecondsSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the capacity and rental availability of a station.
 *
 * Data returned SHOULD be as close to realtime as possible, but in no case should it be more than 5
 * minutes out-of-date.
 */
@Serializable
public data class StationStatus(
  /** Array that contains one object per station in the system. */
  public val stations: List<StationStatusEntry>
) : GbfsFeedData, List<StationStatusEntry> by stations

/** Real-time status information for a single station. */
@OptIn(ExperimentalTime::class)
@Serializable
public data class StationStatusEntry(
  /** Identifier of a station. See station_information.json. */
  @SerialName("station_id") public val stationId: Id<Station>,

  /**
   * Number of functional vehicles physically at the station that may be offered for rental.
   *
   * If is_renting = true, this is the number of vehicles that are currently available for rent. If
   * is_renting = false, this is the number of vehicles that would be available for rent if the
   * station were set to allow rentals.
   */
  @SerialName("num_bikes_available") public val numBikesAvailable: Int,

  /**
   * Array of objects used to model the total number of each defined vehicle type available at a
   * station.
   *
   * REQUIRED if the vehicle_types.json file has been defined. The total number of vehicles from
   * each of these objects SHOULD add up to match the value specified in num_bikes_available.
   */
  @SerialName("vehicle_types_available")
  public val vehicleTypesAvailable: List<VehicleTypeAvailability>? = null,

  /**
   * Number of disabled vehicles of any type at the station.
   *
   * Vendors who do not want to publicize the number of disabled vehicles can opt to omit this
   * field.
   */
  @SerialName("num_bikes_disabled") public val numBikesDisabled: Int? = null,

  /**
   * Number of functional docks physically at the station that are able to accept vehicles for
   * return.
   *
   * REQUIRED except for stations that have unlimited docking capacity. If is_returning = true, this
   * is the number of docks that are currently available to accept vehicle returns. If is_returning
   * = false, this is the number of docks that would be available if the station were set to allow
   * returns.
   */
  @SerialName("num_docks_available") public val numDocksAvailable: Int? = null,

  /**
   * Array of objects used to model the number of docks available for certain vehicle types.
   *
   * REQUIRED in feeds where certain docks are only able to accept certain vehicle types. The total
   * number of docks from each of these objects SHOULD add up to match num_docks_available.
   */
  @SerialName("vehicle_docks_available")
  public val vehicleDocksAvailable: List<VehicleDockAvailability>? = null,

  /** Number of disabled dock points at the station. */
  @SerialName("num_docks_disabled") public val numDocksDisabled: Int? = null,

  /**
   * Is the station currently on the street?
   * - `true` - Station is installed on the street.
   * - `false` - Station is not installed on the street.
   *
   * Boolean SHOULD be set to true when equipment is present on the street. In seasonal systems,
   * boolean SHOULD be set to false during the off season.
   */
  @SerialName("is_installed") public val isInstalled: Boolean,

  /**
   * Is the station currently renting vehicles?
   * - `true` - Station is renting vehicles. Even if the station is empty, if it would otherwise
   *   allow rentals, this value MUST be true.
   * - `false` - Station is not renting vehicles.
   *
   * If the station is temporarily taken out of service and not allowing rentals, this field MUST be
   * set to false.
   */
  @SerialName("is_renting") public val isRenting: Boolean,

  /**
   * Is the station accepting vehicle returns?
   * - `true` - Station is accepting vehicle returns. Even if the station is full, if it would
   *   otherwise allow vehicle returns, this value MUST be true.
   * - `false` - Station is not accepting vehicle returns.
   *
   * If the station is temporarily taken out of service and not allowing vehicle returns, this field
   * MUST be set to false.
   */
  @SerialName("is_returning") public val isReturning: Boolean,

  /** The last time this station reported its status to the operator's backend. */
  @Serializable(with = EpochSecondsSerializer::class)
  @SerialName("last_reported")
  public val lastReported: Instant,
)

/** The total number of available vehicles of a specific type at a station. */
@Serializable
public data class VehicleTypeAvailability(
  /** The vehicle_type_id of this vehicle type as described in vehicle_types.json. */
  @SerialName("vehicle_type_id") public val vehicleTypeId: Id<VehicleType>,

  /** The total number of available vehicles of the corresponding vehicle_type_id at the station. */
  public val count: Int,
)

/** The number of docks available for specific vehicle types. */
@Serializable
public data class VehicleDockAvailability(
  /**
   * An array of vehicle_type_id values that are able to use a particular type of dock at the
   * station.
   */
  @SerialName("vehicle_type_ids") public val vehicleTypeIds: List<Id<VehicleType>>,

  /**
   * The total number of available docks at the station that can accept vehicles of the
   * corresponding vehicle_type_ids.
   */
  public val count: Int,
)
