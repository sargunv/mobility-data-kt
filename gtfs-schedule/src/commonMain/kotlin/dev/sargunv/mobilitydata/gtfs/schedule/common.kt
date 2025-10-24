package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.kotlindsv.Csv
import dev.sargunv.kotlindsv.DsvFormat
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/** Preconfigured CSV format for GTFS .txt files. */
public val GtfsCsv: DsvFormat =
  DsvFormat(
    scheme = Csv.scheme.copy(skipEmptyLines = true),
    ignoreUnknownKeys = true,
    writeEnumsByName = false,
    treatMissingColumnsAsNull = true,
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
public typealias Zone = Nothing

/** Placeholder type for GTFS trip blocks. */
public typealias Block = Nothing
