package dev.sargunv.mobilitydata.utils

import kotlin.coroutines.cancellation.CancellationException

/**
 * Like [runCatching], but rethrows [CancellationException] to preserve structured concurrency.
 *
 * Plain [runCatching] catches all [Throwable] including [CancellationException], which silently
 * breaks coroutine cancellation. Use this in suspend functions that return [Result].
 */
public inline fun <R> suspendRunCatching(block: () -> R): Result<R> =
  try {
    Result.success(block())
  } catch (e: CancellationException) {
    throw e
  } catch (e: Throwable) {
    Result.failure(e)
  }
