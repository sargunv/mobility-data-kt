package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.RgbColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

class RgbColorSerializerTest {
  private val triplets =
    listOf(
      "000000",
      "FFFFFF",
      "ffffff",
      "005B95",
      "e12a40",
      "aBcDeF",
      "12345",
      "1234567",
      "",
      "GGGGGG",
      "00000g",
      "#FFFFFF",
      " FFFFFF",
      "FFFFFF ",
      "+FFFFF",
      "-00001",
      "0xFFFF",
      "００００００",
    )

  /** The parser before the regex was replaced. */
  private fun referenceTriplet(str: String): RgbColor {
    require(str.matches(Regex("[0-9a-fA-F]{6}"))) { "Invalid RGB color code: $str" }
    return RgbColor(str.toInt(16))
  }

  private fun referenceCode(str: String): RgbColor {
    require(str.matches(Regex("#[0-9a-fA-F]{6}"))) { "Invalid RGB color code: $str" }
    return RgbColor(str.substring(1).toInt(16))
  }

  private fun outcome(block: () -> RgbColor): String =
    try {
      "ok: ${block().value}"
    } catch (e: IllegalArgumentException) {
      "error: ${e::class.simpleName}: ${e.message}"
    }

  private fun decode(serializer: KSerializer<RgbColor>, str: String): RgbColor =
    Json.decodeFromJsonElement(serializer, JsonPrimitive(str))

  @Test
  fun tripletMatchesReference() {
    for (str in triplets) {
      assertEquals(
        outcome { referenceTriplet(str) },
        outcome { decode(RgbColorTripletSerializer, str) },
        "input '$str'",
      )
    }
  }

  @Test
  fun codeMatchesReference() {
    for (str in triplets + triplets.map { "#$it" }) {
      assertEquals(
        outcome { referenceCode(str) },
        outcome { decode(RgbColorCodeSerializer, str) },
        "input '$str'",
      )
    }
  }
}
