package dev.sargunv.mobilitydata.gtfs.realtime

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
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

  @Test
  fun invalidProtobufBodyReturnsFailure() = runTest {
    val engine = MockEngine {
      respond(
        content = "not protobuf".toByteArray(),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType to listOf("application/x-protobuf")),
      )
    }
    val client = GtfsRealtimeClient(engine)

    val result = client.getFeedMessage("https://example.com/trip_updates.pb")

    assertTrue(result.isFailure, "Expected failure for invalid protobuf body")
  }
}
