plugins { id("published-library") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(":utils"))
        api(libs.kotlinx.serialization.protobuf)
      }
    }

    create("ktorMain").apply {
      dependencies { api(libs.ktor.client.core) }
      dependsOn(commonMain.get())
      listOf(jvmMain, nativeMain, jsMain, wasmJsMain).forEach { it.get().dependsOn(this) }
    }

    commonTest { dependencies { implementation(libs.kotlinx.coroutines.test) } }

    jvmTest { dependencies { implementation(libs.gtfs.realtime.bindings) } }

    create("ktorTest").apply {
      dependencies {
        implementation(kotlin("test"))
        implementation(libs.ktor.client.mock)
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
    name = "GTFS Realtime for Kotlin"
    description = "GTFS Realtime protobuf support for Kotlin Multiplatform"
  }
}
