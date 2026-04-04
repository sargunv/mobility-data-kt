package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.LanguageCode
import kotlinx.serialization.Serializable

/**
 * Serializer for localized text that converts between Map<LanguageCode, String> and a list format.
 *
 * This serializer handles the conversion of localized text entries, allowing them to be serialized
 * as an array of objects with text and language properties rather than as a map.
 */
public object LocalizedTextSerializer :
  MapAsListSerializer<LocalizedTextSerializer.LocalizedTextEntry, LanguageCode, String>(
    LocalizedTextEntry.serializer()
  ) {

  override fun Map.Entry<LanguageCode, String>.toDelegate(): LocalizedTextEntry =
    LocalizedTextEntry(text = this.value, language = this.key)

  /**
   * Represents a single localized text entry with text and language code.
   *
   * This class is used as the serialization format for individual entries in a localized text map.
   */
  @Serializable
  public data class LocalizedTextEntry(
    /** The translated text content. */
    val text: String,
    /** The IETF BCP 47 language code for this translation. */
    val language: LanguageCode,
  ) : Map.Entry<LanguageCode, String> {
    override val key: LanguageCode
      get() = language

    override val value: String
      get() = text
  }
}
