package dev.sargunv.mobilitydata.mdb.v1

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

/** Pre-configured JSON instance for the Mobility Database Catalog API. */
public val MdbJson: Json = Json {
  explicitNulls = true
  encodeDefaults = false
  ignoreUnknownKeys = true
  isLenient = true
}

/** Instant encoded as an ISO-8601 date-time string. */
@OptIn(ExperimentalTime::class)
public typealias IsoDateTime = @Serializable(with = IsoDateTimeSerializer::class) Instant

/**
 * Serializer for [Instant] that reads and writes ISO-8601 date-time strings.
 *
 * The catalog sometimes omits the offset. Those values are UTC.
 */
@OptIn(ExperimentalTime::class)
public object IsoDateTimeSerializer : KSerializer<Instant> {
  private val delegate = String.serializer()

  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: Instant) {
    delegate.serialize(encoder, value.toString())
  }

  override fun deserialize(decoder: Decoder): Instant =
    Instant.parse(withUtcOffset(delegate.deserialize(decoder)))
}

private fun withUtcOffset(value: String): String {
  val time = value.substringAfter('T', missingDelimiterValue = "")
  if (time.endsWith("Z", ignoreCase = true) || time.contains('+') || time.contains('-')) {
    return value
  }
  return "${value}Z"
}
