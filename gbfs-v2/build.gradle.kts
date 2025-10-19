import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins { id("published-library") }

kotlin {
  compilerOptions { freeCompilerArgs.add("-Xcontext-parameters") }

  // Failing with no clear reason
  @OptIn(ExperimentalWasmDsl::class) wasmJs { d8 { testTask { enabled = false } } }

  sourceSets {
    commonMain {
      dependencies {
        api(libs.kotlinx.serialization.json)
        api(libs.kotlinx.datetime)
        api(libs.spatialk.geojson)
      }
    }

    // https://github.com/Kotlin/kotlinx-datetime/blob/master/README.md#note-about-time-zones-in-js
    webMain {
      dependencies { implementation(npm("@js-joda/timezone", libs.versions.joda.timezone.get())) }
    }
    wasmWasiMain { dependencies { implementation(libs.kotlinx.datetime.zoneinfo) } }

    create("ktorMain").apply {
      dependencies {
        api(libs.ktor.client.core)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.serialization.kotlinx.json)
      }
      dependsOn(commonMain.get())
      listOf(jvmMain, nativeMain, jsMain, wasmJsMain).forEach { it.get().dependsOn(this) }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.kotlinx.io.core)
      }
    }

    jvmTest { dependencies { implementation(libs.ktor.client.okhttp) } }
    appleTest { dependencies { implementation(libs.ktor.client.darwin) } }
    linuxTest { dependencies { implementation(libs.ktor.client.curl) } }
    mingwTest { dependencies { implementation(libs.ktor.client.winhttp) } }
    jsTest { dependencies { implementation(libs.ktor.client.js) } }
    wasmJsTest { dependencies { implementation(libs.ktor.client.js) } }

    create("ktorTest").apply {
      dependsOn(commonTest.get())
      listOf(jvmTest, macosTest, linuxTest, mingwTest, jsTest, wasmJsTest).forEach {
        it.get().dependsOn(this)
      }
    }
  }
}

mavenPublishing {
  pom {
    name = "GBFS v2 for Kotlin"
    description = "GBFS v2 client for Kotlin Multiplatform"
  }
}
