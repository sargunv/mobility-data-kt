package dev.sargunv.mobilitydata.utils

import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SuspendRunCatchingTest {
  @Test
  fun returnsSuccessOnNormalCompletion() {
    val result = suspendRunCatching { "hello" }
    assertEquals("hello", result.getOrThrow())
  }

  @Test
  fun returnsFailureOnNonCancellationException() {
    val result = suspendRunCatching { throw IllegalStateException("boom") }
    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is IllegalStateException)
  }

  @Test
  fun rethrowsCancellationException() {
    assertFailsWith<CancellationException> {
      suspendRunCatching { throw CancellationException("cancelled") }
    }
  }
}
