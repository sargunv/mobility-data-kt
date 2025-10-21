package dev.sargunv.mobilitydata.gbfs.v1

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
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
  @Ignore // TODO: the real manifest contains extra keys; we should ignore them or not use an enum
  fun publicbikesystem() = runTest {
    // https://pittsburgh.publicbikesystem.net/ube/gbfs/v1/
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
      val systemInformation = client.getSystemInformation().data
      assertEquals("pittsburgh", systemInformation.systemId)

      val pricingPlans = client.getSystemPricingPlans().data
      assertEquals(13, pricingPlans.size)

      val stationInfo = client.getStationInformation().data
      assertEquals(60, stationInfo.size)

      val stationStatus = client.getStationStatus().data
      assertEquals(60, stationStatus.size)

      val stationRegions = client.getSystemRegions().data
      assertEquals(0, stationRegions.size)
    }
  }

  @Test
  fun bird() = runTest {
    // https://mds.bird.co/gbfs/seattle-washington/gbfs.json
    val client = GbfsV1Client(createMockEngine("/gbfs/seattle-washington/", "bird"))
    val manifest = client.getSystemManifest("gbfs.json")
    val service = manifest.data.getService("en")

    context(service) {
      val systemInformation = client.getSystemInformation().data
      assertEquals("bird-seattle-washington", systemInformation.systemId)

      val versions = client.getVersionManifest().data
      assertEquals(2, versions.size)

      val regions = client.getSystemRegions().data
      assertEquals(1, regions.size)

      val freeBikeStatus = client.getFreeBikeStatus().data
      assertNotEquals(0, freeBikeStatus.size)
    }
  }

  @Test
  fun bcycle() = runTest {
    // https://gbfs.bcycle.com/bcycle_rtcbikeshare/gbfs.json
    // TODO actually write the test
  }

  @Test
  fun donkey() = runTest {
    // TODO: get https://stables.donkey.bike/api/public/gbfs/donkey_barcelona/gbfs.json
  }

  @Test
  fun bolt() = runTest {
    // TODO: get https://mds.bolt.eu/gbfs/1/720/gbfs
  }

  @Test
  fun lime() = runTest {
    // TODO: get https://data.lime.bike/api/partners/v1/gbfs/seattle/gbfs.json
    // and watch out; the station status feed is non conformant
  }
}
