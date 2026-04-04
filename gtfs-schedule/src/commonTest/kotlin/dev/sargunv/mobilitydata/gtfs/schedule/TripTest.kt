package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  route_id,service_id,trip_id,trip_headsign,direction_id,block_id,shape_id
  AB,FULLW,AB1,to Bullfrog,0,1,
  AB,FULLW,AB2,to Airport,1,2,
  STBA,FULLW,STBA,Shuttle,,,
  CITY,FULLW,CITY1,,0,,
  CITY,FULLW,CITY2,,1,,
  BFC,FULLW,BFC1,to Furnace Creek Resort,0,1,
  BFC,FULLW,BFC2,to Bullfrog,1,2,
  AAMV,WE,AAMV1,to Amargosa Valley,0,,
  AAMV,WE,AAMV2,to Airport,1,,
  AAMV,WE,AAMV3,to Amargosa Valley,0,,
  AAMV,WE,AAMV4,to Airport,1,,
  """
    .trimIndent()

private val expected =
  listOf(
    Trip(
      routeId = "AB",
      serviceId = "FULLW",
      tripId = "AB1",
      tripHeadsign = "to Bullfrog",
      directionId = DirectionId.ThatWay,
      blockId = "1",
    ),
    Trip(
      routeId = "AB",
      serviceId = "FULLW",
      tripId = "AB2",
      tripHeadsign = "to Airport",
      directionId = DirectionId.OtherWay,
      blockId = "2",
    ),
    Trip(
      routeId = "STBA",
      serviceId = "FULLW",
      tripId = "STBA",
      tripHeadsign = "Shuttle",
      directionId = null,
    ),
    Trip(
      routeId = "CITY",
      serviceId = "FULLW",
      tripId = "CITY1",
      tripHeadsign = null,
      directionId = DirectionId.ThatWay,
    ),
    Trip(
      routeId = "CITY",
      serviceId = "FULLW",
      tripId = "CITY2",
      tripHeadsign = null,
      directionId = DirectionId.OtherWay,
    ),
    Trip(
      routeId = "BFC",
      serviceId = "FULLW",
      tripId = "BFC1",
      tripHeadsign = "to Furnace Creek Resort",
      directionId = DirectionId.ThatWay,
      blockId = "1",
    ),
    Trip(
      routeId = "BFC",
      serviceId = "FULLW",
      tripId = "BFC2",
      tripHeadsign = "to Bullfrog",
      directionId = DirectionId.OtherWay,
      blockId = "2",
    ),
    Trip(
      routeId = "AAMV",
      serviceId = "WE",
      tripId = "AAMV1",
      tripHeadsign = "to Amargosa Valley",
      directionId = DirectionId.ThatWay,
    ),
    Trip(
      routeId = "AAMV",
      serviceId = "WE",
      tripId = "AAMV2",
      tripHeadsign = "to Airport",
      directionId = DirectionId.OtherWay,
    ),
    Trip(
      routeId = "AAMV",
      serviceId = "WE",
      tripId = "AAMV3",
      tripHeadsign = "to Amargosa Valley",
      directionId = DirectionId.ThatWay,
    ),
    Trip(
      routeId = "AAMV",
      serviceId = "WE",
      tripId = "AAMV4",
      tripHeadsign = "to Airport",
      directionId = DirectionId.OtherWay,
    ),
  )

class TripTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Trip>(csvContent)
    assertEquals(expected, decoded)
  }
}
