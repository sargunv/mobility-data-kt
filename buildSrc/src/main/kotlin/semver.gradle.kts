plugins { id("com.javiersc.semver") }

val disableSemver = providers.gradleProperty("disableSemver").orNull == "true"

if (disableSemver) {
  version = "0.0.0-dev"
} else {
  semver { tagPrefix = "v" }
}

tasks.register("version") { doLast { println(project.version) } }
