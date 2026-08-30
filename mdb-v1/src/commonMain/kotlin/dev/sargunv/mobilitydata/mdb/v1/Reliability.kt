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

  /** Verdict at the last evaluation, with no grace period applied. */
  public val status: ReliabilityCriterionStatus? = null,

  /** Whether a failing check is still inside the grace period. */
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
  public val value: String
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
  public val value: String
) {
  /** Named constants for [ReliabilityCriterionStatus]. */
  public companion object {
    /** The check passed. */
    public val Pass: ReliabilityCriterionStatus = ReliabilityCriterionStatus("pass")

    /** The check failed. */
    public val Fail: ReliabilityCriterionStatus = ReliabilityCriterionStatus("fail")

    /** Inputs were missing, so no verdict was reached. */
    public val Unknown: ReliabilityCriterionStatus = ReliabilityCriterionStatus("unknown")

    /** The criterion does not apply to this feed. */
    public val NotApplicable: ReliabilityCriterionStatus =
      ReliabilityCriterionStatus("not_applicable")

    /** The criterion has produced no verdict yet. */
    public val NeverEvaluated: ReliabilityCriterionStatus =
      ReliabilityCriterionStatus("never_evaluated")
  }
}
