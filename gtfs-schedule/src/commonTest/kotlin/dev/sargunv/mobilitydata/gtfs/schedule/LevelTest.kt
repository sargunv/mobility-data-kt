package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  level_id,level_index,level_name
  level_1,0,Ground level
  level_2,-1,Platform level
  level_3,1,Mezzanine
  """
    .trimIndent()

private val expected =
  listOf(
    Level(levelId = "level_1", levelIndex = 0.0, levelName = "Ground level"),
    Level(levelId = "level_2", levelIndex = -1.0, levelName = "Platform level"),
    Level(levelId = "level_3", levelIndex = 1.0, levelName = "Mezzanine"),
  )

class LevelTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Level>(csvContent)
    assertEquals(expected, decoded)
  }
}
