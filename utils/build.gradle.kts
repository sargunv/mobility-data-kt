plugins { id("published-library") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.kotlinx.serialization.core)
        api(libs.kotlinx.datetime)
      }
    }

    // https://github.com/Kotlin/kotlinx-datetime/blob/master/README.md#note-about-time-zones-in-js
    webMain {
      dependencies { implementation(npm("@js-joda/timezone", libs.versions.joda.timezone.get())) }
    }
    wasmWasiMain { dependencies { implementation(libs.kotlinx.datetime.zoneinfo) } }

    commonTest { dependencies { api(libs.kotlinx.serialization.json) } }
  }
}

mavenPublishing {
  pom {
    name = "Mobility Data Utils"
    description = "Shared utilities for other Mobility Data for Kotlin modules"
  }
}
