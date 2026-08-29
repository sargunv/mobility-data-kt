package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.kotlindsv.Csv
import dev.sargunv.kotlindsv.DsvFormat
import dev.sargunv.kotlindsv.DsvNamingStrategy
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/** Strips a leading UTF-8 BOM (U+FEFF) from incoming CSV column names. */
private object StripUtf8Bom : DsvNamingStrategy {
  override fun fromDsvName(name: String): String = name.removePrefix("\uFEFF")

  override fun toDsvName(name: String): String = name
}

/** Preconfigured CSV format for GTFS .txt files. */
public val GtfsCsv: DsvFormat =
  DsvFormat(
    scheme = Csv.scheme.copy(skipEmptyLines = true),
    ignoreUnknownKeys = true,
    writeEnumsByName = false,
    treatMissingColumnsAsNull = true,
    namingStrategy = StripUtf8Bom,
  )

/**
 * Represents a three-state boolean value used for accessibility and other trinary fields in GTFS.
 */
@Serializable
@JvmInline
public value class TriState(
  /** The integer value representing the tri-state status. */
  public val value: Int
) {
  /** Companion object containing predefined tri-state constants. */
  public companion object Companion {
    /** No info, or inherit from parent. */
    public val Unknown: TriState = TriState(0)

    /** The feature is available. */
    public val Yes: TriState = TriState(1)

    /** The feature is not available. */
    public val No: TriState = TriState(2)
  }
}

/** Placeholder type for GTFS fare zones. */
public typealias Zone = Stop

/** Placeholder type for GTFS trip blocks. */
public typealias Block = Nothing
