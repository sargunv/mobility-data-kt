package dev.sargunv.mobilitydata.utils.serialization

import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

internal fun <T> assertJsonWrapsOriginalIae(serializer: KSerializer<T>, invalid: String) {
  val error =
    assertFailsWith<SerializationException> {
      Json.decodeFromJsonElement(serializer, JsonPrimitive(invalid))
    }
  assertContains(error.message.orEmpty(), "at path:")
  val cause = error.cause
  assertIs<IllegalArgumentException>(cause)
  assertTrue(cause !is SerializationException)
}
