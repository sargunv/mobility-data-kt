package dev.sargunv.mobilitydata.gbfs.v1

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlin.test.Ignore
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import kotlinx.io.buffered
import kotlinx.io.files.FileNotFoundException
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString

class ProducerTest {
  private fun createMockEngine(
    removePrefix: String,
    resourcesSubdirectory: String,
    extension: String = "",
  ): MockEngine {
    val cwd = SystemFileSystem.resolve(Path("."))
    val projectDir =
      when (cwd.name) {
        "mobility-data-gbfs-v1-test" -> cwd.parent!!.parent!!.parent!!.parent!!
        "gbfs-v1" -> cwd.parent!!
        else -> error("unexpected: $cwd")
      }
    return MockEngine { request ->
      val urlPath = request.url.fullPath.removePrefix(removePrefix)
      val localPath =
        Path("$projectDir", "sample-data", "gbfs-v1", resourcesSubdirectory, urlPath + extension)
      try {
        val source = SystemFileSystem.source(localPath)
        val content = source.buffered().readString()
        respond(content, HttpStatusCode.OK, headersOf("Content-Type" to listOf("application/json")))
      } catch (_: FileNotFoundException) {
        respondError(HttpStatusCode.NotFound)
      }
    }
  }

  @Test
  @Ignore // https://github.com/sargunv/mobility-data-kt/issues/16
  fun publicbikesystem() = runTest {
    val client =
      GbfsV1Client(
        createMockEngine(
          removePrefix = "/customer/ube/gbfs/v1/en/",
          resourcesSubdirectory = "publicbikesystem",
          extension = ".json",
        )
      )

    val manifest = client.getSystemManifest("gbfs")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getSystemPricingPlans()
      client.getStationInformation()
      client.getStationStatus()
      client.getSystemRegions()
    }
  }

  @Test
  fun bird() = runTest {
    val client = GbfsV1Client(createMockEngine("/gbfs/seattle-washington/", "bird"))
    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getVersionManifest()
      client.getSystemRegions()
      client.getFreeBikeStatus()
    }
  }

  @Test
  fun bcycle() = runTest {
    // TODO
  }

  @Test
  fun donkey() = runTest {
    // TODO
  }

  @Test
  fun bolt() = runTest {
    // TODO
  }

  @Test
  fun lime() = runTest {
    // TODO Watch out; the station status feed is non conformant. Skip it.
  }
}
