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
 * Codec for GTFS `.txt` files.
 *
 * Feeds often omit trailing optional fields or ship a BOM. Decode accepts both. Extra cells past
 * the header are dropped.
 */
public object GtfsCsv {
  /** Serialization format for GTFS `.txt` files. */
  public val format: DsvFormat =
    DsvFormat(
      scheme = Csv.scheme.copy(skipEmptyLines = true, allowJaggedRows = true),
      ignoreUnknownKeys = true,
      writeEnumsByName = false,
      treatMissingColumnsAsNull = true,
    )

  /** Decodes records of [T] from [string]. */
  public inline fun <reified T> decodeFromString(string: String): List<T> =
    decodeFromString(serializer(), string)

  /** Decodes records from [string] using [deserializer]. */
  public fun <T> decodeFromString(
    deserializer: DeserializationStrategy<T>,
    string: String,
  ): List<T> = format.decodeFromString(deserializer, string)

  /** Lazily decodes records of [T] from [source]. */
  public inline fun <reified T> decodeFromSource(source: Source): Sequence<T> =
    decodeFromSource(source, serializer())

  /** Lazily decodes records from [source] using [deserializer]. */
  public fun <T> decodeFromSource(
    source: Source,
    deserializer: DeserializationStrategy<T>,
  ): Sequence<T> = format.decodeFromSource(source, deserializer)

  /** Encodes [value] to a `.txt` string. */
  public inline fun <reified T> encodeToString(value: List<T>): String =
    format.encodeToString(value)

  /** Encodes [value] to a `.txt` string using [serializer]. */
  public fun <T> encodeToString(serializer: SerializationStrategy<T>, value: List<T>): String =
    format.encodeToString(serializer, value)

  /** Encodes [sequence] to [sink]. */
  public inline fun <reified T> encodeToSink(sequence: Sequence<T>, sink: Sink) {
    format.encodeToSink(sequence, sink)
  }

  /** Encodes [sequence] to [sink] using [serializer]. */
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
