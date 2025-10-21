package dev.sargunv.mobilitydata.gofs.v1

import kotlinx.serialization.json.Json

/** Pre-configured JSON instance for parsing GOFS feeds. */
public val GofsJson: Json = Json {
  explicitNulls = true
  encodeDefaults = false
  ignoreUnknownKeys = true
  isLenient = true
}
