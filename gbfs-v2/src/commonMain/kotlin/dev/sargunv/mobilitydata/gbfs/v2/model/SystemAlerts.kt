package dev.sargunv.mobilitydata.gbfs.v2.model

import dev.sargunv.mobilitydata.gbfs.v2.serialization.EpochSecondsSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Informs customers about changes to the system that do not fall within the normal system
 * operations.
 *
 * For example, system closures due to weather would be listed here, but a system that only operated
 * for part of the year would have that schedule listed in system_calendar.json. Obsolete alerts
 * SHOULD be removed so the client application can safely present to the end user everything present
 * in the feed.
 */
@Serializable
public data class SystemAlerts(
  /** Array of objects each indicating a system alert. */
  public val alerts: List<Alert>
) : GbfsFeedData, List<Alert> by alerts

/** An alert about changes to the system. */
@OptIn(ExperimentalTime::class)
@Serializable
public data class Alert(
  /** Identifier for this alert. */
  @SerialName("alert_id") public val alertId: Id<Alert>,

  /** Type of alert. */
  public val type: AlertType,

  /**
   * Array of objects indicating when the alert is in effect.
   *
   * For example, when the system or station is actually closed, or when a station is scheduled to
   * be moved.
   */
  public val times: List<AlertTime>? = null,

  /**
   * If this is an alert that affects one or more stations, their IDs.
   *
   * If both station_ids and region_ids are omitted, this alert affects the entire system.
   */
  @SerialName("station_ids") public val stationIds: List<Id<Station>>? = null,

  /**
   * If this system has regions, and if this alert only affects certain regions, their IDs.
   *
   * If both station_ids and region_ids are omitted, this alert affects the entire system.
   */
  @SerialName("region_ids") public val regionIds: List<Id<Region>>? = null,

  /** URL where the customer can learn more information about this alert. */
  public val url: Url? = null,

  /** A short summary of this alert to be displayed to the customer. */
  public val summary: String,

  /** Detailed description of the alert. */
  public val description: String? = null,

  /** Indicates the last time the info for the alert was updated. */
  @Serializable(with = EpochSecondsSerializer::class)
  @SerialName("last_updated")
  public val lastUpdated: Instant? = null,
)

/** Type of system alert. */
@Serializable
public enum class AlertType {
  /** System closure alert. */
  @SerialName("system_closure") SystemClosure,

  /** Station closure alert. */
  @SerialName("station_closure") StationClosure,

  /** Station move alert. */
  @SerialName("station_move") StationMove,

  /** Other type of alert. */
  @SerialName("other") Other,
}

/** Time period when an alert is in effect. */
@OptIn(ExperimentalTime::class)
@Serializable
public data class AlertTime(
  /** Start time of the alert. REQUIRED if times array is defined. */
  @Serializable(with = EpochSecondsSerializer::class) public val start: Instant,

  /**
   * End time of the alert.
   *
   * If there is currently no end time planned for the alert, this can be omitted.
   */
  @Serializable(with = EpochSecondsSerializer::class) public val end: Instant? = null,
)
