package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.LanguageCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Provides translations for customer-facing dataset values in languages other than the one used in
 * the dataset.
 *
 * This class represents a record in the translations.txt file.
 */
@Serializable
public data class Translation(
  /** Defines the table that contains the field to be translated. */
  @SerialName("table_name") public val tableName: String,

  /** Name of the field to be translated. */
  @SerialName("field_name") public val fieldName: String,

  /** Language of translation. */
  @SerialName("language") public val language: LanguageCode,

  /** Translated value. */
  @SerialName("translation") public val translation: String,

  /**
   * Defines the record that corresponds to the field to be translated. The value in record_id must
   * be the first or only field of the table's primary key.
   */
  @SerialName("record_id") public val recordId: String? = null,

  /**
   * Helps the record that contains the field to be translated when the table doesn't have a unique
   * ID. The value in record_sub_id is the second field of the table's primary key.
   */
  @SerialName("record_sub_id") public val recordSubId: String? = null,

  /**
   * Instead of defining which record should be translated, this field can be used to define the
   * value which should be translated. When used, the translation will be applied when the field
   * identified by field_name contains the exact same value defined in field_value.
   */
  @SerialName("field_value") public val fieldValue: String? = null,
)
