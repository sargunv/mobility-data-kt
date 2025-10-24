package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  table_name,field_name,language,translation,record_id,record_sub_id,field_value
  agency,agency_name,es,Autoridad de Tránsito de Demostración,DTA,,
  stops,stop_name,es,Esquina de las calles First y Mission,,1_Mission_0,
  routes,route_long_name,es,Ruta Este,R1,,
  stop_times,stop_headsign,es,Centro de la ciudad,,,Uptown
  """
    .trimIndent()

private val expected =
  listOf(
    Translation(
      tableName = "agency",
      fieldName = "agency_name",
      language = "es",
      translation = "Autoridad de Tránsito de Demostración",
      recordId = "DTA",
      recordSubId = null,
      fieldValue = null,
    ),
    Translation(
      tableName = "stops",
      fieldName = "stop_name",
      language = "es",
      translation = "Esquina de las calles First y Mission",
      recordId = null,
      recordSubId = "1_Mission_0",
      fieldValue = null,
    ),
    Translation(
      tableName = "routes",
      fieldName = "route_long_name",
      language = "es",
      translation = "Ruta Este",
      recordId = "R1",
      recordSubId = null,
      fieldValue = null,
    ),
    Translation(
      tableName = "stop_times",
      fieldName = "stop_headsign",
      language = "es",
      translation = "Centro de la ciudad",
      recordId = null,
      recordSubId = null,
      fieldValue = "Uptown",
    ),
  )

class TranslationTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Translation>(csvContent)
    assertEquals(expected, decoded)
  }
}
