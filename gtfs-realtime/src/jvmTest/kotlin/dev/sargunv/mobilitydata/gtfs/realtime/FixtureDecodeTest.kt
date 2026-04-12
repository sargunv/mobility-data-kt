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
  private fun assertFixtureDecodes(
    vararg path: String,
    entityCheck: (FeedEntity) -> Boolean,
    entityLabel: String,
  ) {
    val bytes = readFixtureBytes(*path)
    val feed = GtfsRealtimeProto.decodeFeedMessage(bytes)
    assertTrue(feed.entity.isNotEmpty(), "Expected at least one realtime entity")
    assertTrue(feed.entity.any(entityCheck), "Expected $entityLabel entities")
  }

  @Test
  fun decodesSoundTransitAlertsFixture() =
    assertFixtureDecodes(
      "gtfs-realtime",
      "sound-transit",
      "alerts.pb",
      entityCheck = { it.alert != null },
      entityLabel = "alert",
    )

  @Test
  fun decodesTriRailTripUpdatesFixture() =
    assertFixtureDecodes(
      "gtfs-realtime",
      "tri-rail",
      "trip_updates.pb",
      entityCheck = { it.tripUpdate != null },
      entityLabel = "trip update",
    )

  @Test
  fun decodesTriRailVehiclePositionsFixture() =
    assertFixtureDecodes(
      "gtfs-realtime",
      "tri-rail",
      "vehicle_positions.pb",
      entityCheck = { it.vehicle != null },
      entityLabel = "vehicle position",
    )

  @Test
  fun decodesTriRailAlertsFixture() =
    assertFixtureDecodes(
      "gtfs-realtime",
      "tri-rail",
      "alerts.pb",
      entityCheck = { it.alert != null },
      entityLabel = "alert",
    )

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
