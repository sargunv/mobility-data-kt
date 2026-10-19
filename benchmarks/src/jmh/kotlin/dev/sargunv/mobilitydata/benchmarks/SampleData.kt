package dev.sargunv.mobilitydata.benchmarks

import java.io.File

/** Resolves a path under `sample-data`, searching up from the working directory. */
internal fun sampleData(vararg path: String): File {
  val root =
    generateSequence(File("").absoluteFile) { it.parentFile }
      .map { it.resolve("sample-data") }
      .firstOrNull { it.isDirectory } ?: error("sample-data not found; run `mise deps`")
  return path.fold(root) { file, segment -> file.resolve(segment) }
}
