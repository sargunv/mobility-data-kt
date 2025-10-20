package dev.sargunv.mobilitydata.gbfs.v3

import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent1 = // language=JSON
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl": 0,
    "version": "3.0",
    "data": {
      "plans": [
        {
          "plan_id": "plan2",
          "name": [
            {
              "text": "One-Way",
              "language": "en"
            }
          ],
          "currency": "USD",
          "price": 2.01,
          "is_taxable": false,
          "description": [
            {
              "text": "Includes 10km, overage fees apply after 10km.",
              "language": "en"
            }
          ],
          "per_km_pricing": [
            {
              "start": 10,
              "rate": 1.01,
              "interval": 1,
              "end": 25
            },
            {
              "start": 25,
              "rate": 0.51,
              "interval": 1
            },
            {
              "start": 25,
              "rate": 3.01,
              "interval": 5
            }
          ]
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse1 =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 0.seconds,
    version = "3.0",
    data =
      SystemPricingPlans(
        plans =
          listOf(
            PricingPlan(
              planId = "plan2",
              name = mapOf("en" to "One-Way"),
              currency = "USD",
              price = 2.01,
              isTaxable = false,
              description = mapOf("en" to "Includes 10km, overage fees apply after 10km."),
              perKmPricing =
                listOf(
                  PricingInterval(start = 10, rate = 1.01, interval = 1, end = 25),
                  PricingInterval(start = 25, rate = 0.51, interval = 1, end = null),
                  PricingInterval(start = 25, rate = 3.01, interval = 5, end = null),
                ),
            )
          )
      ),
  )

private val jsonContent2 = // language=json
  """
  {
    "last_updated": "2023-07-17T13:34:13+02:00",
    "ttl": 0,
    "version": "3.0",
    "data": {
      "plans": [
        {
          "plan_id": "plan3",
          "name": [
            {
              "text": "Simple Rate",
              "language": "en"
            }
          ],
          "currency": "CAD",
          "price": 3.01,
          "is_taxable": true,
          "description": [
            {
              "text": "$3 unlock fee, $0.25 per kilometer and 0.50 per minute.",
              "language": "en"
            }
          ],
          "per_km_pricing": [
            {
              "start": 0,
              "rate": 0.25,
              "interval": 1
            }
          ],
          "per_min_pricing": [
            {
              "start": 0,
              "rate": 0.5,
              "interval": 1
            }
          ]
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse2 =
  GbfsFeedResponse(
    lastUpdated = Timestamp.parse("2023-07-17T13:34:13+02:00"),
    ttl = 0.seconds,
    version = "3.0",
    data =
      SystemPricingPlans(
        plans =
          listOf(
            PricingPlan(
              planId = "plan3",
              name = mapOf("en" to "Simple Rate"),
              currency = "CAD",
              price = 3.01,
              isTaxable = true,
              description =
                mapOf("en" to "$3 unlock fee, $0.25 per kilometer and 0.50 per minute."),
              perKmPricing =
                listOf(PricingInterval(start = 0, rate = 0.25, interval = 1, end = null)),
              perMinPricing =
                listOf(PricingInterval(start = 0, rate = 0.5, interval = 1, end = null)),
            )
          )
      ),
  )

class SystemPricingPlansTest {
  @Test
  fun encode1() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent1)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse1)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode1() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<SystemPricingPlans>>(jsonContent1)
    assertEquals(expectedResponse1, decodedResponse)
  }

  @Test
  fun encode2() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent2)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse2)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode2() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<SystemPricingPlans>>(jsonContent2)
    assertEquals(expectedResponse2, decodedResponse)
  }
}
