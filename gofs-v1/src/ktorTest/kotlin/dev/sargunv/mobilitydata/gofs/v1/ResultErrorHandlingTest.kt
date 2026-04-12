package dev.sargunv.mobilitydata.gofs.v1

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class ResultErrorHandlingTest {
  @Test
  fun networkErrorReturnsFailure() = runTest {
    val engine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
    val client = GofsV1Client(engine)

    val result = client.getSystemManifest("https://example.com/gofs.json")

    assertTrue(result.isFailure, "Expected failure for network error")
  }

  @Test
  fun waitTimesStillThrowOnInvalidArguments() = runTest {
    val client = GofsV1Client(MockEngine { error("unexpected network call") })
    val service = Service()

    context(service) {
      assertFailsWith<IllegalArgumentException> {
        client.getWaitTimes(pickupLat = 41.8781, pickupLon = -87.6298, dropOffLat = 41.9)
      }
    }
  }

  @Test
  fun realtimeBookingsStillThrowOnInvalidArguments() = runTest {
    val client = GofsV1Client(MockEngine { error("unexpected network call") })
    val service = Service()

    context(service) {
      assertFailsWith<IllegalArgumentException> {
        client.getRealtimeBookings(pickupLat = 41.8781, pickupLon = -87.6298, dropOffLat = 41.9)
      }
    }
  }
}
