package dev.sargunv.mobilitydata.utils.serialization

private const val RGB_HEX_DIGITS = 6
private const val HEX_RADIX = 16
private const val HEX_LETTER_OFFSET = 10

/**
 * Reads [value] from [start] to its end as exactly six ASCII hexadecimal digits (either case), the
 * same strings `[0-9a-fA-F]{6}` matches, without compiling a regex or allocating.
 *
 * @return the parsed number, or `-1` if the rest of [value] is not six hexadecimal digits.
 */
internal fun parseRgbHex(value: String, start: Int): Int {
  if (value.length - start != RGB_HEX_DIGITS) return -1
  var result = 0
  for (i in start until value.length) {
    val digit =
      when (val c = value[i]) {
        in '0'..'9' -> c - '0'
        in 'a'..'f' -> c - 'a' + HEX_LETTER_OFFSET
        in 'A'..'F' -> c - 'A' + HEX_LETTER_OFFSET
        else -> return -1
      }
    result = result * HEX_RADIX + digit
  }
  return result
}
