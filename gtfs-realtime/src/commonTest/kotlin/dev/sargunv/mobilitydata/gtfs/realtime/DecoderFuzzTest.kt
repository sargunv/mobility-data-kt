package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Differential tests of [GtfsRealtimeDecoder] against [oracleDecode] on generated input. */
class DecoderFuzzTest {
  @Test
  fun randomFeedsDecodeLikeTheOracle() {
    val random = Random(151)
    var decoded = 0
    repeat(300) { i ->
      val result = assertMatchesOracle(RandomFeedWriter(random).feedMessage(), label = "feed $i")
      val feed = result.getOrNull() ?: return@repeat
      decoded++
      // Encoded by kotlinx, every field the feed has comes back in canonical form.
      val reencoded = GtfsRealtimeProto.encodeFeedMessage(feed)
      assertEquals(feed, decodeMatchingOracle(reencoded, label = "re-encoded feed $i"))
    }
    assertTrue(decoded in 100..290, "Expected a mix of valid and invalid feeds, $decoded decoded")
  }

  @Test
  fun mutatedFeedsDecodeLikeTheOracle() {
    val random = Random(151)
    val seeds = List(50) { RandomFeedWriter(random).feedMessage() }
    var decoded = 0
    repeat(3000) { i ->
      var bytes = seeds.random(random)
      repeat(1 + random.nextInt(3)) { bytes = mutate(bytes, random, donor = seeds.random(random)) }
      if (assertMatchesOracle(bytes, label = "mutation $i").isSuccess) decoded++
    }
    assertTrue(decoded in 100..2900, "Expected a mix of valid and invalid feeds, $decoded decoded")
  }
}
