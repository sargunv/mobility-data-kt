package dev.sargunv.mobilitydata.gofs.v1

import kotlinx.serialization.Serializable
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Polygon

// TODO: zone_id is a foreign member in the Feature object, not yet supported by Spatial-K
// https://github.com/MobilityData/GOFS/issues/44
// https://github.com/maplibre/spatial-k/issues/128

@Serializable
public data class Zones(public val zones: FeatureCollection<Polygon, Zone>) : GofsFeedData

@Serializable public data class Zone(public val name: String)
