package dev.sargunv.mobilitydata.gbfs.v2

import kotlinx.serialization.json.Json

/** Pre-configured JSON instance for parsing GBFS v2 feeds. */
public val GbfsJson: Json = Json {
  explicitNulls = true
  encodeDefaults = false
  ignoreUnknownKeys = true
}
