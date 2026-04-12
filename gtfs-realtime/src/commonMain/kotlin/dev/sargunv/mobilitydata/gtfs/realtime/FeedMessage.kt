@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("UndocumentedPublicProperty")

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/** The root GTFS Realtime feed payload. */
@Serializable
public data class FeedMessage(
  @ProtoNumber(1) public val header: FeedHeader,
  @ProtoNumber(2) public val entity: List<FeedEntity> = emptyList(),
)

/** Feed-level metadata. */
@Serializable
public data class FeedHeader(
  @ProtoNumber(1) public val gtfsRealtimeVersion: String,
  @ProtoNumber(2) public val incrementality: Incrementality = Incrementality.FullDataset,
  @ProtoNumber(3) public val timestamp: Long? = null,
  @ProtoNumber(4) public val feedVersion: String? = null,
) {
  /** Whether this update is a full snapshot or a differential update. */
  @Serializable
  public enum class Incrementality {
    @ProtoNumber(0) FullDataset,
    @ProtoNumber(1) Differential,
  }
}

/** A single realtime entity in the feed. */
@Serializable
public data class FeedEntity(
  @ProtoNumber(1) public val id: String,
  @ProtoNumber(2) public val isDeleted: Boolean = false,
  @ProtoNumber(3) public val tripUpdate: TripUpdate? = null,
  @ProtoNumber(4) public val vehicle: VehiclePosition? = null,
  @ProtoNumber(5) public val alert: Alert? = null,
  @ProtoNumber(6) public val shape: Shape? = null,
  @ProtoNumber(7) public val stop: Stop? = null,
  @ProtoNumber(8) public val tripModifications: TripModifications? = null,
)
