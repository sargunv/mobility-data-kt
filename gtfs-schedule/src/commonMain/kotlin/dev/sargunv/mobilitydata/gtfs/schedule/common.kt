package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.kotlindsv.Csv
import dev.sargunv.kotlindsv.DsvFormat
import kotlin.jvm.JvmInline
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

/**
 * Preconfigured CSV codec for GTFS .txt files.
 *
 * Decode accepts producer rows that omit trailing empty optional fields. RFC 4180 says each record
 * should have the same field count, and GTFS File Requirements cite RFC 4180 for quoting; accepting
 * short rows is producer interoperability rather than a hard GTFS requirement. Present cells stay
 * aligned with the header, and required fields that are omitted still fail during typed decode.
 */
public object GtfsCsv {
  /** Underlying kotlin-dsv format used after short rows are normalized. */
  public val format: DsvFormat =
    DsvFormat(
      scheme = Csv.scheme.copy(skipEmptyLines = true),
      ignoreUnknownKeys = true,
      writeEnumsByName = false,
      treatMissingColumnsAsNull = true,
    )

  /** Decodes a list of [T] from a GTFS CSV string. */
  public inline fun <reified T> decodeFromString(string: String): List<T> =
    decodeFromString(serializer(), string)

  /** Decodes a list of values from a GTFS CSV string using [deserializer]. */
  public fun <T> decodeFromString(
    deserializer: DeserializationStrategy<T>,
    string: String,
  ): List<T> = format.decodeFromString(deserializer, padOmittedTrailingCsvFields(string))

  /** Lazily decodes values of [T] from a UTF-8 GTFS CSV [source]. */
  public inline fun <reified T> decodeFromSource(source: Source): Sequence<T> =
    decodeFromSource(source, serializer())

  /** Lazily decodes values from a UTF-8 GTFS CSV [source] using [deserializer]. */
  public fun <T> decodeFromSource(
    source: Source,
    deserializer: DeserializationStrategy<T>,
  ): Sequence<T> =
    format.decodeFromSource(source.withPaddedOmittedTrailingCsvFields(), deserializer)

  /** Encodes [value] to a GTFS CSV string. */
  public inline fun <reified T> encodeToString(value: List<T>): String =
    format.encodeToString(value)

  /** Encodes [value] to a GTFS CSV string using [serializer]. */
  public fun <T> encodeToString(serializer: SerializationStrategy<T>, value: List<T>): String =
    format.encodeToString(serializer, value)

  /** Encodes [sequence] to [sink] as UTF-8 GTFS CSV. */
  public inline fun <reified T> encodeToSink(sequence: Sequence<T>, sink: Sink) {
    format.encodeToSink(sequence, sink)
  }

  /** Encodes [sequence] to [sink] as UTF-8 GTFS CSV using [serializer]. */
  public fun <T> encodeToSink(
    serializer: SerializationStrategy<T>,
    sequence: Sequence<T>,
    sink: Sink,
  ) {
    format.encodeToSink(serializer, sequence, sink)
  }
}

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
