package dev.sargunv.mobilitydata.gofs.v1

import kotlinx.serialization.json.Json

/** Pre-configured JSON instance for parsing GOFS v1 feeds. */
public val GofsJson: Json = Json {
  explicitNulls = true
  encodeDefaults = false
  ignoreUnknownKeys = true
}
