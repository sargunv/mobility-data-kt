package dev.sargunv.mobilitydata.mdb.v1

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Full Seal of Reliability breakdown for a GTFS feed. */
@Serializable
public data class FeedReliabilityReport(
  /** Catalog id of the GTFS feed. */
  @SerialName("feed_id") public val feedId: FeedId? = null,

  /** Whether the feed currently holds the seal. */
  @SerialName("has_seal") public val hasSeal: Boolean? = null,

  /**
   * Descriptive seal status. [hasSeal] is true only when this is [SealStatus.Granted].
   *
   * Added in catalog v1.16.13.
   */
  @SerialName("seal_status") public val sealStatus: SealStatus? = null,

  /** Instant the feed most recently earned the seal. */
  @SerialName("earned_at") public val earnedAt: IsoDateTime? = null,

  /** Instant the feed most recently lost the seal. */
  @SerialName("lost_at") public val lostAt: IsoDateTime? = null,

  /** Instant the criteria were last evaluated. */
  @SerialName("evaluated_at") public val evaluatedAt: IsoDateTime? = null,

  /** Whether at least one criterion is serving probation. */
  @SerialName("on_probation") public val onProbation: Boolean? = null,

  /** Earliest instant the feed could regain the seal. */
  @SerialName("probation_ends_at") public val probationEndsAt: IsoDateTime? = null,

  /** One entry per criterion, always all six. */
  public val criteria: List<ReliabilityCriterion>? = null,
)

/** One criterion's contribution to the Seal of Reliability. */
@Serializable
public data class ReliabilityCriterion(
  /** Which criterion this entry describes. */
  public val criterion: ReliabilityCriterionId? = null,

  /**
   * Debounced verdict that contributes to the seal, with the grace period already applied.
   *
   * As of catalog v1.16.13, a check still inside its grace period reads
   * [ReliabilityCriterionStatus.Pass] with [inGracePeriod] true (the at-risk state).
   * [ReliabilityCriterionStatus.Fail] means the failure is confirmed and withholds the seal.
   */
  public val status: ReliabilityCriterionStatus? = null,

  /**
   * Whether the daily check is currently failing but still inside its grace period.
   *
   * Can only be true while [status] is [ReliabilityCriterionStatus.Pass].
   */
  @SerialName("in_grace_period") public val inGracePeriod: Boolean? = null,

  /** Instant the grace period expires. */
  @SerialName("grace_period_ends_at") public val gracePeriodEndsAt: IsoDateTime? = null,

  /** Whether this criterion is serving probation. */
  @SerialName("on_probation") public val onProbation: Boolean? = null,

  /** Instant this criterion finishes probation. */
  @SerialName("probation_ends_at") public val probationEndsAt: IsoDateTime? = null,

  /** Instant this criterion was last evaluated. */
  @SerialName("evaluated_at") public val evaluatedAt: IsoDateTime? = null,

  /** Start of the current run of failing checks. */
  @SerialName("first_failure_at") public val firstFailureAt: IsoDateTime? = null,

  /** Most recent failing check. */
  @SerialName("last_failure_at") public val lastFailureAt: IsoDateTime? = null,
)

/** Seal of Reliability criterion name. */
@Serializable
@JvmInline
public value class ReliabilityCriterionId(
  /** Wire value. */
  public val value: String,
) {
  /** Named constants for [ReliabilityCriterionId]. */
  public companion object {
    /** The feed is provided by the agency or a trusted source. */
    public val Official: ReliabilityCriterionId = ReliabilityCriterionId("official")

    /** The feed has a stable producer URL and a long enough track record. */
    public val Stable: ReliabilityCriterionId = ReliabilityCriterionId("stable")

    /** The feed URL responds to scheduled availability checks. */
    public val Available: ReliabilityCriterionId = ReliabilityCriterionId("available")

    /** The latest dataset validates with no errors. */
    public val Compliant: ReliabilityCriterionId = ReliabilityCriterionId("compliant")

    /** The latest dataset's service period extends far enough ahead. */
    public val FreshCoverage: ReliabilityCriterionId = ReliabilityCriterionId("fresh_coverage")

    /** Successive datasets cover service without gaps. */
    public val FreshContinuous: ReliabilityCriterionId = ReliabilityCriterionId("fresh_continuous")
  }
}

/** Verdict for one reliability criterion. */
@Serializable
@JvmInline
public value class ReliabilityCriterionStatus(
  /** Wire value. */
  public val value: String,
) {
  /** Named constants for [ReliabilityCriterionStatus]. */
  public companion object {
    /** The criterion is not counting against the seal. */
    public val Pass: ReliabilityCriterionStatus = ReliabilityCriterionStatus("pass")

    /** The failure is confirmed and the criterion is withholding the seal. */
    public val Fail: ReliabilityCriterionStatus = ReliabilityCriterionStatus("fail")

    /**
     * Not produced by a missing-input run; the last verdict is left untouched. Listed because the
     * underlying column can hold it.
     */
    public val Unknown: ReliabilityCriterionStatus = ReliabilityCriterionStatus("unknown")

    /** The criterion does not apply to this feed. */
    public val NotApplicable: ReliabilityCriterionStatus =
      ReliabilityCriterionStatus("not_applicable")

    /** The criterion has produced no verdict yet. */
    public val NeverEvaluated: ReliabilityCriterionStatus =
      ReliabilityCriterionStatus("never_evaluated")
  }
}

/** Descriptive status of a feed's Seal of Reliability. */
@Serializable
@JvmInline
public value class SealStatus(
  /** Wire value. */
  public val value: String,
) {
  /** Named constants for [SealStatus]. */
  public companion object {
    /** The feed currently holds the seal. [FeedReliabilityReport.hasSeal] is true only then. */
    public val Granted: SealStatus = SealStatus("granted")

    /** At least one criterion is failing. */
    public val NotGranted: SealStatus = SealStatus("not_granted")

    /** None is failing, but not every criterion has been evaluated yet. */
    public val Unknown: SealStatus = SealStatus("unknown")

    /** None of the criteria has been evaluated yet. */
    public val NeverEvaluated: SealStatus = SealStatus("never_evaluated")
  }
}
