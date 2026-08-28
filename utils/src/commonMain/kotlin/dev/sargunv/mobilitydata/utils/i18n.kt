package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.LocalizedTextSerializer
import kotlinx.serialization.Serializable

/** Currency code following the [ISO 4217 standard](https://en.wikipedia.org/wiki/ISO_4217). */
public typealias CurrencyCode = String

/**
 * ISO 4217 minor-unit (fraction) digits for this currency code.
 *
 * Returns `null` if the code is unknown or has no usable minor unit, such as precious-metal and
 * some fund codes. Values come from SIX Group's ISO 4217 List One, published 2026-01-01.
 */
public val CurrencyCode.defaultFractionDigits: Int?
  get() = Iso4217MinorUnits[this]

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
