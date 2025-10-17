pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement { @Suppress("UnstableApiUsage") repositories { mavenCentral() } }

rootProject.name = "root"

include(":gbfs", ":gtfs", ":gtfs-rt")
