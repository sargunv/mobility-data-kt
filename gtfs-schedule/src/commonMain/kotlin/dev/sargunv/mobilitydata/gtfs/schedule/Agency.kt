package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.datetime.TimeZone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Transit agencies with service represented in this dataset.
 *
 * This class represents a record in the agency.txt file.
 */
@Serializable
public data class Agency(
  /** Uniquely identifies a transit agency. */
  @SerialName("agency_id") public val agencyId: String? = null,

  /** Full name of the transit agency. */
  @SerialName("agency_name") public val agencyName: String,

  /** URL of the transit agency. */
  @SerialName("agency_url") public val agencyUrl: Url,

  /** Timezone where the transit agency is located. */
  @SerialName("agency_timezone") public val agencyTimezone: TimeZone,

  /** Primary language used by this transit agency. */
  @SerialName("agency_lang") public val agencyLang: LanguageCode? = null,

  /** Voice telephone number for the specified agency. */
  @SerialName("agency_phone") public val agencyPhone: String? = null,

  /** URL of a web page that allows a rider to purchase tickets or other fare instruments. */
  @SerialName("agency_fare_url") public val agencyFareUrl: Url? = null,

  /** Email address for customer service at the agency. */
  @SerialName("agency_email") public val agencyEmail: String? = null,

  /**
   * Indicates if riders can access a transit service associated with this agency by using a
   * contactless EMV card or mobile device as fare media at a fare validator.
   */
  @SerialName("cemv_support") public val cemvSupport: TriState? = null,
)
