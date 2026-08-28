@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf

/** Binary GTFS Realtime protobuf codec. */
public object GtfsRealtimeProto {
  private val protoBuf: ProtoBuf = ProtoBuf { encodeDefaults = false }

  /** Decodes a GTFS Realtime feed message from protobuf bytes. */
  public fun decodeFeedMessage(bytes: ByteArray): FeedMessage {
    try {
      return protoBuf.decodeFromByteArray(FeedMessage.serializer(), bytes)
    } finally {
      Proto2EnumDecodeSession.clear()
    }
  }

  /** Encodes a GTFS Realtime feed message to protobuf bytes. */
  public fun encodeFeedMessage(feedMessage: FeedMessage): ByteArray =
    protoBuf.encodeToByteArray(FeedMessage.serializer(), feedMessage)
}
