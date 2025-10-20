package dev.sargunv.mobilitydata.utils.serialization

import dev.sargunv.mobilitydata.utils.LanguageCode
import kotlinx.serialization.Serializable

public object LocalizedTextSerializer :
  MapAsListSerializer<LocalizedTextSerializer.LocalizedTextEntry, LanguageCode, String>(
    LocalizedTextEntry.serializer()
  ) {

  override fun Map.Entry<LanguageCode, String>.toDelegate(): LocalizedTextEntry =
    LocalizedTextEntry(text = this.value, language = this.key)

  @Serializable
  public data class LocalizedTextEntry(val text: String, val language: LanguageCode) :
    Map.Entry<LanguageCode, String> {
    override val key: LanguageCode
      get() = language

    override val value: String
      get() = text
  }
}
