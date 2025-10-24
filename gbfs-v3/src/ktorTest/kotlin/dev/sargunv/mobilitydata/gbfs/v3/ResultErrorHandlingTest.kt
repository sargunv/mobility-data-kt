package dev.sargunv.mobilitydata.gbfs.v3

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class ResultErrorHandlingTest {
  @Test
  fun testNetworkErrorReturnsFailure() = runTest {
    val engine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
    val client = GbfsV3Client(engine)

    val result = client.getServiceManifest("https://example.com/gbfs.json")

    assertTrue(result.isFailure, "Expected failure for network error")
  }

  @Test
  fun testNotFoundReturnsFailure() = runTest {
    val engine = MockEngine { respondError(HttpStatusCode.NotFound) }
    val client = GbfsV3Client(engine)

    val result = client.getServiceManifest("https://example.com/gbfs.json")

    assertTrue(result.isFailure, "Expected failure for 404 error")
  }
}
