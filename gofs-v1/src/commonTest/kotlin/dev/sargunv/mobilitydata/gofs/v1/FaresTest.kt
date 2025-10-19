package dev.sargunv.mobilitydata.gofs.v1

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
    "last_updated": 1609866247,
    "ttl": 86400,
    "version": "1.0",
    "data": {
      "fares": [
        {
          "fare_id": "RegularPrice",
          "currency": "CAD",
          "kilometer": [
            {
              "interval": 1.1,
              "start": 10,
              "amount": 1.1
            },
            {
              "interval": 5.1,
              "start": 25,
              "amount": 3.1
            }
          ],
          "rider": [
            {
              "amount": 2.5
            }
          ],
          "luggage": [
            {
              "start": 3,
              "amount": 5.1
            }
          ]
        }
      ]
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GofsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1609866247),
    ttl = 86400.seconds,
    version = "1.0",
    data =
      Fares(
        fares =
          listOf(
            Fare(
              fareId = "RegularPrice",
              currency = "CAD",
              kilometer =
                listOf(
                  FareEntry(interval = 1.1, start = 10, amount = 1.1),
                  FareEntry(interval = 5.1, start = 25, amount = 3.1),
                ),
              rider = listOf(FareEntry(amount = 2.5)),
              luggage = listOf(FareEntry(start = 3, amount = 5.1)),
            )
          )
      ),
  )

class FaresTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse = GofsJson.decodeFromString<GofsFeedResponse<Fares>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
