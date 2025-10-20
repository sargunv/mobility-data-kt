package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.BasicLocalDate
import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.datetime.TimeZone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines the attributes of the on-demand service system.
 *
 * This file defines the attributes such as operator, location, year implemented, URL, contact info,
 * timezone, etc.
 */
@Serializable
public data class SystemInformation(
  /** Language used throughout the rest of the files. */
  public val language: LanguageCode,
  /** Timezone where the on-demand service system is located. */
  public val timezone: TimeZone,
  /** Name of the on-demand service system to be displayed to the riders. */
  public val name: String,
  /** Abbreviation commonly used to name the on-demand service system. */
  @SerialName("short_name") public val shortName: String? = null,
  /**
   * Name of the on-demand service operator. The operator name may be the same as the system name.
   */
  public val operator: String? = null,
  /** URL of the on-demand service system. */
  public val url: Url? = null,
  /** URL where riders can subscribe to the on-demand services. */
  @SerialName("subscribe_url") public val subscribeUrl: Url? = null,
  /** Date that the system began operations. */
  @SerialName("start_date") public val startDate: BasicLocalDate? = null,
  /** Voice telephone number for the specified system's customer service department. */
  @SerialName("phone_number") public val phoneNumber: String? = null,
  /**
   * Contact email address actively monitored by the operator's customer service department. This
   * email address should be a direct contact point where riders can reach a customer service
   * representative.
   */
  public val email: String? = null,
  /** Contact email for feed consumers to report technical issues with the feed. */
  @SerialName("feed_contact_email") public val feedContactEmail: String? = null,
) : GofsFeedData
