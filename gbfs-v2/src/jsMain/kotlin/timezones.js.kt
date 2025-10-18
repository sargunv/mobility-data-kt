// https://github.com/Kotlin/kotlinx-datetime/blob/master/README.md#note-about-time-zones-in-js

@JsModule("@js-joda/timezone") @JsNonModule private external object JsJodaTimeZoneModule

@OptIn(ExperimentalJsExport::class) @JsExport private val jsJodaTz = JsJodaTimeZoneModule
