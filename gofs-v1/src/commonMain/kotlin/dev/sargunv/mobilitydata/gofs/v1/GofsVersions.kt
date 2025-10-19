package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.Serializable

@Serializable
public data class GofsVersions(public val versions: List<VersionInfo>) :
  GofsFeedData, List<VersionInfo> by versions

@Serializable public data class VersionInfo(public val version: String, public val url: Url)
