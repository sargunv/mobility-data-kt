plugins { id("published-library") }

kotlin {
  sourceSets {
    commonMain.dependencies { api(libs.kotlinx.serialization.json) }

    create("fsTest").apply {
      dependsOn(commonTest.get())
      jvmTest.get().dependsOn(this)
      macosArm64Test.get().dependsOn(this)
      macosX64Test.get().dependsOn(this)
      linuxArm64Test.get().dependsOn(this)
      linuxX64Test.get().dependsOn(this)
      mingwX64Test.get().dependsOn(this)
    }
  }
}

mavenPublishing {
  pom {
    name = "GBFS for Kotlin"
    description = "GBFS client for Kotlin Multiplatform"
  }
}
