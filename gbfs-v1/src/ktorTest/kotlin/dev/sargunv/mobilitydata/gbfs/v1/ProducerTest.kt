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
  @Ignore // https://github.com/sargunv/mobility-data-kt/issues/16
  fun publicbikesystem() = runTest {
    val client = GbfsV1Client(createMockEngine("publicbikesystem"))

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
    val client = GbfsV1Client(createMockEngine("bird"))
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
  @Ignore // bcycle's gbfs.json contains a feed named "gbfs" which is not recognized by FeedType
  // enum
  fun bcycle() = runTest {
    val client = GbfsV1Client(createMockEngine("bcycle"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getVersionManifest()
      client.getSystemInformation()
      client.getStationInformation()
      client.getStationStatus()
      client.getSystemPricingPlans()
      client.getSystemRegions()
    }
  }

  @Test
  fun donkey() = runTest {
    val client = GbfsV1Client(createMockEngine("donkey"))

    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getStationInformation()
      client.getStationStatus()
      client.getSystemHours()
      client.getSystemRegions()
      client.getSystemPricingPlans()
    }
  }

  @Test
  fun bolt() = runTest {
    val client = GbfsV1Client(createMockEngine("bolt"))

    val manifest = client.getSystemManifest("gbfs")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getFreeBikeStatus()
      client.getSystemPricingPlans()
    }
  }

  @Test
  @Ignore // station status feed is non-conformant
  fun lime() = runTest {
    val client = GbfsV1Client(createMockEngine("lime"))

    val manifest = client.getSystemManifest("gbfs")
    val service = manifest.data.getService("en")

    context(service) {
      client.getSystemInformation()
      client.getFreeBikeStatus()
      client.getStationInformation()
      client.getStationStatus()
    }
  }
}
