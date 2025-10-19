package dev.sargunv.mobilitydata.gbfs.v2.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent1 = // language=JSON
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
                  "is_taxable": false,
                  "description": "Includes 10km, overage fees apply after 10km.",
                  "per_km_pricing": [
                      {
                          "start": 10,
                          "rate": 1.1,
                          "interval": 1,
                          "end": 25
                      },
                      {
                          "start": 25,
                          "rate": 0.5,
                          "interval": 1
                      },
                      {
                          "start": 25,
                          "rate": 3.1,
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
              perKmPricing =
                listOf(
                  PricingInterval(start = 10, rate = 1.1, interval = 1, end = 25),
                  PricingInterval(start = 25, rate = 0.5, interval = 1, end = null),
                  PricingInterval(start = 25, rate = 3.1, interval = 5, end = null),
                ),
            )
          )
      ),
  )

private val jsonContent2 = // language=json
  """
  {
      "last_updated": 1640887163,
      "ttl": 0,
      "version": "2.3",
      "data": {
          "plans": [
              {
                  "plan_id": "plan3",
                  "name": "Simple Rate",
                  "currency": "CAD",
                  "price": 3.1,
                  "is_taxable": true,
                  "description": "$3 unlock fee, $0.25 per kilometer and 0.50 per minute.",
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
    lastUpdated = Instant.fromEpochSeconds(1640887163),
    ttl = 0.seconds,
    version = "2.3",
    data =
      SystemPricingPlans(
        plans =
          listOf(
            PricingPlan(
              planId = "plan3",
              name = "Simple Rate",
              currency = "CAD",
              price = 3.1,
              isTaxable = true,
              description = "$3 unlock fee, $0.25 per kilometer and 0.50 per minute.",
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
  fun encodePhysical() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent1)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse1)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decodePhysical() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<SystemPricingPlans>>(jsonContent1)
    assertEquals(expectedResponse1, decodedResponse)
  }

  @Test
  fun encodeVirtual() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent2)
    val encodedJson = GbfsJson.encodeToJsonElement(expectedResponse2)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decodeVirtual() {
    val decodedResponse =
      GbfsJson.decodeFromString<GbfsFeedResponse<SystemPricingPlans>>(jsonContent2)
    assertEquals(expectedResponse2, decodedResponse)
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
        "is_taxable": false,
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
