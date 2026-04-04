package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  timeframe_group_id,start_time,end_time,service_id
  peak,07:00:00,09:00:00,weekday
  offpeak,,,weekend
  """
    .trimIndent()

private val expected =
  listOf(
    Timeframe(
      timeframeGroupId = "peak",
      startTime = ServiceTime(7, 0, 0),
      endTime = ServiceTime(9, 0, 0),
      serviceId = "weekday",
    ),
    Timeframe(timeframeGroupId = "offpeak", startTime = null, endTime = null, serviceId = "weekend"),
  )

class TimeframeTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Timeframe>(csvContent)
    assertEquals(expected, decoded)
  }
}
