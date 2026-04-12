package dev.sargunv.mobilitydata.gtfs.realtime

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class ResultErrorHandlingTest {
  @Test
  fun networkErrorReturnsFailure() = runTest {
    val engine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
    val client = GtfsRealtimeClient(engine)

    val result = client.getFeedMessage("https://example.com/trip_updates.pb")

    assertTrue(result.isFailure, "Expected failure for network error")
  }

  @Test
  fun notFoundReturnsFailure() = runTest {
    val engine = MockEngine { respondError(HttpStatusCode.NotFound) }
    val client = GtfsRealtimeClient(engine)

    val result = client.getFeedMessage("https://example.com/trip_updates.pb")

    assertTrue(result.isFailure, "Expected failure for 404 error")
  }
}
