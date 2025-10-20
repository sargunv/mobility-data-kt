package dev.sargunv.mobilitydata.gofs.v1

import kotlinx.serialization.Serializable
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Polygon

// TODO: zone_id is a foreign member in the Feature object, not yet supported by Spatial-K
// https://github.com/MobilityData/GOFS/issues/44
// https://github.com/maplibre/spatial-k/issues/128

/**
 * Geographically defines the zones where the on-demand services are available to the riders.
 *
 * The zones are delineated with a FeatureCollection GeoJSON file. At least one zone must be
 * defined. All zones defined in this file are public information (i.e. all zones can be displayed
 * on a map available to anyone).
 */
@Serializable
public data class Zones(
  /** GeoJSON FeatureCollection containing zone features with polygon geometries. */
  public val zones: FeatureCollection<Polygon, Zone>
) : GofsFeedData

/** Properties for a zone feature. */
@Serializable
public data class Zone(
  /** Indicates the name of the zone as displayed to the riders. */
  public val name: String
)
