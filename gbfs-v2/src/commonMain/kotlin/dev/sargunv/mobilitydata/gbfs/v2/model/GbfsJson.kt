package dev.sargunv.mobilitydata.gbfs.v2.model

import kotlinx.serialization.json.Json

/** Pre-configured JSON instance for parsing GBFS v2 feeds. */
public val GbfsJson: Json = Json {
  explicitNulls = false
  ignoreUnknownKeys = true
}
