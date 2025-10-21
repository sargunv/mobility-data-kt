package dev.sargunv.mobilitydata.gbfs.v3

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import kotlinx.io.buffered
import kotlinx.io.files.FileNotFoundException
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString

class ProducerTest {
  private fun createMockEngine(resourcesSubdirectory: String): MockEngine {
    val cwd = SystemFileSystem.resolve(Path("."))
    val projectDir =
      when (cwd.name) {
        "mobility-data-gbfs-v3-test" -> cwd.parent!!.parent!!.parent!!.parent!!
        "gbfs-v3" -> cwd.parent!!
        else -> error("unexpected: $cwd")
      }
    return MockEngine { request ->
      val fileName = Path(request.url.fullPath).name
      val suffix = if (fileName.endsWith(".json")) "" else ".json"
      val localPath =
        Path("$projectDir", "sample-data", "gbfs-v3", resourcesSubdirectory, fileName + suffix)
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
  fun bolt() = runTest {
    val client = GbfsV3Client(createMockEngine("bolt"))
    val service = client.getServiceManifest("gbfs.json").data

    context(service) {
      client.getVersionManifest()
      client.getGeofencingZones()
      client.getStationInformation()
      client.getStationStatus()
      client.getSystemInformation()
      client.getSystemPricingPlans()
      client.getVehicleStatus()
      client.getVehicleTypes()
    }
  }

  @Test
  fun citiz() {
    // TODO
  }

  @Test
  fun cooltra() {
    // TODO
  }

  @Test
  fun cyclocity() {
    // TODO
  }

  @Test
  fun ecovelo() {
    // TODO
  }

  @Test
  fun flamingo() {
    // TODO
  }

  @Test
  fun ridecheck() {
    // TODO
  }
}
