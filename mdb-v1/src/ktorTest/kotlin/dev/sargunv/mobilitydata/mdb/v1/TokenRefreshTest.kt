package dev.sargunv.mobilitydata.mdb.v1

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest

class TokenRefreshTest {
  @Test
  fun authToStringOmitsTokens() {
    assertEquals("CatalogAuth.Refresh", CatalogAuth.Refresh("secret-refresh").toString())
    assertEquals("CatalogAuth.Access", CatalogAuth.Access("secret-access").toString())
  }

  @Test
  fun refreshPostsBeforeListingFeeds() = runTest {
    val paths = mutableListOf<String>()
    val methods = mutableListOf<HttpMethod>()
    val engine = MockEngine { request ->
      paths += request.url.fullPath
      methods += request.method
      when {
        request.url.fullPath.endsWith("/v1/tokens/access") ->
          respond(
            """{"access_token":"access-1","token_type":"Bearer"}""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
        else ->
          respond(
            """[{"id":"mdb-1210","data_type":"gtfs","provider":"LADOT"}]""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
      }
    }

    val client = MdbV1Client(engine, CatalogAuth.Refresh("refresh-1"))
    val feeds = client.getFeeds().getOrThrow()

    println("${methods[0]} ${paths[0]}")
    println("${methods[1]} ${paths[1]}")
    assertEquals(listOf("/v1/tokens/access", "/v1/feeds"), paths)
    assertEquals(listOf(HttpMethod.Post, HttpMethod.Get), methods)
    assertEquals(FeedId("mdb-1210"), feeds.single().id)
    client.close()
  }

  @Test
  fun accessTokenSkipsRefreshPost() = runTest {
    val paths = mutableListOf<String>()
    val engine = MockEngine { request ->
      paths += request.url.fullPath
      assertEquals("Bearer access-1", request.headers[HttpHeaders.Authorization])
      respond("""[]""", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
    }

    val client = MdbV1Client(engine, CatalogAuth.Access("access-1"))
    client.getFeeds().getOrThrow()

    assertEquals(listOf("/v1/feeds"), paths)
    assertTrue(paths.none { it.contains("/v1/tokens/access") })
    client.close()
  }

  @Test
  fun refreshesOnceOnUnauthorized() = runTest {
    var feedCalls = 0
    val paths = mutableListOf<String>()
    val engine = MockEngine { request ->
      paths += request.url.fullPath
      when {
        request.url.fullPath.endsWith("/v1/tokens/access") ->
          respond(
            """{"access_token":"access-2","token_type":"Bearer"}""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
        else -> {
          feedCalls += 1
          if (feedCalls == 1) {
            respond("invalid token", HttpStatusCode.Unauthorized)
          } else {
            respond(
              """[]""",
              HttpStatusCode.OK,
              headersOf(HttpHeaders.ContentType, "application/json"),
            )
          }
        }
      }
    }

    val client = MdbV1Client(engine, CatalogAuth.Refresh("refresh-1"))
    client.getFeeds().getOrThrow()
    assertEquals(listOf("/v1/tokens/access", "/v1/feeds", "/v1/tokens/access", "/v1/feeds"), paths)
    client.close()
  }

  @Test
  fun concurrentFirstCallsShareOneTokenRefresh() = runTest {
    val engine = MockEngine { request ->
      when {
        request.url.fullPath.endsWith("/v1/tokens/access") -> {
          delay(50)
          respond(
            """{"access_token":"access-1","token_type":"Bearer"}""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
        }
        else ->
          respond(
            """[]""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
      }
    }

    val client = MdbV1Client(engine, CatalogAuth.Refresh("refresh-1"))
    coroutineScope {
      launch { client.getFeeds().getOrThrow() }
      launch { client.getFeeds().getOrThrow() }
    }
    val tokenPosts = engine.requestHistory.count { it.url.fullPath.endsWith("/v1/tokens/access") }
    assertEquals(1, tokenPosts)
    client.close()
  }

  @Test
  fun concurrentUnauthorizedRefreshOnce() = runTest {
    val issued = mutableListOf<String>()
    var feedGets = 0
    val engine = MockEngine { request ->
      when {
        request.url.fullPath.endsWith("/v1/tokens/access") -> {
          delay(50)
          val token = if (issued.isEmpty()) "stale" else "fresh"
          issued += token
          respond(
            """{"access_token":"$token","token_type":"Bearer"}""",
            HttpStatusCode.OK,
            headersOf(HttpHeaders.ContentType, "application/json"),
          )
        }
        else -> {
          feedGets += 1
          val bearer = request.headers[HttpHeaders.Authorization]
          if (feedGets > 1 && bearer == "Bearer stale") {
            respond("invalid token", HttpStatusCode.Unauthorized)
          } else {
            respond(
              """[]""",
              HttpStatusCode.OK,
              headersOf(HttpHeaders.ContentType, "application/json"),
            )
          }
        }
      }
    }

    val client = MdbV1Client(engine, CatalogAuth.Refresh("refresh-1"))
    client.getFeeds().getOrThrow()
    coroutineScope {
      launch { client.getFeeds().getOrThrow() }
      launch { client.getFeeds().getOrThrow() }
    }
    val tokenPosts = engine.requestHistory.count { it.url.fullPath.endsWith("/v1/tokens/access") }
    assertEquals(2, tokenPosts)
    client.close()
  }
}
