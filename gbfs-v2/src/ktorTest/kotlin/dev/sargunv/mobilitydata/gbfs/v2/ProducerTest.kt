package dev.sargunv.mobilitydata.gbfs.v2

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
        "mobility-data-gbfs-v2-test" -> cwd.parent!!.parent!!.parent!!.parent!!
        "gbfs-v2" -> cwd.parent!!
        else -> error("unexpected: $cwd")
      }
    return MockEngine { request ->
      val fileName = Path(request.url.fullPath).name
      val suffix = if (fileName.endsWith(".json")) "" else ".json"
      val localPath =
        Path("$projectDir", "sample-data", "gbfs-v2", resourcesSubdirectory, fileName + suffix)
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
  fun publicbikesystem() = runTest {
    val client = GbfsV2Client(createMockEngine("publicbikesystem"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getSystemPricingPlans()
      client.getSystemRegions()
      client.getStationInformation()
      client.getStationStatus()
      client.getVehicleTypes()
      client.getGeofencingZones()
      client.getVersionManifest()
    }
  }

  @Test
  fun bird() {
    // TODO
  }

  @Test
  fun donkey() {
    // TODO
  }

  @Test
  fun entur() {
    // TODO
  }

  @Test
  fun lime() {
    // TODO
  }

  @Test
  fun mobidata() {
    // TODO
  }

  @Test
  fun neuron() {
    // TODO
  }

  @Test
  fun nextbike() {
    // TODO
  }

  @Test
  fun ridedott() {
    // TODO
  }

  @Test
  fun zeuss() {
    // TODO
  }
}
