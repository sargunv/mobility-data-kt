package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.LocalizedText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class LocalizedTextSerializerTest {
  @Serializable private data class TestData(val localizedText: LocalizedText)

  private val json = Json

  @Test
  fun testSerializeLocalizedText() {
    val localizedText = mapOf("en" to "Hello", "fr" to "Bonjour", "es" to "Hola")
    val testData = TestData(localizedText)

    val jsonElement = json.encodeToJsonElement(TestData.serializer(), testData)
    val localizedTextArray = jsonElement.jsonObject["localizedText"]!!.jsonArray

    assertEquals(3, localizedTextArray.size)

    // Verify all entries are present
    val entries =
      localizedTextArray.map {
        it.jsonObject["language"]!!.jsonPrimitive.content to
          it.jsonObject["text"]!!.jsonPrimitive.content
      }

    assertEquals(setOf("en" to "Hello", "fr" to "Bonjour", "es" to "Hola"), entries.toSet())
  }

  @Test
  fun testDeserializeLocalizedText() {
    val jsonString =
      """{"localizedText":[{"text":"Hello","language":"en"},{"text":"Bonjour","language":"fr"}]}"""

    val result = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(mapOf("en" to "Hello", "fr" to "Bonjour"), result.localizedText)
  }

  @Test
  fun testRoundTrip() {
    val original = TestData(mapOf("en" to "Welcome", "de" to "Willkommen"))

    val jsonString = json.encodeToString(TestData.serializer(), original)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(original, decoded)
  }

  @Test
  fun testEmptyMap() {
    val testData = TestData(emptyMap())

    val jsonString = json.encodeToString(TestData.serializer(), testData)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }

  @Test
  fun testSingleEntry() {
    val testData = TestData(mapOf("en" to "Goodbye"))

    val jsonString = json.encodeToString(TestData.serializer(), testData)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }

  @Test
  fun testSpecialCharactersInText() {
    val testData = TestData(mapOf("en" to "Hello \"World\"!", "zh" to "你好"))

    val jsonString = json.encodeToString(TestData.serializer(), testData)
    val decoded = json.decodeFromString(TestData.serializer(), jsonString)

    assertEquals(testData, decoded)
  }
}
