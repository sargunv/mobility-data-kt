package dev.sargunv.mobilitydata.utils.serialization

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.serializers.FormattedLocalDateSerializer
import kotlinx.serialization.encoding.Decoder

/**
 * Serializer for [LocalDate] using the ISO 8601 basic format (YYYYMMDD).
 *
 * Example: 20211109 for November 9th, 2021.
 */
public object IsoBasicLocalDateSerializer :
  FormattedLocalDateSerializer(
    name = "dev.sargunv.mobilitydata.utils.serialization.IsoBasicLocalDateSerializer",
    format = LocalDate.Formats.ISO_BASIC,
  ) {
  override fun deserialize(decoder: Decoder): LocalDate =
    parseIsoBasicDate(decoder.decodeString(), LocalDate.Formats.ISO_BASIC)
}

private const val BASIC_DATE_LENGTH = 8
private const val MONTH_START = 4
private const val DAY_START = 6

/**
 * Parses [value] as `YYYYMMDD`, reading the common shape (eight ASCII digits forming a valid date)
 * directly and leaving every other input to [format], so the result and any error are the ones
 * [format] gives.
 */
internal fun parseIsoBasicDate(value: String, format: DateTimeFormat<LocalDate>): LocalDate {
  if (value.length == BASIC_DATE_LENGTH && value.all { it in '0'..'9' }) {
    val date =
      try {
        LocalDate(
          year = parseDateDigits(value, 0, MONTH_START),
          month = parseDateDigits(value, MONTH_START, DAY_START),
          day = parseDateDigits(value, DAY_START, BASIC_DATE_LENGTH),
        )
      } catch (_: IllegalArgumentException) {
        null
      }
    if (date != null) return date
  }
  return format.parse(value)
}

private fun parseDateDigits(value: String, start: Int, end: Int): Int {
  var result = 0
  for (i in start until end) result = result * 10 + (value[i] - '0')
  return result
}
