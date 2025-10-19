package dev.sargunv.mobilitydata.gbfs.v2

import dev.sargunv.mobilitydata.gbfs.v2.serialization.EpochSecondsSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.MultiPolygon

/**
 * Describes geofencing zones and their associated rules and attributes.
 *
 * Geofenced areas are delineated using GeoJSON in accordance with RFC 7946. By default, no
 * restrictions apply everywhere. Geofencing zones SHOULD be modeled according to restrictions
 * rather than allowance.
 */
@Serializable
public data class GeofencingZones(
  /**
   * Each geofenced zone and its associated rules and attributes is described as an object within
   * the array of features.
   */
  @SerialName("geofencing_zones")
  public val geofencingZones: FeatureCollection<MultiPolygon, GeofencingZone>
) : GbfsFeedData

/** Properties describing travel allowances and limitations for a geofencing zone. */
@OptIn(ExperimentalTime::class)
@Serializable
public data class GeofencingZone(

  /** Public name of the geofencing zone. */
  public val name: String? = null,

  /**
   * Start time of the geofencing zone.
   *
   * If the geofencing zone is always active, this can be omitted.
   */
  @Serializable(with = EpochSecondsSerializer::class) public val start: Instant? = null,

  /**
   * End time of the geofencing zone.
   *
   * If the geofencing zone is always active, this can be omitted.
   */
  @Serializable(with = EpochSecondsSerializer::class) public val end: Instant? = null,

  /**
   * Array that contains one object per rule.
   *
   * In the event of colliding rules within the same polygon, the earlier rule (in order of the JSON
   * file) takes precedence. In the case of overlapping polygons, the combined set of rules
   * associated with the overlapping polygons applies to the union of the polygons.
   */
  public val rules: List<GeofencingZoneRule>? = null,
)

/** A rule defining restrictions for a geofencing zone. */
@Serializable
public data class GeofencingZoneRule(

  /**
   * Array of IDs of vehicle types for which any restrictions SHOULD be applied.
   *
   * If vehicle type IDs are not specified, then restrictions apply to all vehicle types.
   */
  @SerialName("vehicle_type_id") public val vehicleTypeId: List<Id<VehicleType>>? = null,

  /**
   * Is the undocked ("free floating") ride allowed to start and end in this zone?
   *
   * REQUIRED if rules array is defined.
   * - `true` - Undocked ("free floating") ride can start and end in this zone.
   * - `false` - Undocked ("free floating") ride cannot start or end in this zone.
   */
  @SerialName("ride_allowed") public val rideAllowed: Boolean,

  /**
   * Is the ride allowed to travel through this zone?
   *
   * REQUIRED if rules array is defined.
   * - `true` - Ride can travel through this zone.
   * - `false` - Ride cannot travel through this zone.
   */
  @SerialName("ride_through_allowed") public val rideThroughAllowed: Boolean,

  /**
   * What is the maximum speed allowed, in kilometers per hour?
   *
   * If there is no maximum speed to observe, this can be omitted.
   */
  @SerialName("maximum_speed_kph") public val maximumSpeedKph: Int? = null,

  /**
   * Can vehicles only be parked at stations defined in station_information.json within this
   * geofence zone?
   * - `true` - Vehicles can only be parked at stations.
   * - `false` - Vehicles may be parked outside of stations.
   */
  @SerialName("station_parking") public val stationParking: Boolean? = null,
)
