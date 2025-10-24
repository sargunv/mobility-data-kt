package dev.sargunv.mobilitydata.gtfs.schedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.PolygonGeometry

/**
 * Defines zones where riders can request either pickup or drop off by on-demand services.
 *
 * This type alias represents the content of a locations.geojson file.
 */
public typealias Locations = FeatureCollection<PolygonGeometry, LocationProperties>

/** Defines a zone where riders can request either pickup or drop off by on-demand services. */
public typealias Location = Feature<PolygonGeometry, LocationProperties>

@Serializable
public data class LocationProperties(
  @SerialName("stop_name") public val stopName: String? = null,
  @SerialName("stop_desc") public val stopDesc: String? = null,
)
