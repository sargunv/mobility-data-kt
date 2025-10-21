package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.LocalizedText
import dev.sargunv.mobilitydata.utils.LocalizedUrl
import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.jvm.JvmInline
import kotlin.time.ExperimentalTime
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
  public val url: LocalizedUrl? = null,

  /** A short summary of this alert to be displayed to the customer. */
  public val summary: LocalizedText,

  /** Detailed description of the alert. */
  public val description: LocalizedText? = null,

  /** Indicates the last time the info for the alert was updated. */
  @SerialName("last_updated") public val lastUpdated: Timestamp? = null,
)

/** Type of system alert. */
@Serializable
@JvmInline
public value class AlertType(
  /** The string value representing the alert type. */
  public val value: String
) {
  /** Companion object containing predefined alert type constants. */
  public companion object {
    /** System closure alert. */
    public val SystemClosure: AlertType = AlertType("system_closure")

    /** Station closure alert. */
    public val StationClosure: AlertType = AlertType("station_closure")

    /** Station move alert. */
    public val StationMove: AlertType = AlertType("station_move")

    /** Other type of alert. */
    public val Other: AlertType = AlertType("other")
  }
}

/** Time period when an alert is in effect. */
@OptIn(ExperimentalTime::class)
@Serializable
public data class AlertTime(
  /** Start time of the alert. REQUIRED if times array is defined. */
  public val start: Timestamp,

  /**
   * End time of the alert.
   *
   * If there is currently no end time planned for the alert, this can be omitted.
   */
  public val end: Timestamp? = null,
)
