package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.datetime.TimeZone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Transit agencies with service represented in this dataset.
 *
 * See [GTFS Reference](https://gtfs.org/documentation/schedule/reference/#agencytxt)
 */
@Serializable
public data class Agency(
  /** Uniquely identifies a transit agency. */
  @SerialName("agency_id") public val agencyId: Id<Agency>? = null,

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
)
