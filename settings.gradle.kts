pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement { @Suppress("UnstableApiUsage") repositories { mavenCentral() } }

rootProject.name = "mobility-data"

include(":utils", ":gbfs-v1", ":gbfs-v2", ":gbfs-v3", ":gofs-v1", ":gtfs-schedule")
