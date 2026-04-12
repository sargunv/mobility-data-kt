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
    val service = client.getServiceManifest("gbfs.json").getOrThrow().data

    context(service) {
      client.getVersionManifest().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getSystemInformation().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getVehicleStatus().getOrThrow()
      client.getVehicleTypes().getOrThrow()
    }
  }

  @Test
  fun citiz() = runTest {
    val client = GbfsV3Client(createMockEngine("citiz"))
    val service = client.getServiceManifest("gbfs.json").getOrThrow().data

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVersionManifest().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getVehicleStatus().getOrThrow()
    }
  }

  @Test
  fun cooltra() = runTest {
    val client = GbfsV3Client(createMockEngine("cooltra"))
    val service = client.getServiceManifest("gbfs.json").getOrThrow().data

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getVehicleStatus().getOrThrow()
    }
  }

  @Test
  fun cyclocity() = runTest {
    val client = GbfsV3Client(createMockEngine("cyclocity"))
    val service = client.getServiceManifest("gbfs.json").getOrThrow().data

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVersionManifest().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
    }
  }

  @Test
  fun ecovelo() = runTest {
    val client = GbfsV3Client(createMockEngine("ecovelo"))
    val service = client.getServiceManifest("gbfs.json").getOrThrow().data

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVersionManifest().getOrThrow()
      client.getSystemRegions().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getSystemAlerts().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getVehicleStatus().getOrThrow()
      client.getVehicleTypes().getOrThrow()
    }
  }

  @Test
  fun flamingo() = runTest {
    val client = GbfsV3Client(createMockEngine("flamingo"))
    val service = client.getServiceManifest("gbfs.json").getOrThrow().data

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVersionManifest().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getSystemAlerts().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getVehicleStatus().getOrThrow()
    }
  }

  @Test
  fun ridecheck() = runTest {
    val client = GbfsV3Client(createMockEngine("ridecheck"))
    val service = client.getServiceManifest("gbfs.json").getOrThrow().data

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getVehicleStatus().getOrThrow()
    }
  }
}
