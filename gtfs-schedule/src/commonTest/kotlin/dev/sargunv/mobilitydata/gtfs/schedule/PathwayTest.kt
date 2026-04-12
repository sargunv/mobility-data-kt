package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

private val csvContent = // language=CSV
  """
  pathway_id,from_stop_id,to_stop_id,pathway_mode,is_bidirectional,length,traversal_time,stair_count,max_slope,min_width,signposted_as,reversed_signposted_as
  pathway_1,stop_a,stop_b,1,1,50.5,45,,,1.2,To Platform B,To Entrance A
  pathway_2,stop_b,stop_c,2,0,10.0,30,20,0.1,,Stairs to Upper Level,
  pathway_3,stop_c,stop_d,5,1,,60,,,,Elevator,Elevator
  """
    .trimIndent()

private val expected =
  listOf(
    Pathway(
      pathwayId = "pathway_1",
      fromStopId = "stop_a",
      toStopId = "stop_b",
      pathwayMode = PathwayMode.Walkway,
      isBidirectional = true,
      length = 50.5,
      traversalTime = 45.seconds,
      minWidth = 1.2,
      signpostedAs = "To Platform B",
      reversedSignpostedAs = "To Entrance A",
    ),
    Pathway(
      pathwayId = "pathway_2",
      fromStopId = "stop_b",
      toStopId = "stop_c",
      pathwayMode = PathwayMode.Stairs,
      isBidirectional = false,
      length = 10.0,
      traversalTime = 30.seconds,
      stairCount = 20,
      maxSlope = 0.1,
      signpostedAs = "Stairs to Upper Level",
    ),
    Pathway(
      pathwayId = "pathway_3",
      fromStopId = "stop_c",
      toStopId = "stop_d",
      pathwayMode = PathwayMode.Elevator,
      isBidirectional = true,
      traversalTime = 60.seconds,
      signpostedAs = "Elevator",
      reversedSignpostedAs = "Elevator",
    ),
  )

class PathwayTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Pathway>(csvContent)
    assertEquals(expected, decoded)
  }
}
