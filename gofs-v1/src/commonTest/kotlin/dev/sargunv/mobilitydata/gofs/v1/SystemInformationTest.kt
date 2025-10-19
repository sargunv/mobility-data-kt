package dev.sargunv.mobilitydata.gofs.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val jsonContent = // language=JSON
  """
  {
    "last_updated": 1611598155,
    "ttl": 1800,
    "version": "1.0",
    "data": {
      "language": "en",
      "timezone": "America/Toronto",
      "name": "Example MicroTransit",
      "short_name": "Micro",
      "operator": "MicroTransit, Inc",
      "url": "https://www.example.com",
      "subscribe_url": "https://www.example.com",
      "start_date": "20100610",
      "phone_number": "+18005551234",
      "email": "customerservice@example.com",
      "feed_contact_email": "datafeed@example.com"
    }
  }
  """
    .trimIndent()

@OptIn(ExperimentalTime::class)
private val expectedResponse =
  GofsFeedResponse(
    lastUpdated = Instant.fromEpochSeconds(1611598155),
    ttl = 1800.seconds,
    version = "1.0",
    data =
      SystemInformation(
        language = "en",
        timezone = TimeZone.of("America/Toronto"),
        name = "Example MicroTransit",
        shortName = "Micro",
        operator = "MicroTransit, Inc",
        url = "https://www.example.com",
        subscribeUrl = "https://www.example.com",
        startDate = LocalDate(2010, 6, 10),
        phoneNumber = "+18005551234",
        email = "customerservice@example.com",
        feedContactEmail = "datafeed@example.com",
      ),
  )

class SystemInformationTest {
  @Test
  fun encode() {
    val expectedJson = Json.decodeFromString<JsonElement>(jsonContent)
    val encodedJson = GofsJson.encodeToJsonElement(expectedResponse)
    assertEquals(expectedJson, encodedJson)
  }

  @Test
  fun decode() {
    val decodedResponse =
      GofsJson.decodeFromString<GofsFeedResponse<SystemInformation>>(jsonContent)
    assertEquals(expectedResponse, decodedResponse)
  }
}
