package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  shape_id,shape_pt_sequence,shape_pt_lat,shape_pt_lon,shape_dist_traveled
  p5wu,0,37.788500,-122.398500,0
  p5wu,1,37.788350,-122.398320,22.986773400646417
  p5wu,2,37.788560,-122.398040,56.908371103956796
  """
    .trimIndent()

private val expected =
  listOf(
    Shape(
      shapeId = "p5wu",
      shapePointLatitude = 37.788500,
      shapePointLongitude = -122.398500,
      shapePointSequence = 0,
      shapeDistTraveled = 0.0,
    ),
    Shape(
      shapeId = "p5wu",
      shapePointLatitude = 37.788350,
      shapePointLongitude = -122.398320,
      shapePointSequence = 1,
      shapeDistTraveled = 22.986773400646417,
    ),
    Shape(
      shapeId = "p5wu",
      shapePointLatitude = 37.788560,
      shapePointLongitude = -122.398040,
      shapePointSequence = 2,
      shapeDistTraveled = 56.908371103956796,
    ),
  )

class ShapeTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Shape>(csvContent)
    assertEquals(expected, decoded)
  }
}
