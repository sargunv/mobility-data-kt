package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Stops where vehicles pick up or drop off riders.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#stopstxt)
 */
@Serializable
public data class Stop(
  /** Uniquely identifies a stop, station, or station entrance. */
  @SerialName("stop_id") public val stopId: Id<Stop>,

  /** Short text or a number that identifies the location for riders. */
  @SerialName("stop_code") public val stopCode: String? = null,

  /** Name of the location. */
  @SerialName("stop_name") public val stopName: String? = null,

  /** Readable version of the stop_name field. */
  @SerialName("tts_stop_name") public val ttsStopName: String? = null,

  /** Description of the location. */
  @SerialName("stop_desc") public val stopDesc: String? = null,

  /** Latitude of the location. */
  @SerialName("stop_lat") public val stopLat: Double? = null,

  /** Longitude of the location. */
  @SerialName("stop_lon") public val stopLon: Double? = null,

  /** Identifies the fare zone for a stop. */
  @SerialName("zone_id") public val zoneId: String? = null,

  /** URL of a web page about the location. */
  @SerialName("stop_url") public val stopUrl: Url? = null,

  /** Type of the location. */
  @SerialName("location_type") public val locationType: LocationType? = null,

  /** Defines hierarchy between the different locations. */
  @SerialName("parent_station") public val parentStation: Id<Stop>? = null,

  /** Timezone of the location. */
  @SerialName("stop_timezone") public val stopTimezone: String? = null,

  /** Indicates whether wheelchair boardings are possible from the location. */
  @SerialName("wheelchair_boarding") public val wheelchairBoarding: WheelchairBoarding? = null,

  /** Level of the location. */
  @SerialName("level_id") public val levelId: String? = null,

  /** Platform identifier for a platform stop. */
  @SerialName("platform_code") public val platformCode: String? = null,
)

/**
 * Type of the location.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#stopstxt)
 */
@Serializable
public enum class LocationType {
  /** Stop (or Platform). A location where passengers board or disembark from a transit vehicle. */
  @SerialName("0") STOP,

  /** Station. A physical structure or area that contains one or more platforms. */
  @SerialName("1") STATION,

  /** Entrance/Exit. A location where passengers can enter or exit a station. */
  @SerialName("2") ENTRANCE_EXIT,

  /** Generic Node. A location within a station, not matching any other location_type. */
  @SerialName("3") GENERIC_NODE,

  /** Boarding Area. A specific location on a platform. */
  @SerialName("4") BOARDING_AREA,
}

/**
 * Indicates whether wheelchair boardings are possible from the location.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#stopstxt)
 */
@Serializable
public enum class WheelchairBoarding {
  /** No accessibility information for the stop. */
  @SerialName("0") NO_INFO,

  /** Some vehicles at this stop can be boarded by a rider in a wheelchair. */
  @SerialName("1") ACCESSIBLE,

  /** Wheelchair boarding is not possible at this stop. */
  @SerialName("2") NOT_ACCESSIBLE,
}

/** Placeholder for Zone entity. */
public class Zone

/** Placeholder for Level entity. */
public class Level
