pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement { @Suppress("UnstableApiUsage") repositories { mavenCentral() } }

rootProject.name = "mobility-data"

include(":utils")

include(":gbfs-v2")
