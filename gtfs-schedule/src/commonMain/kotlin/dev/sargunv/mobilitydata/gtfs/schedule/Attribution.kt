package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.IntBoolean
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Dataset attributions.
 *
 * This class represents a record in the attributions.txt file.
 */
@Serializable
public data class Attribution(
  /**
   * Identifies an attribution for the dataset or a subset of it. This is mostly useful for
   * translations.
   */
  @SerialName("attribution_id") public val attributionId: Id<Attribution>? = null,

  /**
   * Agency to which the attribution applies.
   *
   * If one agency_id, route_id, or trip_id attribution is defined, the other ones must be empty. If
   * none of them is specified, the attribution will apply to the whole dataset.
   */
  @SerialName("agency_id") public val agencyId: Id<Agency>? = null,

  /**
   * Functions in the same way as agency_id except the attribution applies to a route. Multiple
   * attributions may apply to the same route.
   */
  @SerialName("route_id") public val routeId: Id<Route>? = null,

  /**
   * Functions in the same way as agency_id except the attribution applies to a trip. Multiple
   * attributions may apply to the same trip.
   */
  @SerialName("trip_id") public val tripId: Id<Trip>? = null,

  /** Name of the organization that the dataset is attributed to. */
  @SerialName("organization_name") public val organizationName: String,

  /**
   * The role of the organization is producer.
   *
   * At least one of the fields is_producer, is_operator, or is_authority should be set at 1.
   */
  @SerialName("is_producer") public val isProducer: IntBoolean? = null,

  /** Functions in the same way as is_producer except the role of the organization is operator. */
  @SerialName("is_operator") public val isOperator: IntBoolean? = null,

  /** Functions in the same way as is_producer except the role of the organization is authority. */
  @SerialName("is_authority") public val isAuthority: IntBoolean? = null,

  /** URL of the organization. */
  @SerialName("attribution_url") public val attributionUrl: Url? = null,

  /** Email of the organization. */
  @SerialName("attribution_email") public val attributionEmail: String? = null,

  /** Phone number of the organization. */
  @SerialName("attribution_phone") public val attributionPhone: String? = null,
)
