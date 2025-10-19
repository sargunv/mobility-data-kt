pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement { @Suppress("UnstableApiUsage") repositories { mavenCentral() } }

rootProject.name = "mobility-data"

include(":gbfs-v2")
