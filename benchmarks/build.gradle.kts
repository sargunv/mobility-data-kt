plugins {
  id("org.jetbrains.kotlin.jvm")
  id("me.champeau.jmh")
}

kotlin { jvmToolchain(25) }

dependencies {
  jmh(project(":gtfs-realtime"))
  jmh(project(":gtfs-schedule"))
  jmh(libs.gtfs.realtime.bindings)
  jmh(libs.kotlinx.io.core)
}

jmh { jmhVersion = libs.versions.jmh }

// Keep the benchmarks compiling with the rest of the build.
tasks.check { dependsOn(tasks.named("jmhClasses")) }
