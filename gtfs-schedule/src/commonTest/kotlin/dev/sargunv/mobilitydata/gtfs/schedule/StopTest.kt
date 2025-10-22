package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  stop_id,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url
  FUR_CREEK_RES,Furnace Creek Resort (Demo),,36.425288,-117.133162,,
  BEATTY_AIRPORT,Nye County Airport (Demo),,36.868446,-116.784582,,
  BULLFROG,Bullfrog (Demo),,36.88108,-116.81797,,
  STAGECOACH,Stagecoach Hotel & Casino (Demo),,36.915682,-116.751677,,
  NADAV,North Ave / D Ave N (Demo),,36.914893,-116.76821,,
  NANAA,North Ave / N A Ave (Demo),,36.914944,-116.761472,,
  DADAN,Doing Ave / D Ave N (Demo),,36.909489,-116.768242,,
  EMSI,E Main St / S Irving St (Demo),,36.905697,-116.76218,,
  AMV,Amargosa Valley (Demo),,36.641496,-116.40094,,
  """
    .trimIndent()

private val expected =
  listOf(
    Stop(
      stopId = "FUR_CREEK_RES",
      stopName = "Furnace Creek Resort (Demo)",
      stopLatitude = 36.425288,
      stopLongitude = -117.133162,
    ),
    Stop(
      stopId = "BEATTY_AIRPORT",
      stopName = "Nye County Airport (Demo)",
      stopLatitude = 36.868446,
      stopLongitude = -116.784582,
    ),
    Stop(
      stopId = "BULLFROG",
      stopName = "Bullfrog (Demo)",
      stopLatitude = 36.88108,
      stopLongitude = -116.81797,
    ),
    Stop(
      stopId = "STAGECOACH",
      stopName = "Stagecoach Hotel & Casino (Demo)",
      stopLatitude = 36.915682,
      stopLongitude = -116.751677,
    ),
    Stop(
      stopId = "NADAV",
      stopName = "North Ave / D Ave N (Demo)",
      stopLatitude = 36.914893,
      stopLongitude = -116.76821,
    ),
    Stop(
      stopId = "NANAA",
      stopName = "North Ave / N A Ave (Demo)",
      stopLatitude = 36.914944,
      stopLongitude = -116.761472,
    ),
    Stop(
      stopId = "DADAN",
      stopName = "Doing Ave / D Ave N (Demo)",
      stopLatitude = 36.909489,
      stopLongitude = -116.768242,
    ),
    Stop(
      stopId = "EMSI",
      stopName = "E Main St / S Irving St (Demo)",
      stopLatitude = 36.905697,
      stopLongitude = -116.76218,
    ),
    Stop(
      stopId = "AMV",
      stopName = "Amargosa Valley (Demo)",
      stopLatitude = 36.641496,
      stopLongitude = -116.40094,
    ),
  )

class StopTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Stop>(csvContent)
    assertEquals(expected, decoded)
  }
}
