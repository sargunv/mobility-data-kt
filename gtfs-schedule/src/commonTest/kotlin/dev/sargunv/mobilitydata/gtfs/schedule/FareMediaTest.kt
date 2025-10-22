package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  fare_media_id,fare_media_name,fare_media_type
  cash,Cash,0
  clipper,Clipper,2
  """
    .trimIndent()

private val expected =
  listOf(
    FareMedia(fareMediaId = "cash", fareMediaName = "Cash", fareMediaType = FareMediaType.None),
    FareMedia(
      fareMediaId = "clipper",
      fareMediaName = "Clipper",
      fareMediaType = FareMediaType.PhysicalTransitCard,
    ),
  )

class FareMediaTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<FareMedia>(csvContent)
    assertEquals(expected, decoded)
  }
}
