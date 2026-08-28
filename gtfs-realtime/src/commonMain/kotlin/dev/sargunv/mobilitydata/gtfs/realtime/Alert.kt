@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/** A service alert in the realtime feed. */
@Serializable
public data class Alert(
  /** Time ranges when riders should see this alert. */
  @ProtoNumber(1) public val activePeriod: List<TimeRange> = emptyList(),
  /** GTFS entities affected by this alert. */
  @ProtoNumber(5) public val informedEntity: List<EntitySelector> = emptyList(),
  /** Root cause of the service disruption. */
  @ProtoNumber(6)
  @Serializable(with = CauseSerializer::class)
  public val cause: Cause = Cause.UnknownCause,
  /** Rider-visible effect on service. */
  @ProtoNumber(7)
  @Serializable(with = EffectSerializer::class)
  public val effect: Effect = Effect.UnknownEffect,
  /** URL with additional alert information. */
  @ProtoNumber(8) public val url: TranslatedString? = null,
  /** Short summary of the alert. */
  @ProtoNumber(10) public val headerText: TranslatedString? = null,
  /** Full description of the alert. */
  @ProtoNumber(11) public val descriptionText: TranslatedString? = null,
  /** Text-to-speech version of [headerText]. */
  @ProtoNumber(12) public val ttsHeaderText: TranslatedString? = null,
  /** Text-to-speech version of [descriptionText]. */
  @ProtoNumber(13) public val ttsDescriptionText: TranslatedString? = null,
  /** Severity of the alert. */
  @ProtoNumber(14)
  @Serializable(with = SeverityLevelSerializer::class)
  public val severityLevel: SeverityLevel = SeverityLevel.UnknownSeverity,
  /** Image to display alongside the alert. */
  @ProtoNumber(15) public val image: TranslatedImage? = null,
  /** Accessibility alt text for [image]. */
  @ProtoNumber(16) public val imageAlternativeText: TranslatedString? = null,
  /** Human-readable description of the [cause]. */
  @ProtoNumber(17) public val causeDetail: TranslatedString? = null,
  /** Human-readable description of the [effect]. */
  @ProtoNumber(18) public val effectDetail: TranslatedString? = null,
) {
  /** Root cause for the alert. */
  @Suppress("UndocumentedPublicProperty")
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
  @Suppress("UndocumentedPublicProperty")
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
  @Suppress("UndocumentedPublicProperty")
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
  /** Start of the interval as a POSIX timestamp, inclusive. */
  @ProtoNumber(1) public val start: Long? = null,
  /** End of the interval as a POSIX timestamp, exclusive. */
  @ProtoNumber(2) public val end: Long? = null,
)

/** Selects an affected GTFS entity. */
@Serializable
public data class EntitySelector(
  /** Affected GTFS `agency_id`. */
  @ProtoNumber(1) public val agencyId: String? = null,
  /** Affected GTFS `route_id`. */
  @ProtoNumber(2) public val routeId: String? = null,
  /** Affected GTFS `route_type`. */
  @ProtoNumber(3) public val routeType: Int? = null,
  /** Affected trip instance. */
  @ProtoNumber(4) public val trip: TripDescriptor? = null,
  /** Affected GTFS `stop_id`. */
  @ProtoNumber(5) public val stopId: String? = null,
  /** Affected GTFS `direction_id`. */
  @ProtoNumber(6) public val directionId: Int? = null,
)

/** Internationalized text content. */
@Serializable
public data class TranslatedString(
  /** Localized text variants; at least one is required. */
  @ProtoNumber(1) public val translation: List<Translation> = emptyList()
) {
  /** A localized text variant. */
  @Serializable
  public data class Translation(
    /** UTF-8 text string. */
    @ProtoNumber(1) public val text: String,
    /** BCP-47 language code, or null for the default translation. */
    @ProtoNumber(2) public val language: String? = null,
  )
}

/** Internationalized image metadata. */
@Serializable
public data class TranslatedImage(
  /** Localized image variants; at least one is required. */
  @ProtoNumber(1) public val localizedImage: List<LocalizedImage> = emptyList()
) {
  /** A localized image variant. */
  @Serializable
  public data class LocalizedImage(
    /** URL linking to the image. */
    @ProtoNumber(1) public val url: String,
    /** IANA media type of the image (e.g. `image/png`). */
    @ProtoNumber(2) public val mediaType: String,
    /** BCP-47 language code, or null for the default image. */
    @ProtoNumber(3) public val language: String? = null,
  )
}
