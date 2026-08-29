package dev.sargunv.mobilitydata.utils

/**
 * Marks APIs that model a specification that is not the Current Version.
 *
 * Apply this to types and members that track a prerelease or release-candidate spec. When that spec
 * is promoted, remove the annotation from surviving declarations. Deprecate this marker and retain
 * it until the next major version so callers that already opted in keep compiling.
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
