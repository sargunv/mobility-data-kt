package dev.sargunv.mobilitydata.gbfs.v1

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
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
        "mobility-data-gbfs-v1-test" -> cwd.parent!!.parent!!.parent!!.parent!!
        "gbfs-v1" -> cwd.parent!!
        else -> error("unexpected: $cwd")
      }
    return MockEngine { request ->
      val fileName = Path(request.url.fullPath).name
      val suffix = if (fileName.endsWith(".json")) "" else ".json"
      val localPath =
        Path("$projectDir", "sample-data", "gbfs-v1", resourcesSubdirectory, fileName + suffix)
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
    val client = GbfsV1Client(createMockEngine("publicbikesystem"))

    val manifest = client.getSystemManifest("gbfs").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getSystemRegions().getOrThrow()
    }
  }

  @Test
  fun bird() = runTest {
    val client = GbfsV1Client(createMockEngine("bird"))
    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getVersionManifest().getOrThrow()
      client.getSystemRegions().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
    }
  }

  @Test
  fun bcycle() = runTest {
    val client = GbfsV1Client(createMockEngine("bcycle"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")
    assertEquals(
      "https://gbfs.bcycle.com/bcycle_rtcbikeshare/gbfs.json",
      service.feeds.getValue(FeedType.SystemManifest),
    )

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getSystemRegions().getOrThrow()

      // INVALID: data is empty {}
      // client.getVersionManifest()

      // INVALID: description is missing
      // client.getSystemPricingPlans()
    }
  }

  @Test
  fun donkey() = runTest {
    val client = GbfsV1Client(createMockEngine("donkey"))

    val manifest = client.getSystemManifest("gbfs.json").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getStationInformation().getOrThrow()
      client.getStationStatus().getOrThrow()
      client.getSystemHours().getOrThrow()
      client.getSystemRegions().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
    }
  }

  @Test
  fun bolt() = runTest {
    val client = GbfsV1Client(createMockEngine("bolt"))

    val manifest = client.getSystemManifest("gbfs").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
      client.getSystemPricingPlans().getOrThrow()
    }
  }

  @Test
  fun lime() = runTest {
    val client = GbfsV1Client(createMockEngine("lime"))

    val manifest = client.getSystemManifest("gbfs").getOrThrow()
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation().getOrThrow()
      client.getFreeBikeStatus().getOrThrow()
      client.getStationInformation().getOrThrow()

      // INVALID: num_bikes_available is named num_vehicles_available
      // client.getStationStatus()
    }
  }
}
