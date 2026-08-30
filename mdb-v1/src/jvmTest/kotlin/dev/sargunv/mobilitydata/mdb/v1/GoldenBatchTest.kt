package dev.sargunv.mobilitydata.mdb.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.builtins.ListSerializer

class GoldenBatchTest {
  @Test
  fun decodeEveryLiveGolden() {
    val manifestStream = javaClass.getResourceAsStream("/goldens/manifest.json")
    if (manifestStream == null) {
      println("GoldenBatchTest skipped: no /goldens/manifest.json")
      return
    }
    val manifestText = manifestStream.bufferedReader().readText()
    val rows = MdbJson.decodeFromString<List<GoldenManifestRow>>(manifestText)
    assertTrue(rows.isNotEmpty(), "manifest is empty")

    for (row in rows) {
      assertEquals(200, row.status, "${row.file} status")
      val body = javaClass.getResourceAsStream("/goldens/${row.file}")?.bufferedReader()?.readText()
      assertNotNull(body, "missing ${row.file}")
      decodeGolden(row.decodeAs, body)
    }
  }
}

@kotlinx.serialization.Serializable
private data class GoldenManifestRow(
  val file: String,
  @kotlinx.serialization.SerialName("decode_as") val decodeAs: String,
  val status: Int,
  val bytes: Int? = null,
)

private fun decodeGolden(decodeAs: String, body: String) {
  when (decodeAs) {
    "AccessToken" -> MdbJson.decodeFromString<AccessToken>(body)
    "List<Feed>" -> MdbJson.decodeFromString(ListSerializer(Feed.serializer()), body)
    "List<Feed.Gtfs>" -> MdbJson.decodeFromString(ListSerializer(Feed.Gtfs.serializer()), body)
    "List<Feed.GtfsRt>" -> MdbJson.decodeFromString(ListSerializer(Feed.GtfsRt.serializer()), body)
    "List<Feed.Gbfs>" -> MdbJson.decodeFromString(ListSerializer(Feed.Gbfs.serializer()), body)
    "Feed" -> MdbJson.decodeFromString<Feed>(body)
    "Feed.Gtfs" -> MdbJson.decodeFromString<Feed.Gtfs>(body)
    "Feed.GtfsRt" -> MdbJson.decodeFromString<Feed.GtfsRt>(body)
    "Feed.Gbfs" -> MdbJson.decodeFromString<Feed.Gbfs>(body)
    "List<GtfsDataset>" -> MdbJson.decodeFromString(ListSerializer(GtfsDataset.serializer()), body)
    "GtfsDataset" -> MdbJson.decodeFromString<GtfsDataset>(body)
    "Metadata" -> MdbJson.decodeFromString<Metadata>(body)
    "SearchFeedsResponse" -> MdbJson.decodeFromString<SearchFeedsResponse>(body)
    "LocationSearchResponse" -> MdbJson.decodeFromString<LocationSearchResponse>(body)
    "List<License>" -> MdbJson.decodeFromString(ListSerializer(License.serializer()), body)
    "LicenseWithRules" -> MdbJson.decodeFromString<LicenseWithRules>(body)
    "List<MatchingLicense>" ->
      MdbJson.decodeFromString(ListSerializer(MatchingLicense.serializer()), body)
    "GtfsFeedAvailabilityResponse" -> MdbJson.decodeFromString<GtfsFeedAvailabilityResponse>(body)
    "FeedReliabilityReport" -> MdbJson.decodeFromString<FeedReliabilityReport>(body)
    else -> error("unknown decode_as $decodeAs")
  }
}
