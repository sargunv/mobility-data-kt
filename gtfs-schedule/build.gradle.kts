plugins { id("published-library") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(":utils"))
        api(libs.kotlinx.serialization.json)
        api(libs.kotlinx.datetime)
        implementation(libs.kotlin.dsv)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.kotlinx.io.core)
      }
    }

    jvmTest { dependencies { implementation(libs.kotlinx.io.core) } }
  }
}

mavenPublishing {
  pom {
    name = "GTFS Schedule for Kotlin"
    description = "GTFS Schedule models for Kotlin Multiplatform"
  }
}
