package dev.sargunv.mobilitydata.gbfs.v2

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
  fun bird() = runTest {
    val client = GbfsV2Client(createMockEngine("bird"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getVersionManifest()
      client.getSystemRegions()
      client.getSystemPricingPlans()
      client.getVehicleTypes()
      client.getGeofencingZones()
      client.getStationInformation()
      client.getStationStatus()
      client.getFreeBikeStatus()
    }
  }

  @Test
  fun donkey() = runTest {
    val client = GbfsV2Client(createMockEngine("donkey"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getVersionManifest()
      client.getSystemRegions()
      client.getSystemPricingPlans()
      client.getSystemHours()
      client.getVehicleTypes()
      client.getStationInformation()
      client.getStationStatus()
      client.getFreeBikeStatus()
    }
  }

  @Test
  @Ignore // last_reported required on bike
  fun entur() = runTest {
    val client = GbfsV2Client(createMockEngine("entur"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("no")

    context(service) {
      client.getSystemInformation()
      client.getSystemPricingPlans()
      client.getSystemHours()
      client.getVehicleTypes()
      client.getGeofencingZones()
      client.getStationInformation()
      client.getStationStatus()
      client.getFreeBikeStatus()
    }
  }

  @Test
  @Ignore // lime's station_status.json is missing required fields (num_bikes_available)
  fun lime() = runTest {
    val client = GbfsV2Client(createMockEngine("lime"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getVehicleTypes()
      client.getStationInformation()
      client.getStationStatus()
      client.getFreeBikeStatus()
    }
  }

  @Test
  @Ignore // last_reported required on bike
  fun mobidata() = runTest {
    val client = GbfsV2Client(createMockEngine("mobidata"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("de")

    context(service) {
      client.getSystemInformation()
      client.getSystemPricingPlans()
      client.getSystemAlerts()
      client.getVehicleTypes()
      client.getStationInformation()
      client.getStationStatus()
      client.getFreeBikeStatus()
    }
  }

  @Test
  @Ignore // neuron's system_regions.json has region_id as integer instead of string
  fun neuron() = runTest {
    val client = GbfsV2Client(createMockEngine("neuron"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getSystemRegions()
      client.getVehicleTypes()
      client.getGeofencingZones()
      client.getStationInformation()
      client.getStationStatus()
      client.getFreeBikeStatus()
    }
  }

  @Test
  @Ignore // last_reported required on bike
  fun nextbike() = runTest {
    val client = GbfsV2Client(createMockEngine("nextbike"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("de")

    context(service) {
      client.getSystemInformation()
      client.getSystemRegions()
      client.getSystemPricingPlans()
      client.getSystemHours()
      client.getVehicleTypes()
      client.getStationInformation()
      client.getStationStatus()
      client.getFreeBikeStatus()
    }
  }

  @Test
  fun ridedott() = runTest {
    val client = GbfsV2Client(createMockEngine("ridedott"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getVersionManifest()
      client.getSystemPricingPlans()
      client.getVehicleTypes()
      client.getGeofencingZones()
      client.getStationInformation()
      client.getStationStatus()
      client.getFreeBikeStatus()
    }
  }

  @Test
  fun zeuss() = runTest {
    val client = GbfsV2Client(createMockEngine("zeus"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getVersionManifest()
      client.getSystemPricingPlans()
      client.getVehicleTypes()
      client.getGeofencingZones()
      client.getFreeBikeStatus()
    }
  }
}
