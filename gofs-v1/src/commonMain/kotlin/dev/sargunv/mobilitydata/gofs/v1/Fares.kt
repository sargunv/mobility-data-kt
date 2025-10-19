package dev.sargunv.mobilitydata.gofs.v1

import dev.sargunv.mobilitydata.utils.CurrencyCode
import dev.sargunv.mobilitydata.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Fares(public val fares: List<Fare>) : GofsFeedData, List<Fare> by fares

@Serializable
public data class Fare(
  @SerialName("fare_id") public val fareId: Id<Fare>,
  public val currency: CurrencyCode? = null,
  public val kilometer: List<FareEntry>? = null,
  public val minute: List<FareEntry>? = null,
  @SerialName("active_minute") public val activeMinute: List<FareEntry>? = null,
  @SerialName("idle_minute") public val idleMinute: List<FareEntry>? = null,
  public val rider: List<FareEntry>? = null,
  public val luggage: List<FareEntry>? = null,
)

@Serializable
public data class FareEntry(
  public val interval: Double? = null,
  public val start: Int? = null,
  public val end: Int? = null,
  public val amount: Double? = null,
)
