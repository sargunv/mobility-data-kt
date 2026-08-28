package dev.sargunv.mobilitydata.utils

/**
 * Rounding behavior for [Decimal] operations that cannot be represented exactly at the requested
 * precision.
 *
 * These modes follow conventional decimal rounding semantics.
 */
public enum class RoundingMode {
  /** Round away from zero. */
  UP,

  /** Round toward zero. */
  DOWN,

  /** Round toward positive infinity. */
  CEILING,

  /** Round toward negative infinity. */
  FLOOR,

  /** Round to nearest; ties are resolved away from zero. */
  HALF_UP,

  /** Round to nearest; ties are resolved toward zero. */
  HALF_DOWN,

  /** Round to nearest; ties are resolved toward the even retained digit. */
  HALF_EVEN,

  /** Throw [ArithmeticException] if rounding would be required. */
  UNNECESSARY,
}
