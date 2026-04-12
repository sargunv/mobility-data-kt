package dev.sargunv.mobilitydata.gtfs.realtime

import dev.sargunv.mobilitydata.utils.Url
import dev.sargunv.mobilitydata.utils.suspendRunCatching
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.get

/** HTTP client for fetching GTFS Realtime feeds. */
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
   * @return Result wrapping the decoded feed message, or an error
   */
  public suspend fun getFeedMessage(feedUrl: Url): Result<FeedMessage> = suspendRunCatching {
    val bytes = httpClient.get(feedUrl).body<ByteArray>()
    GtfsRealtimeProto.decodeFeedMessage(bytes)
  }

  override fun close(): Unit = httpClient.close()
}
