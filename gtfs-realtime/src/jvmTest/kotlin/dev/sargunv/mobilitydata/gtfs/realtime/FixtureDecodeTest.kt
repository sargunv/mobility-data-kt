package dev.sargunv.mobilitydata.gtfs.realtime

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class FixtureDecodeTest {
  @Test
  fun decodesSoundTransitAlertsFixture() {
    val bytes = readFixtureBytes("gtfs-realtime", "sound-transit", "alerts.pb")
    val feed = GtfsRealtimeProto.decodeFeedMessage(bytes)

    assertTrue(feed.entity.isNotEmpty(), "Expected at least one realtime entity")
    assertTrue(feed.entity.any { it.alert != null }, "Expected alert entities")
  }

  @Test
  fun decodesTriRailTripUpdatesFixture() {
    val bytes = readFixtureBytes("gtfs-realtime", "tri-rail", "trip_updates.pb")
    val feed = GtfsRealtimeProto.decodeFeedMessage(bytes)

    assertTrue(feed.entity.isNotEmpty(), "Expected at least one realtime entity")
    assertTrue(feed.entity.any { it.tripUpdate != null }, "Expected trip update entities")
  }

  @Test
  fun decodesTriRailVehiclePositionsFixture() {
    val bytes = readFixtureBytes("gtfs-realtime", "tri-rail", "vehicle_positions.pb")
    val feed = GtfsRealtimeProto.decodeFeedMessage(bytes)

    assertTrue(feed.entity.isNotEmpty(), "Expected at least one realtime entity")
    assertTrue(feed.entity.any { it.vehicle != null }, "Expected vehicle position entities")
  }

  @Test
  fun decodesTriRailAlertsFixture() {
    val bytes = readFixtureBytes("gtfs-realtime", "tri-rail", "alerts.pb")
    val feed = GtfsRealtimeProto.decodeFeedMessage(bytes)

    assertTrue(feed.entity.isNotEmpty(), "Expected at least one realtime entity")
    assertTrue(feed.entity.any { it.alert != null }, "Expected alert entities")
  }

  @Test
  fun clientDecodesFixturePayload() = runTest {
    val bytes = readFixtureBytes("gtfs-realtime", "tri-rail", "trip_updates.pb")
    val engine = MockEngine {
      respond(
        content = bytes,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType to listOf("application/x-protobuf")),
      )
    }
    val client = GtfsRealtimeClient(engine)

    val result = client.getFeedMessage("https://example.com/trip_updates.pb").getOrThrow()

    assertTrue(result.entity.any { it.tripUpdate != null }, "Expected trip update entities")
    assertEquals(result, GtfsRealtimeProto.decodeFeedMessage(bytes))
  }
}
