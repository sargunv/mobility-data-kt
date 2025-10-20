package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.RgbColorTriplet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines the on-demand service brands available to the riders.
 *
 * One feed may contain multiple service brands with different features and amenities (e.g. A
 * ridehail service system may offer the 'Regular Ride', 'Large Ride' and 'Shared Ride' services).
 */
@Serializable
public data class ServiceBrands(
  /** Array that contains one object per service brand. */
  @SerialName("service_brands") public val serviceBrands: List<Brand>
) : GofsFeedData, List<Brand> by serviceBrands

/** A service brand with its visual identity. */
@Serializable
public data class Brand(
  /**
   * Unique identifier of the service brand. This value should remain the same over the availability
   * of the service.
   */
  @SerialName("brand_id") public val brandId: Id<Brand>,
  /** Name of the service brand to be displayed to the riders. */
  @SerialName("brand_name") public val brandName: String,
  /** Color identifying the service brand to be displayed to the riders. */
  @SerialName("brand_color") public val brandColor: RgbColorTriplet? = null,
  /**
   * Color used for displaying text over the brand_color. For visual-accessibility reasons, the
   * brand_text_color must highly contrast with the brand_color (e.g. a dark brand_color should be
   * paired with a white brand_text_color; a light brand_color should be paired with a black
   * brand_text_color).
   */
  @SerialName("brand_text_color") public val brandTextColor: RgbColorTriplet? = null,
)
