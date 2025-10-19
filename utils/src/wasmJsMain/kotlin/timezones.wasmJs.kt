// https://github.com/Kotlin/kotlinx-datetime/blob/master/README.md#note-about-time-zones-in-js

@OptIn(ExperimentalWasmJsInterop::class)
@JsModule("@js-joda/timezone")
private external object JsJodaTimeZoneModule

@Suppress("unused") private val jsJodaTz = JsJodaTimeZoneModule
