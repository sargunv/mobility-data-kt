package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.gofs.v1.serialization.FeedDiscoverySerializer
import dev.sargunv.mobilitydata.utils.LanguageCode
import dev.sargunv.mobilitydata.utils.Url
import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
public value class GofsManifest(
  @SerialName("feeds") private val services: Map<LanguageCode, Service>
) : GofsFeedData, Map<LanguageCode, Service> by services {
  public constructor(vararg entries: Pair<LanguageCode, Service>) : this(mapOf(*entries))

  public fun getService(language: LanguageCode): Service = services.getValue(language)

  public fun getServiceOrNull(language: LanguageCode): Service? = services[language]
}

@Serializable
public data class Service(
  @Serializable(with = FeedDiscoverySerializer::class) val feeds: Map<FeedType, Url>
) : Map<FeedType, Url> by feeds {
  public constructor(vararg entries: Pair<FeedType, Url>) : this(mapOf(*entries))
}

@Serializable
public enum class FeedType {
  @SerialName("gofs") GofsManifest,
  @SerialName("gofs_versions") GofsVersions,
  @SerialName("system_information") SystemInformation,
  @SerialName("service_brands") ServiceBrands,
  @SerialName("vehicle_types") VehicleTypes,
  @SerialName("zones") Zones,
  @SerialName("operating_rules") OperatingRules,
  @SerialName("calendars") Calendars,
  @SerialName("fares") Fares,
  @SerialName("wait_time") WaitTimes,
  @SerialName("booking_rules") BookingRules,
  @SerialName("realtime_booking") RealtimeBookings,
}
