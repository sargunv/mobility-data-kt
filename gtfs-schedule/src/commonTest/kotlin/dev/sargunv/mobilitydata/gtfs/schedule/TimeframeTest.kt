package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.GtfsLocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
      startTime = GtfsLocalTime(7, 0, 0),
      endTime = GtfsLocalTime(9, 0, 0),
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

  @Test
  fun decodeAcceptsEndOfLocalDay() {
    val csv = // language=CSV
      """
      timeframe_group_id,start_time,end_time,service_id
      overnight,23:00:00,24:00:00,weekday
      """
        .trimIndent()

    val decoded = GtfsCsv.decodeFromString<Timeframe>(csv).single()
    assertEquals(GtfsLocalTime(23, 0, 0), decoded.startTime)
    assertEquals(GtfsLocalTime(24, 0, 0), decoded.endTime)
  }

  @Test
  fun decodeRejectsTimeAfter240000() {
    val csv = // language=CSV
      """
      timeframe_group_id,start_time,end_time,service_id
      late,25:00:00,26:00:00,weekday
      """
        .trimIndent()

    assertFailsWith<IllegalArgumentException> { GtfsCsv.decodeFromString<Timeframe>(csv) }
  }
}
