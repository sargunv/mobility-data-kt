package dev.sargunv.mobilitydata.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlinx.datetime.IllegalTimeZoneException
import kotlinx.datetime.TimeZone

class TimeZoneIdTest {
  @Test
  fun resolvesKnownIdentifier() {
    assertEquals(TimeZone.of("America/Chicago"), "America/Chicago".toTimeZone())
    assertEquals(TimeZone.of("UTC"), "UTC".toTimeZoneOrNull())
  }

  @Test
  fun unknownIdentifierDoesNotResolve() {
    assertFailsWith<IllegalTimeZoneException> { "Not/AZone".toTimeZone() }
    assertNull("Not/AZone".toTimeZoneOrNull())
  }
}
