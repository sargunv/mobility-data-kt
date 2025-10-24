package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Url
import kotlin.jvm.JvmInline
import kotlinx.datetime.TimeZone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Stops where vehicles pick up or drop off riders.
 *
 * This class represents a record in the stops.txt file.
 */
@Serializable
public data class Stop(
  /** Uniquely identifies a stop, station, or station entrance. */
  @SerialName("stop_id") public val stopId: String,

  /** Short text or a number that identifies the location for riders. */
  @SerialName("stop_code") public val stopCode: String? = null,

  /** Name of the location. */
  @SerialName("stop_name") public val stopName: String? = null,

  /** Readable version of the stop_name field. */
  @SerialName("tts_stop_name") public val ttsStopName: String? = null,

  /** Description of the location. */
  @SerialName("stop_desc") public val stopDescription: String? = null,

  /** Latitude of the location. */
  @SerialName("stop_lat") public val stopLatitude: Double? = null,

  /** Longitude of the location. */
  @SerialName("stop_lon") public val stopLongitude: Double? = null,

  /** Identifies the fare zone for a stop. */
  @SerialName("zone_id") public val zoneId: String? = null,

  /** URL of a web page about the location. */
  @SerialName("stop_url") public val stopUrl: Url? = null,

  /** Type of the location. */
  @SerialName("location_type") public val locationType: LocationType? = null,

  /** Defines hierarchy between the different locations. */
  @SerialName("parent_station") public val parentStation: String? = null,

  /** Timezone of the location. */
  @SerialName("stop_timezone") public val stopTimezone: TimeZone? = null,

  /** Indicates whether wheelchair boardings are possible from the location. */
  @SerialName("wheelchair_boarding") public val wheelchairBoarding: TriState? = null,

  /** Level of the location. */
  @SerialName("level_id") public val levelId: String? = null,

  /** Platform identifier for a platform stop. */
  @SerialName("platform_code") public val platformCode: String? = null,

  /** Indicates how the stop is accessed for a particular station. */
  @SerialName("stop_access") public val stopAccess: StopAccess? = null,
)

/** Type of the location. */
@Serializable
@JvmInline
public value class LocationType
private constructor(
  /** The integer value representing the location type. */
  public val value: Int
) {
  /** Companion object containing predefined location type constants. */
  public companion object {
    /**
     * Stop (or Platform). A location where passengers board or disembark from a transit vehicle.
     */
    public val Stop: LocationType = LocationType(0)

    /** Station. A physical structure or area that contains one or more platforms. */
    public val Station: LocationType = LocationType(1)

    /** Entrance/Exit. A location where passengers can enter or exit a station. */
    public val EntranceOrExit: LocationType = LocationType(2)

    /** Generic Node. A location within a station, not matching any other location_type. */
    public val GenericNode: LocationType = LocationType(3)

    /** Boarding Area. A specific location on a platform. */
    public val BoardingArea: LocationType = LocationType(4)
  }
}

/** Indicates how the stop is accessed for a particular station. */
@Serializable
@JvmInline
public value class StopAccess
private constructor(
  /** The integer value representing the stop access type. */
  public val value: Int
) {
  /** Companion object containing predefined stop access constants. */
  public companion object {
    /**
     * The stop/platform cannot be directly accessed from the street network. It must be accessed
     * from a station entrance if there is one defined for the station, otherwise the station
     * itself. If there are pathways defined for the station, they must be used to access the
     * stop/platform.
     */
    public val Indirect: StopAccess = StopAccess(0)

    /**
     * Consuming applications should generate directions for access directly to the stop,
     * independent of any entrances or pathways of the parent station.
     */
    public val Direct: StopAccess = StopAccess(1)
  }
}
