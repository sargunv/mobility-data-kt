package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

/** Differential tests of [GtfsRealtimeDecoder] against [oracleDecode] on damaged real feeds. */
class FixtureFuzzTest {
  @Test
  fun mutatedFixturesDecodeLikeTheOracle() {
    val random = Random(151)
    val fixtures =
      realtimeFixtures().associateWith { readFixtureBytes(*it.split("/").toTypedArray()) }
    val donors = fixtures.values.toList()
    var decoded = 0
    var total = 0
    for ((fixture, bytes) in fixtures) {
      // Full-size feeds take milliseconds per decode, so they get fewer mutations.
      repeat(if (bytes.size > 20_000) 15 else 300) { i ->
        var mutated = bytes
        repeat(1 + random.nextInt(3)) {
          mutated = mutate(mutated, random, donor = donors.random(random))
        }
        if (assertMatchesOracle(mutated, label = "$fixture mutation $i").isSuccess) decoded++
        total++
      }
    }
    assertTrue(
      decoded in 1 until total,
      "Expected a mix of valid and invalid feeds, $decoded decoded",
    )
  }
}
