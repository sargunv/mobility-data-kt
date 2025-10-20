package dev.sargunv.mobilitydata.gbfs.v2

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/** Pre-configured JSON instance for parsing GBFS feeds. */
public val GbfsJson: Json = Json {
  explicitNulls = true
  encodeDefaults = false
  ignoreUnknownKeys = true
  @OptIn(ExperimentalSerializationApi::class)
  decodeEnumsCaseInsensitive = true
}
