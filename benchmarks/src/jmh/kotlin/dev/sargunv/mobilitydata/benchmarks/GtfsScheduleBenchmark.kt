package dev.sargunv.mobilitydata.benchmarks

import dev.sargunv.mobilitydata.gtfs.schedule.GtfsCsv
import dev.sargunv.mobilitydata.gtfs.schedule.Route
import dev.sargunv.mobilitydata.gtfs.schedule.ServiceCalendarOverride
import dev.sargunv.mobilitydata.gtfs.schedule.Shape
import dev.sargunv.mobilitydata.gtfs.schedule.Stop
import dev.sargunv.mobilitydata.gtfs.schedule.StopTime
import dev.sargunv.mobilitydata.gtfs.schedule.Trip
import java.util.concurrent.TimeUnit
import kotlinx.io.Buffer
import kotlinx.serialization.DeserializationStrategy
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
import org.openjdk.jmh.infra.Blackhole

/** Decodes the first [ROWS] rows of Puget Sound GTFS tables from `sample-data/gtfs-schedule`. */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class GtfsScheduleBenchmark {
  @Param("stop_times", "trips", "shapes", "stops", "routes", "calendar_dates")
  lateinit var table: String

  private lateinit var bytes: ByteArray
  private lateinit var deserializer: DeserializationStrategy<*>

  @Setup
  fun setup() {
    val lines =
      sampleData("gtfs-schedule", "puget-sound", "$table.txt").useLines { lines ->
        lines.take(ROWS + 1).joinToString("\n", postfix = "\n")
      }
    bytes = lines.encodeToByteArray()
    deserializer =
      when (table) {
        "stop_times" -> StopTime.serializer()
        "trips" -> Trip.serializer()
        "shapes" -> Shape.serializer()
        "stops" -> Stop.serializer()
        "routes" -> Route.serializer()
        "calendar_dates" -> ServiceCalendarOverride.serializer()
        else -> error("Unknown table $table")
      }
  }

  @Benchmark
  fun decode(blackhole: Blackhole) {
    val source = Buffer().apply { write(bytes) }
    GtfsCsv.decodeFromSource(source, deserializer).forEach(blackhole::consume)
  }

  private companion object {
    const val ROWS = 50_000
  }
}
