package dev.sargunv.mobilitydata.gtfs.realtime

import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.suspendRunCatching
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.get

/**
 * HTTP client for fetching GTFS Realtime feeds.
 *
 * All constructors set `expectSuccess = true`; non-2xx responses surface as errors in the returned
 * [Result].
 */
public class GtfsRealtimeClient internal constructor(private val httpClient: HttpClient) :
  AutoCloseable {

  public constructor(
    block: HttpClientConfig<*>.() -> Unit = {}
  ) : this(
    HttpClient {
      block()
      expectSuccess = true
    }
  )

  public constructor(
    engine: HttpClientEngine,
    block: HttpClientConfig<*>.() -> Unit = {},
  ) : this(
    HttpClient(engine) {
      block()
      expectSuccess = true
    }
  )

  /**
   * Fetches and decodes a GTFS Realtime protobuf feed from the given URL.
   *
   * @param feedUrl Fully qualified URL of the GTFS Realtime protobuf feed
   * @return a [Result] containing the decoded [FeedMessage] on success, or the exception on failure
   */
  public suspend fun getFeedMessage(feedUrl: Url): Result<FeedMessage> = suspendRunCatching {
    val bytes = httpClient.get(feedUrl).body<ByteArray>()
    GtfsRealtimeProto.decodeFeedMessage(bytes)
  }

  override fun close(): Unit = httpClient.close()
}
