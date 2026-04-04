package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.LocalizedTextSerializer
import kotlinx.serialization.Serializable

/** Currency code following the [ISO 4217 standard](https://en.wikipedia.org/wiki/ISO_4217). */
public typealias CurrencyCode = String

/**
 * Country code following the
 * [ISO 3166-1 alpha-2 notation](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2).
 */
public typealias CountryCode = String

/** An [IETF BCP 47 language code](https://en.wikipedia.org/wiki/IETF_language_tag). */
public typealias LanguageCode = String

/** Text localized to multiple languages. */
public typealias LocalizedText =
  @Serializable(with = LocalizedTextSerializer::class) Map<LanguageCode, String>

/** [Url] localized to multiple languages. */
public typealias LocalizedUrl =
  @Serializable(with = LocalizedTextSerializer::class) Map<LanguageCode, Url>
