plugins { id("published-library") }

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(":utils"))
        api(libs.kotlinx.serialization.json)
        api(libs.kotlinx.datetime)
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
        implementation(kotlin("test"))
        implementation(kotlin("test-annotations-common"))
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.kotlinx.io.core)
        implementation(libs.ktor.client.engine.defaults)
      }
    }

    create("ktorTest").apply {
      dependencies {
        implementation(kotlin("test"))
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
    name = "Mobility Database Catalog v1 for Kotlin"
    description = "Mobility Database Catalog API v1 client for Kotlin Multiplatform"
  }
}
