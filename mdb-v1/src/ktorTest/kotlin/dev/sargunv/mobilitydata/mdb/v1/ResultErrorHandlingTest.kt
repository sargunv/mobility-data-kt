package dev.sargunv.mobilitydata.mdb.v1

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class ResultErrorHandlingTest {
  @Test
  fun serverErrorReturnsFailure() = runTest {
    val client =
      MdbV1Client(
        MockEngine { respondError(HttpStatusCode.InternalServerError) },
        CatalogAuth.Access("access-1"),
      )
    val result = client.getFeeds()
    assertTrue(result.isFailure, "Expected failure for 500")
    client.close()
  }

  @Test
  fun unauthorizedAccessTokenReturnsFailure() = runTest {
    val client =
      MdbV1Client(
        MockEngine { respond("invalid token", HttpStatusCode.Unauthorized) },
        CatalogAuth.Access("bad"),
      )
    val result = client.getFeeds()
    assertTrue(result.isFailure, "Expected failure for 401")
    client.close()
  }

  @Test
  fun rateLimitReturnsFailure() = runTest {
    val client =
      MdbV1Client(
        MockEngine { respondError(HttpStatusCode.TooManyRequests) },
        CatalogAuth.Access("access-1"),
      )
    val result = client.getFeeds()
    assertTrue(result.isFailure, "Expected failure for 429")
    client.close()
  }

  @Test
  fun iapRedirectReturnsFailure() = runTest {
    val client =
      MdbV1Client(
        MockEngine { respond("<html>login</html>", HttpStatusCode.Found) },
        CatalogAuth.Access("access-1"),
      )
    val result = client.getFeeds()
    assertTrue(result.isFailure, "Expected failure for IAP 302")
    client.close()
  }
}
