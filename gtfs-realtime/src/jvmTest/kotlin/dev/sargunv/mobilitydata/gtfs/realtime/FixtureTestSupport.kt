package dev.sargunv.mobilitydata.gtfs.realtime

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.walk

internal fun readFixtureBytes(vararg relativePath: String): ByteArray =
  Files.readAllBytes(resolveFixturePath(*relativePath))

/** Every `.pb` fixture under `sample-data/gtfs-realtime`, as `sample-data`-relative paths. */
internal fun realtimeFixtures(): List<String> {
  val root = resolveFixturePath()
  return root
    .resolve("gtfs-realtime")
    .walk()
    .filter { it.isRegularFile() && it.extension == "pb" }
    .map { root.relativize(it).joinToString("/") }
    .sorted()
    .toList()
}

private fun resolveFixturePath(vararg relativePath: String): Path {
  val cwd = Path.of("").toAbsolutePath()
  val projectDir =
    when (cwd.fileName.toString()) {
      "mobility-data-gtfs-realtime-test" -> cwd.parent.parent.parent.parent
      "gtfs-realtime" -> cwd.parent
      else -> cwd
    }
  return relativePath.fold(projectDir.resolve("sample-data")) { path, segment ->
    path.resolve(segment)
  }
}
