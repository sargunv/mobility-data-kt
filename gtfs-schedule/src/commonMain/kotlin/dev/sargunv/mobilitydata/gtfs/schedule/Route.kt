package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.RgbColorTriplet
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Transit routes. A route is a group of trips that are displayed to riders as a single service.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#routestxt)
 */
@Serializable
public data class Route(
  /** Uniquely identifies a route. */
  @SerialName("route_id") public val routeId: Id<Route>,

  /** Agency for the specified route. */
  @SerialName("agency_id") public val agencyId: Id<Agency>? = null,

  /** Short name of a route. */
  @SerialName("route_short_name") public val routeShortName: String? = null,

  /** Full name of a route. */
  @SerialName("route_long_name") public val routeLongName: String? = null,

  /** Description of a route. */
  @SerialName("route_desc") public val routeDesc: String? = null,

  /** Type of transportation used on a route. */
  @SerialName("route_type") public val routeType: RouteType,

  /** URL of a web page about the particular route. */
  @SerialName("route_url") public val routeUrl: Url? = null,

  /** Route color designation. */
  @SerialName("route_color") public val routeColor: RgbColorTriplet? = null,

  /** Legible color to use for text drawn against route_color. */
  @SerialName("route_text_color") public val routeTextColor: RgbColorTriplet? = null,

  /**
   * Order in which route should be displayed. Routes with smaller values should be displayed first.
   */
  @SerialName("route_sort_order") public val routeSortOrder: Int? = null,

  /** Indicates continuous pickup behavior. */
  @SerialName("continuous_pickup") public val continuousPickup: ContinuousPickupDropOff? = null,

  /** Indicates continuous drop off behavior. */
  @SerialName("continuous_drop_off") public val continuousDropOff: ContinuousPickupDropOff? = null,

  /** Network ID to which a route belongs. */
  @SerialName("network_id") public val networkId: String? = null,
)

/**
 * Type of transportation used on a route.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#routestxt)
 */
@Serializable
public enum class RouteType {
  /** Tram, Streetcar, Light rail. */
  @SerialName("0") TRAM,

  /** Subway, Metro. */
  @SerialName("1") SUBWAY,

  /** Rail. */
  @SerialName("2") RAIL,

  /** Bus. */
  @SerialName("3") BUS,

  /** Ferry. */
  @SerialName("4") FERRY,

  /** Cable tram. */
  @SerialName("5") CABLE_TRAM,

  /** Aerial lift, suspended cable car. */
  @SerialName("6") AERIAL_LIFT,

  /** Funicular. */
  @SerialName("7") FUNICULAR,

  /** Trolleybus. */
  @SerialName("11") TROLLEYBUS,

  /** Monorail. */
  @SerialName("12") MONORAIL,
}

/**
 * Indicates continuous pickup or drop off behavior.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#routestxt)
 */
@Serializable
public enum class ContinuousPickupDropOff {
  /** Continuous stopping pickup/drop off. */
  @SerialName("0") CONTINUOUS,

  /** No continuous stopping pickup/drop off. */
  @SerialName("1") NO_CONTINUOUS,

  /** Must phone agency to arrange continuous stopping pickup/drop off. */
  @SerialName("2") PHONE_AGENCY,

  /** Must coordinate with driver to arrange continuous stopping pickup/drop off. */
  @SerialName("3") COORDINATE_WITH_DRIVER,
}

/** Placeholder for Network entity. */
public class Network
