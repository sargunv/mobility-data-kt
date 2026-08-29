package dev.sargunv.mobilitydata.gtfs.schedule

import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.readCodePointValue
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

internal fun Source.withPaddedOmittedTrailingCsvFields(): Source =
  TrailingCsvFieldPaddingSource(this).buffered()

private class TrailingCsvFieldPaddingSource(
  private val upstream: Source,
  private val delimiter: Int = ','.code,
  private val quote: Int = '"'.code,
) : RawSource {
  private val pending = Buffer()
  private var finished = false
  private var inQuotes = false
  private var headerFields = -1
  private var rowFields = 1
  private var rowHasContent = false

  override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
    require(byteCount >= 0)
    if (byteCount == 0L) return 0L
    fill(byteCount)
    if (pending.size == 0L) return -1L
    return pending.readAtMostTo(sink, byteCount)
  }

  private fun fill(minBytes: Long) {
    while (pending.size < minBytes && !finished) {
      if (upstream.exhausted()) {
        if (rowHasContent || rowFields > 1) padCurrentRow()
        finished = true
        return
      }
      process(upstream.readCodePointValue())
    }
  }

  private fun process(cp: Int) {
    when {
      inQuotes && cp == quote -> {
        if (tryConsumeAscii(quote)) {
          emit(quote)
          emit(quote)
          rowHasContent = true
        } else {
          inQuotes = false
          emit(cp)
        }
      }
      !inQuotes && cp == quote -> {
        inQuotes = true
        emit(cp)
        rowHasContent = true
      }
      !inQuotes && cp == delimiter -> {
        emit(cp)
        rowFields++
      }
      !inQuotes && cp == '\r'.code -> {
        padCurrentRow()
        emit('\r'.code)
        if (tryConsumeAscii('\n'.code)) emit('\n'.code)
        resetRow()
      }
      !inQuotes && cp == '\n'.code -> {
        padCurrentRow()
        emit(cp)
        resetRow()
      }
      else -> {
        emit(cp)
        rowHasContent = true
      }
    }
  }

  private fun tryConsumeAscii(expected: Int): Boolean {
    if (upstream.exhausted()) return false
    val peeked = upstream.peek()
    if (!peeked.request(1)) return false
    if (peeked.readByte() != expected.toByte()) return false
    upstream.skip(1)
    return true
  }

  private fun isEmptyRow(): Boolean = !rowHasContent && rowFields == 1

  private fun padCurrentRow() {
    if (headerFields < 0) {
      headerFields = rowFields
    } else if (!isEmptyRow() && rowFields < headerFields) {
      repeat(headerFields - rowFields) { emit(delimiter) }
    }
  }

  private fun resetRow() {
    rowFields = 1
    rowHasContent = false
  }

  private fun emit(cp: Int) {
    pending.writeString(codePointToString(cp))
  }

  override fun close() {
    upstream.close()
  }
}

private fun codePointToString(cp: Int): String {
  if (cp <= 0xFFFF) return cp.toChar().toString()
  val offset = cp - 0x10000
  return charArrayOf(((offset ushr 10) + 0xD800).toChar(), ((offset and 0x3FF) + 0xDC00).toChar())
    .concatToString()
}
