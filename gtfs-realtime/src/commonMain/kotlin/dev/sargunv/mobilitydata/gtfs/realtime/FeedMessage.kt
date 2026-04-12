@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/** The root GTFS Realtime feed payload. */
@Serializable
public data class FeedMessage(
  /** Feed-level metadata. */
  @ProtoNumber(1) public val header: FeedHeader,
  /** Realtime entities in the feed. */
  @ProtoNumber(2) public val entity: List<FeedEntity> = emptyList(),
)

/** Feed-level metadata. */
@Serializable
public data class FeedHeader(
  /** Version of the GTFS Realtime spec (e.g. `"2.0"`). */
  @ProtoNumber(1) public val gtfsRealtimeVersion: String,
  /** Whether this feed is a full snapshot or a differential update. */
  @ProtoNumber(2) public val incrementality: Incrementality = Incrementality.FullDataset,
  /** POSIX timestamp when the feed content was created. */
  @ProtoNumber(3) public val timestamp: Long? = null,
  /** Publisher-defined version string for the feed. */
  @ProtoNumber(4) public val feedVersion: String? = null,
) {
  /** Whether this feed is a full snapshot or a differential update. */
  @Suppress("UndocumentedPublicProperty")
  @Serializable
  public enum class Incrementality {
    @ProtoNumber(0) FullDataset,
    @ProtoNumber(1) Differential,
  }
}

/** A single realtime entity in the feed. */
@Serializable
public data class FeedEntity(
  /** Feed-unique identifier for this entity. */
  @ProtoNumber(1) public val id: String,
  /** Whether this entity should be deleted (differential feeds only). */
  @ProtoNumber(2) public val isDeleted: Boolean = false,
  /** Realtime trip progress data. */
  @ProtoNumber(3) public val tripUpdate: TripUpdate? = null,
  /** Realtime vehicle position data. */
  @ProtoNumber(4) public val vehicle: VehiclePosition? = null,
  /** Service alert data. */
  @ProtoNumber(5) public val alert: Alert? = null,
  /** Realtime shape data. */
  @ProtoNumber(6) public val shape: Shape? = null,
  /** Dynamically added stop data. */
  @ProtoNumber(7) public val stop: Stop? = null,
  /** Trip modification data such as detours. */
  @ProtoNumber(8) public val tripModifications: TripModifications? = null,
)
