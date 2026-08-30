package dev.sargunv.mobilitydata.mdb.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val gtfsJson = // language=JSON
  """
  {
    "id": "mdb-1210",
    "data_type": "gtfs",
    "created_at": "2023-07-10T22:06:00Z",
    "provider": "Los Angeles Department of Transportation",
    "status": "active",
    "official": true,
    "feed_name": "Bus"
  }
  """
    .trimIndent()

private val gtfsRtJson = // language=JSON
  """
  {
    "id": "mdb-20",
    "data_type": "gtfs_rt",
    "provider": "Example Transit",
    "status": "active",
    "entity_types": ["vp", "tu"],
    "feed_references": ["mdb-10"]
  }
  """
    .trimIndent()

private val gbfsJson = // language=JSON
  """
  {
    "id": "mdb-300",
    "data_type": "gbfs",
    "provider": "City Bike",
    "system_id": "system-1234",
    "provider_url": "https://www.citybikenyc.com/"
  }
  """
    .trimIndent()

private val expectedGtfs =
  Feed.Gtfs(
    id = FeedId("mdb-1210"),
    createdAt = Instant.parse("2023-07-10T22:06:00Z"),
    provider = "Los Angeles Department of Transportation",
    status = FeedStatus.Active,
    official = true,
    feedName = "Bus",
  )

private val expectedGtfsRt =
  Feed.GtfsRt(
    id = FeedId("mdb-20"),
    provider = "Example Transit",
    status = FeedStatus.Active,
    entityTypes = listOf(RealtimeEntityType.VehiclePositions, RealtimeEntityType.TripUpdates),
    feedReferences = listOf(FeedId("mdb-10")),
  )

private val expectedGbfs =
  Feed.Gbfs(
    id = FeedId("mdb-300"),
    provider = "City Bike",
    systemId = "system-1234",
    providerUrl = "https://www.citybikenyc.com/",
  )

class FeedSerializationTest {
  @Test
  fun encodeGtfs() {
    val expected = Json.decodeFromString<JsonElement>(gtfsJson)
    val encoded = MdbJson.encodeToJsonElement<Feed>(expectedGtfs)
    assertEquals(expected, encoded)
  }

  @Test
  fun decodeGtfs() {
    val decoded = MdbJson.decodeFromString<Feed>(gtfsJson)
    assertEquals(expectedGtfs, decoded)
    assertIs<Feed.Gtfs>(decoded)
  }

  @Test
  fun encodeGtfsRt() {
    val expected = Json.decodeFromString<JsonElement>(gtfsRtJson)
    val encoded = MdbJson.encodeToJsonElement<Feed>(expectedGtfsRt)
    assertEquals(expected, encoded)
  }

  @Test
  fun decodeGtfsRt() {
    val decoded = MdbJson.decodeFromString<Feed>(gtfsRtJson)
    assertEquals(expectedGtfsRt, decoded)
    assertIs<Feed.GtfsRt>(decoded)
  }

  @Test
  fun encodeGbfs() {
    val expected = Json.decodeFromString<JsonElement>(gbfsJson)
    val encoded = MdbJson.encodeToJsonElement<Feed>(expectedGbfs)
    assertEquals(expected, encoded)
  }

  @Test
  fun decodeGbfs() {
    val decoded = MdbJson.decodeFromString<Feed>(gbfsJson)
    assertEquals(expectedGbfs, decoded)
    assertIs<Feed.Gbfs>(decoded)
  }

  @Test
  fun decodeOffsetLessCreatedAtAsUtc() {
    val json =
      """
      {
        "id": "mdb-1",
        "data_type": "gtfs",
        "created_at": "2025-01-22T20:19:42.509622"
      }
      """
        .trimIndent()
    val decoded = MdbJson.decodeFromString<Feed>(json)
    assertEquals(Instant.parse("2025-01-22T20:19:42.509622Z"), (decoded as Feed.Gtfs).createdAt)
  }

  @Test
  fun decodeIgnoresUnknownKeys() {
    val json =
      """
      {
        "id": "mdb-1210",
        "data_type": "gtfs",
        "provider": "LADOT",
        "unexpected_key": true
      }
      """
        .trimIndent()
    val decoded = MdbJson.decodeFromString<Feed>(json)
    assertEquals(Feed.Gtfs(id = FeedId("mdb-1210"), provider = "LADOT", seasonal = false), decoded)
  }

  @Test
  fun decodeOmittedSeasonalIsFalse() {
    val json =
      """
      {
        "id": "mdb-1",
        "data_type": "gtfs",
        "status": "active"
      }
      """
        .trimIndent()
    val decoded = MdbJson.decodeFromString<Feed>(json)
    val gtfs = decoded as Feed.Gtfs
    assertEquals(false, gtfs.seasonal)
  }

  @Test
  fun decodeGbfsSharedFields() {
    val json =
      """
      {
        "id": "gbfs-zem_ch",
        "data_type": "gbfs",
        "status": "active",
        "official": false,
        "seasonal": false,
        "related_links": [],
        "system_id": "zem"
      }
      """
        .trimIndent()
    val decoded = MdbJson.decodeFromString<Feed>(json)
    val gbfs = decoded as Feed.Gbfs
    assertEquals(FeedId("gbfs-zem_ch"), gbfs.id)
    assertEquals(FeedStatus.Active, gbfs.status)
    assertEquals(false, gbfs.official)
    assertEquals(false, gbfs.seasonal)
    assertEquals(emptyList(), gbfs.relatedLinks)
    assertEquals("zem", gbfs.systemId)
  }

  @Test
  fun decodeUnknownDataType() {
    val json =
      """
      {
        "id": "mdb-1",
        "data_type": "neptunian",
        "status": "active"
      }
      """
        .trimIndent()
    val decoded = MdbJson.decodeFromString<Feed>(json)
    val unknown = decoded as Feed.Unknown
    assertEquals("neptunian", unknown.dataType)
    assertEquals(FeedId("mdb-1"), unknown.id)
    assertEquals(FeedStatus.Active, unknown.status)
  }

  @Test
  fun encodeUnknownPreservesDataType() {
    val expected =
      Json.decodeFromString<JsonElement>(
        """
        {
          "data_type": "neptunian",
          "id": "mdb-1"
        }
        """
          .trimIndent()
      )
    val encoded =
      MdbJson.encodeToJsonElement<Feed>(Feed.Unknown(dataType = "neptunian", id = FeedId("mdb-1")))
    assertEquals(expected, encoded)
  }
}
