package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.Id
import dev.sargunv.mobilitydata.utils.RgbColorTriplet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ServiceBrands(
  @SerialName("service_brands") public val serviceBrands: List<Brand>
) : GofsFeedData, List<Brand> by serviceBrands

@Serializable
public data class Brand(
  @SerialName("brand_id") public val brandId: Id<Brand>,
  @SerialName("brand_name") public val brandName: String,
  @SerialName("brand_color") public val brandColor: RgbColorTriplet,
  @SerialName("brand_text_color") public val brandTextColor: RgbColorTriplet,
)
