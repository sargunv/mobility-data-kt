package dev.sargunv.mobilitydata.utils

/**
 * Marks APIs that model a specification that is not the Current Version.
 *
 * Apply this to types and members that track a prerelease or release-candidate spec.
 */
@RequiresOptIn(
  message =
    "This API models a prerelease or release-candidate specification and may change without a major version bump.",
  level = RequiresOptIn.Level.ERROR,
)
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.CONSTRUCTOR,
  AnnotationTarget.PROPERTY,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.FUNCTION,
)
public annotation class ExperimentalMobilityDataApi
