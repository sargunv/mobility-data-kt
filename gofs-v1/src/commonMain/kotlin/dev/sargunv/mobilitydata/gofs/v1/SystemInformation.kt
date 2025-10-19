package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.BasicLocalDate
import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.datetime.TimeZone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SystemInformation(
  public val language: LanguageCode,
  public val timezone: TimeZone,
  public val name: String,
  @SerialName("short_name") public val shortName: String? = null,
  public val operator: String? = null,
  public val url: Url? = null,
  @SerialName("subscribe_url") public val subscribeUrl: Url? = null,
  @SerialName("start_date") public val startDate: BasicLocalDate? = null,
  @SerialName("phone_number") public val phoneNumber: String? = null,
  public val email: String? = null,
  @SerialName("feed_contact_email") public val feedContactEmail: String? = null,
) : GofsFeedData
