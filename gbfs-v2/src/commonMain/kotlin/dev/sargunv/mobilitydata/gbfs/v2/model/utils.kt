package dev.sargunv.mobilitydata.gbfs.v2.model

import dev.sargunv.mobilitydata.gbfs.v2.serialization.EpochSecondsSerializer
import dev.sargunv.mobilitydata.gbfs.v2.serialization.WholeSecondsSerializer
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Standard GBFS feed response wrapper.
 *
 * All GBFS feeds are wrapped in this structure that provides metadata about the feed.
 */
@Serializable
@OptIn(ExperimentalTime::class)
public data class GbfsFeedResponse<T : GbfsFeedData>(
  /** POSIX timestamp indicating the last time the data in this feed was updated. */
  @Serializable(with = EpochSecondsSerializer::class)
  @SerialName("last_updated")
  public val lastUpdated: Instant,

  /**
   * Number of seconds representing how long before the data in this feed will be updated again.
   *
   * Represents the minimum amount of time the client should wait before polling the feed again.
   */
  @Serializable(with = WholeSecondsSerializer::class) public val ttl: Duration,

  /** GBFS version number to which the feed conforms, according to the versioning framework. */
  public val version: String,

  /** Response data in the form of the specific feed type being accessed. */
  public val data: T,
)

/** A possible value of [GbfsFeedResponse.data]. */
public sealed interface GbfsFeedData

/** Currency code following the [ISO 4217 standard](https://en.wikipedia.org/wiki/ISO_4217). */
public typealias CurrencyCode = String

/**
 * Country code following the
 * [ISO 3166-1 alpha-2 notation](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2).
 */
public typealias CountryCode = String

/** An [IETF BCP 47 language code](https://en.wikipedia.org/wiki/IETF_language_tag). */
public typealias LanguageCode = String

/**
 * A fully qualified URI that includes the scheme (for example, `com.example.android://`). Any
 * special characters in the URI MUST be correctly escaped.
 */
public typealias Uri = String

/**
 * A fully qualified URL that includes `http://` or `https://`. Any special characters in the URL
 * MUST be correctly escaped.
 */
public typealias Url = String

/**
 * A string that identifies that particular entity of type [T]. An ID:
 * - MUST be unique within like fields (for example, `Id<Station>` MUST be unique among stations).
 * - Does not have to be globally unique, unless otherwise specified.
 * - MUST NOT contain spaces.
 * - MUST be persistent for a given entity ([Station], [PricingPlan], etc).
 * - An exception is floating bike [Bike.bikeId], which MUST NOT be persistent for privacy reasons
 */
public typealias Id<@Suppress("unused") T> = String
