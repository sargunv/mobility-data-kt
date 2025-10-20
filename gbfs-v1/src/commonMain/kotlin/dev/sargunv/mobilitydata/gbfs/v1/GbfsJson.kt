package dev.sargunv.mobilitydata.gbfs.v1

import kotlinx.serialization.json.Json

/** Pre-configured JSON instance for parsing GBFS feeds. */
public val GbfsJson: Json = Json {
  explicitNulls = true
  encodeDefaults = false
  ignoreUnknownKeys = true
}
