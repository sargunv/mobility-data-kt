@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("UndocumentedPublicProperty")

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/** A service alert in the realtime feed. */
@Serializable
public data class Alert(
  @ProtoNumber(1) public val activePeriod: List<TimeRange> = emptyList(),
  @ProtoNumber(5) public val informedEntity: List<EntitySelector> = emptyList(),
  @ProtoNumber(6) public val cause: Cause = Cause.UnknownCause,
  @ProtoNumber(7) public val effect: Effect = Effect.UnknownEffect,
  @ProtoNumber(8) public val url: TranslatedString? = null,
  @ProtoNumber(10) public val headerText: TranslatedString? = null,
  @ProtoNumber(11) public val descriptionText: TranslatedString? = null,
  @ProtoNumber(12) public val ttsHeaderText: TranslatedString? = null,
  @ProtoNumber(13) public val ttsDescriptionText: TranslatedString? = null,
  @ProtoNumber(14) public val severityLevel: SeverityLevel = SeverityLevel.UnknownSeverity,
  @ProtoNumber(15) public val image: TranslatedImage? = null,
  @ProtoNumber(16) public val imageAlternativeText: TranslatedString? = null,
  @ProtoNumber(17) public val causeDetail: TranslatedString? = null,
  @ProtoNumber(18) public val effectDetail: TranslatedString? = null,
) {
  /** Root cause for the alert. */
  @Serializable
  public enum class Cause {
    @ProtoNumber(1) UnknownCause,
    @ProtoNumber(2) OtherCause,
    @ProtoNumber(3) TechnicalProblem,
    @ProtoNumber(4) Strike,
    @ProtoNumber(5) Demonstration,
    @ProtoNumber(6) Accident,
    @ProtoNumber(7) Holiday,
    @ProtoNumber(8) Weather,
    @ProtoNumber(9) Maintenance,
    @ProtoNumber(10) Construction,
    @ProtoNumber(11) PoliceActivity,
    @ProtoNumber(12) MedicalEmergency,
  }

  /** Rider-visible effect of the alert. */
  @Serializable
  public enum class Effect {
    @ProtoNumber(1) NoService,
    @ProtoNumber(2) ReducedService,
    @ProtoNumber(3) SignificantDelays,
    @ProtoNumber(4) Detour,
    @ProtoNumber(5) AdditionalService,
    @ProtoNumber(6) ModifiedService,
    @ProtoNumber(7) OtherEffect,
    @ProtoNumber(8) UnknownEffect,
    @ProtoNumber(9) StopMoved,
    @ProtoNumber(10) NoEffect,
    @ProtoNumber(11) AccessibilityIssue,
  }

  /** Severity level for the alert. */
  @Serializable
  public enum class SeverityLevel {
    @ProtoNumber(1) UnknownSeverity,
    @ProtoNumber(2) Info,
    @ProtoNumber(3) Warning,
    @ProtoNumber(4) Severe,
  }
}

/** A time interval for alert display. */
@Serializable
public data class TimeRange(
  @ProtoNumber(1) public val start: Long? = null,
  @ProtoNumber(2) public val end: Long? = null,
)

/** Selects an affected GTFS entity. */
@Serializable
public data class EntitySelector(
  @ProtoNumber(1) public val agencyId: String? = null,
  @ProtoNumber(2) public val routeId: String? = null,
  @ProtoNumber(3) public val routeType: Int? = null,
  @ProtoNumber(4) public val trip: TripDescriptor? = null,
  @ProtoNumber(5) public val stopId: String? = null,
  @ProtoNumber(6) public val directionId: Int? = null,
)

/** Internationalized text content. */
@Serializable
public data class TranslatedString(
  @ProtoNumber(1) public val translation: List<Translation> = emptyList()
) {
  /** A localized text variant. */
  @Serializable
  public data class Translation(
    @ProtoNumber(1) public val text: String,
    @ProtoNumber(2) public val language: String? = null,
  )
}

/** Internationalized image metadata. */
@Serializable
public data class TranslatedImage(
  @ProtoNumber(1) public val localizedImage: List<LocalizedImage> = emptyList()
) {
  /** A localized image variant. */
  @Serializable
  public data class LocalizedImage(
    @ProtoNumber(1) public val url: String,
    @ProtoNumber(2) public val mediaType: String,
    @ProtoNumber(3) public val language: String? = null,
  )
}
