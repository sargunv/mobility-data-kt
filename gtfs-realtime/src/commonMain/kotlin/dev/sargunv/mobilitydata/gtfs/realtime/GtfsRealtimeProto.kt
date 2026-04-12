@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

private val gtfsRealtimeProtoBuf: ProtoBuf = ProtoBuf { encodeDefaults = false }

/** Binary GTFS Realtime protobuf codec. */
public object GtfsRealtimeProto {
  /** Decodes a GTFS Realtime feed message from protobuf bytes. */
  public fun decodeFeedMessage(bytes: ByteArray): FeedMessage =
    gtfsRealtimeProtoBuf.decodeFromByteArray(FeedMessage.serializer(), bytes)

  /** Encodes a GTFS Realtime feed message to protobuf bytes. */
  public fun encodeFeedMessage(feedMessage: FeedMessage): ByteArray =
    gtfsRealtimeProtoBuf.encodeToByteArray(FeedMessage.serializer(), feedMessage)
}
