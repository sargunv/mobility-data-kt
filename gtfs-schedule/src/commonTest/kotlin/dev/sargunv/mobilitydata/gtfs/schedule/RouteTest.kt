package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private val csvContent = // language=CSV
  """
  route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color
  AB,DTA,10,Airport - Bullfrog,,3,,,
  BFC,DTA,20,Bullfrog - Furnace Creek Resort,,3,,,
  STBA,DTA,30,Stagecoach - Airport Shuttle,,3,,,
  CITY,DTA,40,City,,3,,,
  AAMV,DTA,50,Airport - Amargosa Valley,,3,,,
  """
    .trimIndent()

private val expected =
  listOf(
    Route(
      routeId = "AB",
      agencyId = "DTA",
      routeShortName = "10",
      routeLongName = "Airport - Bullfrog",
      routeType = RouteType.Bus,
    ),
    Route(
      routeId = "BFC",
      agencyId = "DTA",
      routeShortName = "20",
      routeLongName = "Bullfrog - Furnace Creek Resort",
      routeType = RouteType.Bus,
    ),
    Route(
      routeId = "STBA",
      agencyId = "DTA",
      routeShortName = "30",
      routeLongName = "Stagecoach - Airport Shuttle",
      routeType = RouteType.Bus,
    ),
    Route(
      routeId = "CITY",
      agencyId = "DTA",
      routeShortName = "40",
      routeLongName = "City",
      routeType = RouteType.Bus,
    ),
    Route(
      routeId = "AAMV",
      agencyId = "DTA",
      routeShortName = "50",
      routeLongName = "Airport - Amargosa Valley",
      routeType = RouteType.Bus,
    ),
  )

class RouteTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Route>(csvContent)
    assertEquals(expected, decoded)
  }

  @Test
  fun emptyContinuousPickupAndDropOffDecodeAsNull() {
    val csv = // language=CSV
      """
      route_id,route_type,continuous_pickup,continuous_drop_off
      EMPTY,3,,
      EXPLICIT,3,1,1
      CONTINUOUS,3,0,2
      """
        .trimIndent()

    val decoded = GtfsCsv.decodeFromString<Route>(csv)
    assertNull(decoded[0].continuousPickup)
    assertNull(decoded[0].continuousDropOff)
    assertEquals(ContinuousPickupDropOff.NoContinuous, decoded[1].continuousPickup)
    assertEquals(ContinuousPickupDropOff.NoContinuous, decoded[1].continuousDropOff)
    assertEquals(ContinuousPickupDropOff.Continuous, decoded[2].continuousPickup)
    assertEquals(ContinuousPickupDropOff.PhoneAgency, decoded[2].continuousDropOff)
  }
}
