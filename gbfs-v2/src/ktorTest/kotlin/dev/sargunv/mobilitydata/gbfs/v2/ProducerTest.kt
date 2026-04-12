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

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getSystemRegions().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getVersionManifest().getOrThrow()
    }
  }

  @Test
  fun bird() = runTest {
    val client = GbfsV2Client(createMockEngine("bird"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVersionManifest().getOrThrow()
      client.getSystemRegions().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
    }
  }

  @Test
  fun donkey() = runTest {
    val client = GbfsV2Client(createMockEngine("donkey"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVersionManifest().getOrThrow()
      client.getSystemRegions().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getSystemHours().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
    }
  }

  @Test
  fun entur() = runTest {
    val client = GbfsV2Client(createMockEngine("entur"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("no")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getSystemHours().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
    }
  }

  @Test
  fun lime() = runTest {
    val client = GbfsV2Client(createMockEngine("lime"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()

      // INVALID: num_bikes_available is named num_vehicles_available
      // client.getStationStatus()
    }
  }

  @Test
  fun mobidata() = runTest {
    val client = GbfsV2Client(createMockEngine("mobidata"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("de")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getSystemAlerts().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
    }
  }

  @Test
  fun neuron() = runTest {
    val client = GbfsV2Client(createMockEngine("neuron"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()

      // INVALID: station_id is int instead of string
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()

      // INVALID: region_id is int instead of string
      client.getSystemRegions().getOrThrow()

      // INVALID: rules should be array
      // client.getGeofencingZones()
    }
  }

  @Test
  fun nextbike() = runTest {
    val client = GbfsV2Client(createMockEngine("nextbike"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("de")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getSystemRegions().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getSystemHours().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
    }
  }

  @Test
  fun ridedott() = runTest {
    val client = GbfsV2Client(createMockEngine("ridedott"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVersionManifest().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
    }
  }

  @Test
  fun zeuss() = runTest {
    val client = GbfsV2Client(createMockEngine("zeus"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVersionManifest().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getVehicleTypes().getOrThrow()
      client.getGeofencingZones().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
    }
  }
}
