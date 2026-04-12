package dev.sargunv.mobilitydata.gtfs.realtime

import java.nio.file.Files
import java.nio.file.Path

internal fun readFixtureBytes(vararg relativePath: String): ByteArray =
  Files.readAllBytes(resolveFixturePath(*relativePath))

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
