package dev.sargunv.mobilitydata.mdb.v1

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/** Catalog `data_type` discriminator: `gtfs`, `gtfs_rt`, or `gbfs`. */
@Serializable
@JvmInline
public value class FeedDataType(
  /** Wire value. */
  public val value: String
) {
  /** Named constants for [FeedDataType]. */
  public companion object {
    /** A static GTFS feed. */
    public val Gtfs: FeedDataType = FeedDataType("gtfs")

    /** A GTFS Realtime feed. */
    public val GtfsRt: FeedDataType = FeedDataType("gtfs_rt")

    /** A GBFS feed. */
    public val Gbfs: FeedDataType = FeedDataType("gbfs")
  }
}
