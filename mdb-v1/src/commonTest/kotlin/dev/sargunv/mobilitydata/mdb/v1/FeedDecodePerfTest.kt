package dev.sargunv.mobilitydata.mdb.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeedDecodePerfTest {
  @Test
  fun decode3500FeedsMedianUnder200ms() {
    val items =
      (0 until 3500).joinToString(",") { i ->
        """{"id":"mdb-$i","data_type":"gtfs","provider":"Provider $i","status":"active"}"""
      }
    val json = "[$items]"
    val times = LongArray(20)
    repeat(20) { i ->
      val start = kotlin.time.TimeSource.Monotonic.markNow()
      val decoded = MdbJson.decodeFromString<List<Feed>>(json)
      times[i] = start.elapsedNow().inWholeMilliseconds
      assertEquals(3500, decoded.size)
    }
    val median = times.sorted()[10]
    println("mdb-2 decode 3500 feeds median_ms=$median")
    assertTrue(median < 200, "median ${median}ms must stay under 200ms")
  }
}
