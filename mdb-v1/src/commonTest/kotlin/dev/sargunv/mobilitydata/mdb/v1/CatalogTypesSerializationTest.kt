package dev.sargunv.mobilitydata.mdb.v1

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val tokenJson = // language=JSON
  """
  {
    "access_token": "ya29.example",
    "expiration_datetime_utc": "2020-01-01T00:00:00Z",
    "token_type": "Bearer"
  }
  """
    .trimIndent()

private val datasetJson = // language=JSON
  """
  {
    "id": "mdb-10-202402080058",
    "feed_id": "mdb-10",
    "downloaded_at": "2023-07-10T22:06:00Z",
    "hash": "6497e85e34390b8b377130881f2f10ec29c18a80dd6005d504a2038cdd00aa71"
  }
  """
    .trimIndent()

private val locationJson = // language=JSON
  """
  {
    "location_id": 175905,
    "name": "Montréal",
    "location_type": "municipality",
    "country_code": "CA",
    "subdivision_name": "Quebec"
  }
  """
    .trimIndent()

private val licenseJson = // language=JSON
  """
  {
    "id": "0BSD",
    "name": "BSD Zero Clause License",
    "is_spdx": true,
    "license_rules": [
      {
        "name": "commercial-use",
        "label": "Commercial use",
        "type": "permission"
      }
    ]
  }
  """
    .trimIndent()

private val expectedToken =
  AccessToken(
    accessToken = "ya29.example",
    expirationDatetimeUtc = Instant.parse("2020-01-01T00:00:00Z"),
    tokenType = "Bearer",
  )

private val expectedDataset =
  GtfsDataset(
    id = "mdb-10-202402080058",
    feedId = FeedId("mdb-10"),
    downloadedAt = Instant.parse("2023-07-10T22:06:00Z"),
    hash = "6497e85e34390b8b377130881f2f10ec29c18a80dd6005d504a2038cdd00aa71",
  )

private val expectedLocation =
  LocationSearchResult(
    locationId = 175905,
    name = "Montréal",
    locationType = LocationType.Municipality,
    countryCode = "CA",
    subdivisionName = "Quebec",
  )

private val expectedLicense = License(id = "0BSD", name = "BSD Zero Clause License", isSpdx = true)

private val expectedLicenseWithRules =
  LicenseWithRules(
    id = "0BSD",
    name = "BSD Zero Clause License",
    isSpdx = true,
    licenseRules =
      listOf(
        LicenseRule(
          name = "commercial-use",
          label = "Commercial use",
          type = LicenseRuleType.Permission,
        )
      ),
  )

class CatalogTypesSerializationTest {
  @Test
  fun encodeToken() {
    val expected = Json.decodeFromString<JsonElement>(tokenJson)
    val encoded = MdbJson.encodeToJsonElement(expectedToken)
    assertEquals(expected, encoded)
  }

  @Test
  fun decodeToken() {
    val decoded = MdbJson.decodeFromString<AccessToken>(tokenJson)
    assertEquals(expectedToken, decoded)
    assertIs<AccessToken>(decoded)
  }

  @Test
  fun encodeDataset() {
    val expected = Json.decodeFromString<JsonElement>(datasetJson)
    val encoded = MdbJson.encodeToJsonElement(expectedDataset)
    assertEquals(expected, encoded)
  }

  @Test
  fun decodeDataset() {
    val decoded = MdbJson.decodeFromString<GtfsDataset>(datasetJson)
    assertEquals(expectedDataset, decoded)
    assertEquals(Instant.parse("2023-07-10T22:06:00Z"), decoded.downloadedAt)
  }

  @Test
  fun encodeLocation() {
    val expected = Json.decodeFromString<JsonElement>(locationJson)
    val encoded = MdbJson.encodeToJsonElement(expectedLocation)
    assertEquals(expected, encoded)
  }

  @Test
  fun decodeLocation() {
    val decoded = MdbJson.decodeFromString<LocationSearchResult>(locationJson)
    assertEquals(expectedLocation, decoded)
    assertEquals("CA", decoded.countryCode)
  }

  @Test
  fun encodeLicenseOmitsRules() {
    val expected =
      Json.decodeFromString<JsonElement>(
        """{"id":"0BSD","name":"BSD Zero Clause License","is_spdx":true}"""
      )
    val encoded = MdbJson.encodeToJsonElement(expectedLicense)
    assertEquals(expected, encoded)
  }

  @Test
  fun decodeLicenseFromListRow() {
    val json = """{"id":"0BSD","name":"BSD Zero Clause License","is_spdx":true}"""
    val decoded = MdbJson.decodeFromString<License>(json)
    assertEquals(expectedLicense, decoded)
  }

  @Test
  fun encodeLicenseWithRules() {
    val expected = Json.decodeFromString<JsonElement>(licenseJson)
    val encoded = MdbJson.encodeToJsonElement(expectedLicenseWithRules)
    assertEquals(expected, encoded)
  }

  @Test
  fun decodeLicenseWithRules() {
    val decoded = MdbJson.decodeFromString<LicenseWithRules>(licenseJson)
    assertEquals(expectedLicenseWithRules, decoded)
    assertEquals(1, decoded.licenseRules?.size)
  }

  @Test
  fun decodeMatchingLicense() {
    val json =
      """{"license_id":"CC-BY-4.0","match_type":"heuristic","confidence":0.99,"spdx_id":"CC-BY-4.0"}"""
    val decoded = MdbJson.decodeFromString<MatchingLicense>(json)
    assertEquals("CC-BY-4.0", decoded.licenseId)
    assertEquals(LicenseMatchType.Heuristic, decoded.matchType)
    assertEquals(0.99, decoded.confidence)
  }

  @Test
  fun decodeOmittedSearchSeasonalIsFalse() {
    val json = """{"id":"mdb-1","data_type":"gtfs"}"""
    val decoded = MdbJson.decodeFromString<SearchFeedItem>(json)
    assertEquals(false, decoded.seasonal)
    assertEquals(FeedDataType.Gtfs, decoded.dataType)
  }
}
