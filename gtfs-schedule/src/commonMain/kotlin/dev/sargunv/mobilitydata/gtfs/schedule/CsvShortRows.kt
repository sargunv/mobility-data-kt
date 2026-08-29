package dev.sargunv.mobilitydata.gtfs.schedule

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString

/**
 * Pads rows that omit trailing empty cells so they match the header field count.
 *
 * kotlin-dsv rejects unequal column counts (RFC 4180). Many GTFS producers drop trailing commas for
 * empty optional fields; this keeps present cells aligned and treats omitted trailing cells as
 * empty. Empty lines are left untouched so skipEmptyLines still applies. Rows longer than the
 * header are not trimmed.
 */
internal fun padOmittedTrailingCsvFields(
  input: String,
  delimiter: Char = ',',
  quote: Char = '"',
): String {
  if (input.isEmpty()) return input

  val out = StringBuilder(input.length)
  var index = 0
  var inQuotes = false
  var headerFields = -1
  var rowFields = 1
  var rowHasContent = false

  fun isEmptyRow(): Boolean = !rowHasContent && rowFields == 1

  fun padCurrentRow() {
    if (headerFields < 0) {
      headerFields = rowFields
    } else if (!isEmptyRow() && rowFields < headerFields) {
      repeat(headerFields - rowFields) { out.append(delimiter) }
    }
  }

  fun resetRow() {
    rowFields = 1
    rowHasContent = false
  }

  while (index < input.length) {
    val c = input[index]
    when {
      inQuotes && c == quote -> {
        if (index + 1 < input.length && input[index + 1] == quote) {
          out.append(quote).append(quote)
          index += 2
          rowHasContent = true
          continue
        }
        inQuotes = false
        out.append(c)
      }
      !inQuotes && c == quote -> {
        inQuotes = true
        out.append(c)
        rowHasContent = true
      }
      !inQuotes && c == delimiter -> {
        out.append(c)
        rowFields++
      }
      !inQuotes && (c == '\n' || c == '\r') -> {
        val newline =
          if (c == '\r' && index + 1 < input.length && input[index + 1] == '\n') {
            index++
            "\r\n"
          } else {
            c.toString()
          }
        padCurrentRow()
        out.append(newline)
        resetRow()
      }
      else -> {
        out.append(c)
        rowHasContent = true
      }
    }
    index++
  }

  if (rowHasContent || rowFields > 1) {
    padCurrentRow()
  }

  return out.toString()
}

internal fun Source.withPaddedOmittedTrailingCsvFields(): Source {
  val padded = use { source -> padOmittedTrailingCsvFields(source.readString()) }
  return Buffer().apply { writeString(padded) }
}
