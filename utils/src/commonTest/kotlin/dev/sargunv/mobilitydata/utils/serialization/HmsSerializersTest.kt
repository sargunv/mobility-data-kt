package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.GtfsLocalTime
import dev.sargunv.mobilitydata.utils.ServiceTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

class HmsSerializersTest {
  private val inputs =
    listOf(
      "00:00:00",
      "07:30:00",
      "7:30:00",
      "7:5:9",
      "23:59:59",
      "24:00:00",
      "24:00:01",
      "25:35:00",
      "47:59:59",
      "123456789:00:00",
      "1234567890:00:00",
      "9999999999:00:00",
      "007:30:00",
      "07:030:00",
      "07:30:000",
      "07:60:00",
      "07:30:60",
      "07:99:99",
      "+7:30:00",
      "-1:30:00",
      "07:-1:00",
      " 07:30:00",
      "07:30:00 ",
      "07:30",
      "07:30:00:00",
      "07::00",
      ":30:00",
      "07:30:",
      "",
      ":",
      "::",
      "ab:cd:ef",
      "07:3a:00",
      "٧:30:00",
      "07.30.00",
    )

  /** The parser before the fast path was added. */
  private fun <T> reference(value: String, typeName: String, create: (Int, Int, Int) -> T): T {
    val parts = value.split(":")
    require(parts.size == 3) { "$typeName must be in the format HH:MM:SS, but was $value." }
    return create(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
  }

  private fun <T> decode(serializer: KSerializer<T>, value: String): T =
    Json.decodeFromJsonElement(serializer, JsonPrimitive(value))

  private fun outcome(block: () -> Any): String =
    try {
      "ok: ${block()}"
    } catch (e: IllegalArgumentException) {
      "error: ${e::class.simpleName}: ${e.message}"
    }

  @Test
  fun serviceTimeMatchesReference() {
    for (input in inputs) {
      assertEquals(
        outcome { reference(input, "ServiceTime", ::ServiceTime) },
        outcome { decode(ServiceTimeSerializer, input) },
        "input '$input'",
      )
    }
  }

  @Test
  fun gtfsLocalTimeMatchesReference() {
    for (input in inputs) {
      assertEquals(
        outcome { reference(input, "GtfsLocalTime", ::GtfsLocalTime) },
        outcome { decode(GtfsLocalTimeSerializer, input) },
        "input '$input'",
      )
    }
  }

  @Test
  fun parsesCommonShapes() {
    assertEquals(ServiceTime(7, 30, 0), decode(ServiceTimeSerializer, "07:30:00"))
    assertEquals(ServiceTime(7, 5, 9), decode(ServiceTimeSerializer, "7:5:9"))
    assertEquals(ServiceTime(25, 35, 0), decode(ServiceTimeSerializer, "25:35:00"))
    assertEquals(ServiceTime(7, 30, 0), decode(ServiceTimeSerializer, "007:30:00"))
  }
}
