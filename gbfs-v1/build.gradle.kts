plugins { id("published-library") }

kotlin {
  compilerOptions { freeCompilerArgs.add("-Xcontext-parameters") }

  sourceSets {
    commonMain {
      dependencies {
        api(project(":utils"))
        api(libs.kotlinx.serialization.json)
        api(libs.kotlinx.datetime)
        api(libs.spatialk.geojson)
      }
    }

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
      dependencies {
        implementation(libs.ktor.client.mock)
        implementation(libs.kotlinx.io.core)
      }
      dependsOn(commonTest.get())
      listOf(jvmTest, macosTest, linuxTest, mingwTest, jsTest, wasmJsTest).forEach {
        it.get().dependsOn(this)
      }
    }
  }
}

mavenPublishing {
  pom {
    name = "GBFS v1 for Kotlin"
    description = "GBFS v1 client for Kotlin Multiplatform"
  }
}
