package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.assertEquals

internal fun assertFeedRoundTrips(feedMessage: FeedMessage) {
  val encoded = GtfsRealtimeProto.encodeFeedMessage(feedMessage)
  val decoded = GtfsRealtimeProto.decodeFeedMessage(encoded)

  assertEquals(feedMessage, decoded)
}
