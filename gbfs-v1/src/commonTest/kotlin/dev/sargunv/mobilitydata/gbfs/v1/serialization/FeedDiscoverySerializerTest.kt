package dev.sargunv.mobilitydata.gbfs.v1.serialization

import dev.sargunv.mobilitydata.gbfs.v1.FeedType
import dev.sargunv.mobilitydata.gbfs.v1.GbfsJson
import dev.sargunv.mobilitydata.utils.Url
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class FeedDiscoverySerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = FeedDiscoverySerializer::class) val feeds: Map<FeedType, Url>
  )

  @Test
  fun testSerializeSingleFeed() {
    val feeds = mapOf(FeedType.SystemInformation to "https://example.com/system_information.json")
    val testData = TestData(feeds)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val feedsArray = json.jsonObject["feeds"]!!.jsonArray

    assertEquals(1, feedsArray.size)
    val feed = feedsArray[0].jsonObject
    assertEquals("system_information", feed["name"]?.jsonPrimitive?.content)
    assertEquals("https://example.com/system_information.json", feed["url"]?.jsonPrimitive?.content)
  }

  @Test
  fun testSerializeMultipleFeeds() {
    val feeds =
      mapOf(
        FeedType.GbfsManifest to "https://example.com/gbfs.json",
        FeedType.SystemInformation to "https://example.com/system_information.json",
        FeedType.StationInformation to "https://example.com/station_information.json",
      )
    val testData = TestData(feeds)

    val json = GbfsJson.encodeToJsonElement(TestData.serializer(), testData)
    val feedsArray = json.jsonObject["feeds"]!!.jsonArray

    assertEquals(3, feedsArray.size)

    val feedNames = feedsArray.map { it.jsonObject["name"]?.jsonPrimitive?.content }.toSet()
    assertTrue(feedNames.contains("gbfs"))
    assertTrue(feedNames.contains("system_information"))
    assertTrue(feedNames.contains("station_information"))
  }

  @Test
  fun testDeserializeSingleFeed() {
    val jsonString =
      """{"feeds":[{"name":"system_information","url":"https://example.com/system_information.json"}]}"""

    val result = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(1, result.feeds.size)
    assertEquals(
      "https://example.com/system_information.json",
      result.feeds[FeedType.SystemInformation],
    )
  }

  @Test
  fun testDeserializeMultipleFeeds() {
    val jsonString =
      """{"feeds":[
        {"name":"gbfs","url":"https://example.com/gbfs.json"},
        {"name":"station_information","url":"https://example.com/station_information.json"},
        {"name":"station_status","url":"https://example.com/station_status.json"}
      ]}"""

    val result = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(3, result.feeds.size)
    assertEquals("https://example.com/gbfs.json", result.feeds[FeedType.GbfsManifest])
    assertEquals(
      "https://example.com/station_information.json",
      result.feeds[FeedType.StationInformation],
    )
    assertEquals("https://example.com/station_status.json", result.feeds[FeedType.StationStatus])
  }

  @Test
  fun testRoundTrip() {
    val original =
      TestData(
        mapOf(
          FeedType.GbfsManifest to "https://example.com/gbfs.json",
          FeedType.GbfsVersions to "https://example.com/gbfs_versions.json",
          FeedType.SystemInformation to "https://example.com/system_information.json",
        )
      )

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), original)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original.feeds.size, decoded.feeds.size)
    for ((feedType, url) in original.feeds) {
      assertEquals(url, decoded.feeds[feedType], "URL for $feedType should match")
    }
  }

  @Test
  fun testEmptyMap() {
    val testData = TestData(emptyMap())

    val jsonString = GbfsJson.encodeToString(TestData.serializer(), testData)
    val decoded = GbfsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
    assertTrue(decoded.feeds.isEmpty())
  }
}
