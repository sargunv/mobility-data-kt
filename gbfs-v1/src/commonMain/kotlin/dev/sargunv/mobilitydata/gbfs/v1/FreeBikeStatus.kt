package dev.sargunv.mobilitydata.gbfs.v1

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.IntBoolean
import kotlin.time.ExperimentalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes all vehicles that are not currently in active rental. Required of systems that don't
 * utilize docks or offer bikes for rent outside of stations.
 */
@Serializable
public data class FreeBikeStatus(
  /** Array that contains one object per vehicle that is currently not part of an active rental. */
  public val bikes: List<Bike>
) : GbfsFeedData, List<Bike> by bikes

/** Represents a vehicle that is currently not part of an active rental. */
@OptIn(ExperimentalTime::class)
@Serializable
public data class Bike(

  /** Identifier of a vehicle. */
  @SerialName("bike_id") public val bikeId: Id<Bike>,

  /** Latitude of the vehicle in decimal degrees. */
  public val lat: Double,

  /** Longitude of the vehicle in decimal degrees. */
  public val lon: Double,

  /**
   * Is the vehicle currently reserved?
   * - `true` - Vehicle is currently reserved.
   * - `false` - Vehicle is not currently reserved.
   */
  @SerialName("is_reserved") public val isReserved: IntBoolean,

  /**
   * Is the vehicle currently disabled?
   * - `true` - Vehicle is currently disabled.
   * - `false` - Vehicle is not currently disabled.
   *
   * This field is used to indicate vehicles that are in the field but not available for rental.
   * This may be due to a mechanical issue, low battery, etc.
   */
  @SerialName("is_disabled") public val isDisabled: IntBoolean,

  /**
   * JSON object that contains rental URIs for Android, iOS, and web in the android, ios, and web
   * fields.
   */
  @SerialName("rental_uris") public val rentalUris: RentalUris? = null,
)
