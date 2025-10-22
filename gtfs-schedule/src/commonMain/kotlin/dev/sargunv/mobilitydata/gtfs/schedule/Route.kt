package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.RgbColorTriplet
import dev.sargunv.mobilitydata.utils.Url
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Transit routes. A route is a group of trips that are displayed to riders as a single service.
 *
 * This class represents a record in the routes.txt file.
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
  @SerialName("network_id") public val networkId: Id<Network>? = null,

  /**
   * Indicates if riders can access a transit service associated with this route by using a
   * contactless EMV card or mobile device as fare media at a fare validator.
   */
  @SerialName("cemv_support") public val cemvSupport: TriState? = null,
)

/** Type of transportation used on a route. */
@Serializable
@JvmInline
public value class RouteType
private constructor(
  /** The integer value representing the route type. */
  public val value: Int
) {
  /** Companion object containing predefined route type constants. */
  public companion object {
    /** Tram, Streetcar, Light rail. */
    public val Tram: RouteType = RouteType(0)

    /** Subway, Metro. */
    public val Subway: RouteType = RouteType(1)

    /** Rail. */
    public val Rail: RouteType = RouteType(2)

    /** Bus. */
    public val Bus: RouteType = RouteType(3)

    /** Ferry. */
    public val Ferry: RouteType = RouteType(4)

    /** Cable tram. */
    public val CableTram: RouteType = RouteType(5)

    /** Aerial lift, suspended cable car. */
    public val AerialLift: RouteType = RouteType(6)

    /** Funicular. */
    public val Funicular: RouteType = RouteType(7)

    /** Trolleybus. */
    public val Trolleybus: RouteType = RouteType(11)

    /** Monorail. */
    public val Monorail: RouteType = RouteType(12)
  }
}

/** Indicates continuous pickup or drop off behavior. */
@Serializable
@JvmInline
public value class ContinuousPickupDropOff
private constructor(
  /** The integer value representing the continuous pickup/drop off type. */
  public val value: Int
) {
  /** Companion object containing predefined continuous pickup/drop off constants. */
  public companion object {
    /** Continuous stopping pickup/drop off. */
    public val Continuous: ContinuousPickupDropOff = ContinuousPickupDropOff(0)

    /** No continuous stopping pickup/drop off. */
    public val NoContinuous: ContinuousPickupDropOff = ContinuousPickupDropOff(1)

    /** Must phone agency to arrange continuous stopping pickup/drop off. */
    public val PhoneAgency: ContinuousPickupDropOff = ContinuousPickupDropOff(2)

    /** Must coordinate with driver to arrange continuous stopping pickup/drop off. */
    public val CoordinateWithDriver: ContinuousPickupDropOff = ContinuousPickupDropOff(3)
  }
}
