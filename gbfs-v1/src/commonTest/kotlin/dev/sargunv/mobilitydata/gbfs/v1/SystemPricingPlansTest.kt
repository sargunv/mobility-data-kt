package dev.sargunv.mobilitydata.gbfs.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent = // language=JSON
  """
  {
      "last_updated": 1640887163,
      "ttl": 0,
      "version": "2.3",
      "data": {
          "plans": [
              {
                  "plan_id": "plan2",
                  "name": "One-Way",
                  "currency": "USD",
                  "price": 2.1,
                  "is_taxable": 0,
                  "description": "Includes 10km, overage fees apply after 10km."
              }
          ]
      }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GbfsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 0.seconds,
    version = "2.3",
    data =
      SystemPricingPlans(
        plans =
          listOf(
            PricingPlan(
              planId = "plan2",
              name = "One-Way",
              currency = "USD",
              price = 2.1,
              isTaxable = false,
              description = "Includes 10km, overage fees apply after 10km.",
            )
          )
      ),
  )

class SystemPricingPlansTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<SystemPricingPlans>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }

  @Test
  fun decodeStringPrice() {
    // In GBFS v1, price can be a string or a number

    val json = // language=json
      """
      {
        "plan_id": "",
        "name": "",
        "currency": "",
        "price": "3.1",
        "is_taxable": 0,
        "description": ""
      }
      """
        .trimIndent()

    assertEquals(
      PricingPlan(
        planId = "",
        name = "",
        currency = "",
        price = 3.1,
        isTaxable = false,
        description = "",
      ),
      GbfsJson.decodeFromString<PricingPlan>(json),
    )
  }
}
