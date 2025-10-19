package dev.sargunv.mobilitydata.gofs.v1.serialization

import dev.sargunv.mobilitydata.gofs.v1.FeedType
import dev.sargunv.mobilitydata.gofs.v1.GofsJson
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

    val json = GofsJson.encodeToJsonElement(TestData.serializer(), testData)
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
        FeedType.GofsManifest to "https://example.com/gofs.json",
        FeedType.SystemInformation to "https://example.com/system_information.json",
        FeedType.VehicleTypes to "https://example.com/vehicle_types.json",
      )
    val testData = TestData(feeds)

    val json = GofsJson.encodeToJsonElement(TestData.serializer(), testData)
    val feedsArray = json.jsonObject["feeds"]!!.jsonArray

    assertEquals(3, feedsArray.size)

    val feedNames = feedsArray.map { it.jsonObject["name"]?.jsonPrimitive?.content }.toSet()
    assertTrue(feedNames.contains("gofs"))
    assertTrue(feedNames.contains("system_information"))
    assertTrue(feedNames.contains("vehicle_types"))
  }

  @Test
  fun testDeserializeSingleFeed() {
    val jsonString =
      """{"feeds":[{"name":"system_information","url":"https://example.com/system_information.json"}]}"""

    val result = GofsJson.decodeFromString(TestData.serializer(), jsonString)

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
        {"name":"gofs","url":"https://example.com/gofs.json"},
        {"name":"vehicle_types","url":"https://example.com/vehicle_types.json"},
        {"name":"zones","url":"https://example.com/zones.json"}
      ]}"""

    val result = GofsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(3, result.feeds.size)
    assertEquals("https://example.com/gofs.json", result.feeds[FeedType.GofsManifest])
    assertEquals("https://example.com/vehicle_types.json", result.feeds[FeedType.VehicleTypes])
    assertEquals("https://example.com/zones.json", result.feeds[FeedType.Zones])
  }

  @Test
  fun testRoundTrip() {
    val original =
      TestData(
        mapOf(
          FeedType.GofsManifest to "https://example.com/gofs.json",
          FeedType.GofsVersions to "https://example.com/gofs_versions.json",
          FeedType.SystemInformation to "https://example.com/system_information.json",
          FeedType.VehicleTypes to "https://example.com/vehicle_types.json",
        )
      )

    val jsonString = GofsJson.encodeToString(TestData.serializer(), original)
    val decoded = GofsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original.feeds.size, decoded.feeds.size)
    for ((feedType, url) in original.feeds) {
      assertEquals(url, decoded.feeds[feedType], "URL for $feedType should match")
    }
  }

  @Test
  fun testEmptyMap() {
    val testData = TestData(emptyMap())

    val jsonString = GofsJson.encodeToString(TestData.serializer(), testData)
    val decoded = GofsJson.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
    assertTrue(decoded.feeds.isEmpty())
  }
}
