pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement { @Suppress("UnstableApiUsage") repositories { mavenCentral() } }

rootProject.name = "mobility-data"

include(":utils", ":gbfs-v2", ":gofs-v1")
