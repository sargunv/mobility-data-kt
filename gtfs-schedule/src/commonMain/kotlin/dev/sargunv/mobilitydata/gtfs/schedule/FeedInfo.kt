package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.mobilitydata.utils.BasicLocalDate
import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Dataset metadata.
 *
 * This class represents a record in the feed_info.txt file.
 */
@Serializable
public data class FeedInfo(
  /** Full name of the organization that publishes the dataset. */
  @SerialName("feed_publisher_name") public val feedPublisherName: String,

  /** URL of the dataset publishing organization's website. */
  @SerialName("feed_publisher_url") public val feedPublisherUrl: Url,

  /** Default language used for the text in this dataset. */
  @SerialName("feed_lang") public val feedLang: LanguageCode,

  /**
   * Defines the language that should be used when the data consumer doesn't know the language of
   * the rider.
   */
  @SerialName("default_lang") public val defaultLang: LanguageCode? = null,

  /**
   * The dataset provides complete and reliable schedule information for service in the period from
   * the beginning of the feed_start_date day to the end of the feed_end_date day.
   */
  @SerialName("feed_start_date") public val feedStartDate: BasicLocalDate? = null,

  /** The dataset end date (see feed_start_date). */
  @SerialName("feed_end_date") public val feedEndDate: BasicLocalDate? = null,

  /**
   * String that indicates the current version of their GTFS dataset. GTFS-consuming applications
   * can display this value to help dataset publishers determine whether the latest dataset has been
   * incorporated.
   */
  @SerialName("feed_version") public val feedVersion: String? = null,

  /**
   * Email address for communication regarding the GTFS dataset and data publishing practices.
   * feed_contact_email is a technical contact for GTFS-consuming applications.
   */
  @SerialName("feed_contact_email") public val feedContactEmail: String? = null,

  /**
   * URL for contact information, a web-form, support desk, or other tools for communication
   * regarding the GTFS dataset and data publishing practices. feed_contact_url is a technical
   * contact for GTFS-consuming applications.
   */
  @SerialName("feed_contact_url") public val feedContactUrl: Url? = null,
)
