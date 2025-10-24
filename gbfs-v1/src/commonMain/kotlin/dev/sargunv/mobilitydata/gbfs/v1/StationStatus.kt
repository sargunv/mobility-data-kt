package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.EpochSeconds
import dev.sargunv.mobilitydata.utils.IntBoolean
import kotlin.time.ExperimentalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Describes the capacity and rental availability of a station. */
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
  @SerialName("station_id") public val stationId: String,

  /** Number of functional vehicles physically at the station that may be offered for rental. */
  @SerialName("num_bikes_available") public val numBikesAvailable: Int,

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
   */
  @SerialName("num_docks_available") public val numDocksAvailable: Int,

  /** Number of disabled dock points at the station. */
  @SerialName("num_docks_disabled") public val numDocksDisabled: Int? = null,

  /**
   * Is the station currently on the street?
   * - `true` - Station is installed on the street.
   * - `false` - Station is not installed on the street.
   */
  @SerialName("is_installed") public val isInstalled: IntBoolean,

  /**
   * Is the station currently renting vehicles?
   * - `true` - Station is renting vehicles. Even if the station is empty, if it would otherwise
   *   allow rentals, this value MUST be true.
   * - `false` - Station is not renting vehicles.
   */
  @SerialName("is_renting") public val isRenting: IntBoolean,

  /**
   * Is the station accepting vehicle returns?
   * - `true` - Station is accepting vehicle returns. Even if the station is full, if it would
   *   otherwise allow vehicle returns, this value MUST be true.
   * - `false` - Station is not accepting vehicle returns.
   */
  @SerialName("is_returning") public val isReturning: IntBoolean,

  /** The last time this station reported its status to the operator's backend. */
  @SerialName("last_reported") public val lastReported: EpochSeconds,
)
