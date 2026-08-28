package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.Decimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class DecimalSerializerTest {
  @Serializable private data class TestData(val amount: Decimal)

  private val json = Json

  @Test
  fun jsonRoundTripsExactValues() {
    val values = listOf("0.1", "0.2", "0.3", "0.000000001", "123456789.123456789", "-3.25", "0")
    for (value in values) {
      val original = TestData(Decimal.parse(value))
      val encoded = json.encodeToString(TestData.serializer(), original)
      val decoded = json.decodeFromString(TestData.serializer(), encoded)
      assertEquals(original, decoded, value)
    }
  }

  @Test
  fun jsonUsesNumericLiteralsNotStrings() {
    val encoded = json.encodeToString(TestData.serializer(), TestData(Decimal.parse("2.5")))
    assertEquals("""{"amount":2.5}""", encoded)
    assertFalse('"' in encoded.substringAfter(':'))
  }

  @Test
  fun jsonDoesNotConvertThroughDouble() {
    val sum = Decimal.parse("0.1") + Decimal.parse("0.2")
    assertEquals(Decimal.parse("0.3"), sum)

    val encoded = json.encodeToString(TestData.serializer(), TestData(sum))
    assertEquals("""{"amount":0.3}""", encoded)

    val tenth = json.encodeToString(TestData.serializer(), TestData(Decimal.parse("0.1")))
    assertEquals("""{"amount":0.1}""", tenth)

    val decoded = json.decodeFromString(TestData.serializer(), """{"amount":0.1}""")
    assertEquals(Decimal.parse("0.1"), decoded.amount)
    assertEquals("0.1", decoded.amount.toString())
  }

  @Test
  fun jsonAcceptsNumericTokensAndRejectsStrings() {
    assertEquals(
      TestData(Decimal.parse("3.1")),
      json.decodeFromString(TestData.serializer(), """{"amount":3.1}"""),
    )
    assertFailsWith<SerializationException> {
      json.decodeFromString(TestData.serializer(), """{"amount":"3.1"}""")
    }
  }

  @Test
  fun jsonDecodesExponentFormExactly() {
    val cases =
      listOf(
        "1e-2" to "0.01",
        "1E-2" to "0.01",
        "123e-9" to "0.000000123",
        "-1.5e+1" to "-15",
        "1e3" to "1000",
      )
    for ((token, canonical) in cases) {
      val decoded = json.decodeFromString(TestData.serializer(), """{"amount":$token}""")
      assertEquals(Decimal.parse(canonical), decoded.amount, token)
      assertEquals(canonical, decoded.amount.toString(), token)
    }
  }

  @Test
  fun jsonElementContentIsCanonicalText() {
    val element = json.encodeToJsonElement(TestData.serializer(), TestData(Decimal.parse("0.001")))
    assertEquals("0.001", element.jsonObject["amount"]!!.jsonPrimitive.content)
    assertFalse(element.jsonObject["amount"]!!.jsonPrimitive.isString)
  }
}
