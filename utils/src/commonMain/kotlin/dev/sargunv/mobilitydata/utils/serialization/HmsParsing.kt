package dev.sargunv.mobilitydata.utils.serialization

private const val MAX_HOUR_DIGITS = 9
private const val MAX_MINUTE_SECOND_DIGITS = 2

/**
 * Parses an `HH:MM:SS` time.
 *
 * The common shape (one to nine ASCII digits of hours, then one or two ASCII digits each of minutes
 * and seconds) is read in place without allocating. Any other input goes through the general
 * split-based path. Values that do not have three integer parts throw [IllegalArgumentException]
 * with a format message; range checks stay on [create].
 *
 * @param typeName the type named in the error message for a malformed value.
 */
internal inline fun <T> parseHms(
  value: String,
  typeName: String,
  create: (hours: Int, minutes: Int, seconds: Int) -> T,
): T {
  val hoursEnd = digitRunEnd(value, 0, MAX_HOUR_DIGITS)
  val minutesEnd = digitRunEnd(value, hoursEnd + 1, MAX_MINUTE_SECOND_DIGITS)
  val secondsEnd = digitRunEnd(value, minutesEnd + 1, MAX_MINUTE_SECOND_DIGITS)
  val isFastShape =
    hoursEnd > 0 &&
      value.getOrNull(hoursEnd) == ':' &&
      minutesEnd > hoursEnd + 1 &&
      value.getOrNull(minutesEnd) == ':' &&
      secondsEnd > minutesEnd + 1 &&
      secondsEnd == value.length
  if (isFastShape) {
    return create(
      parseDigits(value, 0, hoursEnd),
      parseDigits(value, hoursEnd + 1, minutesEnd),
      parseDigits(value, minutesEnd + 1, secondsEnd),
    )
  }

  val parts = value.split(":")
  val hours = parts.getOrNull(0)?.toIntOrNull()
  val minutes = parts.getOrNull(1)?.toIntOrNull()
  val seconds = parts.getOrNull(2)?.toIntOrNull()
  require(parts.size == 3 && hours != null && minutes != null && seconds != null) {
    "$typeName must be in the format HH:MM:SS, but was $value."
  }
  return create(hours, minutes, seconds)
}

/** Returns the end of the run of at most [maxDigits] ASCII digits starting at [start]. */
internal fun digitRunEnd(value: String, start: Int, maxDigits: Int): Int {
  var pos = start
  while (pos < value.length && pos - start < maxDigits && value[pos] in '0'..'9') pos++
  return pos
}

/** Converts the ASCII digits of [value] in `[start, end)` to an [Int]. */
internal fun parseDigits(value: String, start: Int, end: Int): Int {
  var result = 0
  for (i in start until end) result = result * 10 + (value[i] - '0')
  return result
}
