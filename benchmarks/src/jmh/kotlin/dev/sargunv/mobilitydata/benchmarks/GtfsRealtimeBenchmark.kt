package dev.sargunv.mobilitydata.benchmarks

import com.google.transit.realtime.GtfsRealtime
import dev.sargunv.mobilitydata.gtfs.realtime.FeedMessage
import dev.sargunv.mobilitydata.gtfs.realtime.GtfsRealtimeProto
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup

/** Decodes full GTFS Realtime feeds from `sample-data/gtfs-realtime`. */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class GtfsRealtimeBenchmark {
  @Param(
    "mbta/trip_updates.pb",
    "mbta/vehicle_positions.pb",
    "mbta/alerts.pb",
    "king-county-metro/trip_updates.pb",
    "king-county-metro/vehicle_positions.pb",
    "nyct/ace.pb",
  )
  lateinit var feed: String

  private lateinit var bytes: ByteArray

  @Setup
  fun setup() {
    bytes = sampleData("gtfs-realtime", feed).readBytes()
  }

  @Benchmark fun decode(): FeedMessage = GtfsRealtimeProto.decodeFeedMessage(bytes)

  /** The official protobuf-java bindings, as a reference point. */
  @Benchmark
  fun decodeWithBindings(): GtfsRealtime.FeedMessage = GtfsRealtime.FeedMessage.parseFrom(bytes)
}
