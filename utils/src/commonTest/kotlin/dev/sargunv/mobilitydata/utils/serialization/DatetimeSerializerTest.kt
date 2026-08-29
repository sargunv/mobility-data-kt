package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.Timestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.UtcOffset
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalTime::class)
class DatetimeSerializerTest {
  @Serializable
  private data class TestData(
    @Serializable(with = DatetimeSerializer::class) val datetime: Timestamp
  )

  @Test
  fun testSerializeDropsFractionalSeconds() {
    val timestamp = Timestamp(Instant.parse("2021-05-17T15:00:00.500Z"), UtcOffset.ZERO)
    val jsonElement = Json.encodeToJsonElement(TestData.serializer(), TestData(timestamp))
    val datetimeValue = jsonElement.jsonObject["datetime"]!!.jsonPrimitive

    assertEquals("2021-05-17T15:00:00Z", datetimeValue.content)
  }
}
