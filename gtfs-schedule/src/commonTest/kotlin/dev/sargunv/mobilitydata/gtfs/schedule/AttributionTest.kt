package dev.sargunv.mobilitydata.gtfs.schedule

import kotlin.test.Test
import kotlin.test.assertEquals

private val csvContent = // language=CSV
  """
  attribution_id,organization_name,is_producer,is_operator,is_authority,attribution_url,attribution_email,attribution_phone
  attr1,Example Transit Authority,1,0,1,https://example.com,contact@example.com,555-1234
  """
    .trimIndent()

private val expected =
  listOf(
    Attribution(
      attributionId = "attr1",
      organizationName = "Example Transit Authority",
      isProducer = true,
      isOperator = false,
      isAuthority = true,
      attributionUrl = "https://example.com",
      attributionEmail = "contact@example.com",
      attributionPhone = "555-1234",
    )
  )

class AttributionTest {
  @Test
  fun decode() {
    val decoded = GtfsCsv.decodeFromString<Attribution>(csvContent)
    assertEquals(expected, decoded)
  }
}
