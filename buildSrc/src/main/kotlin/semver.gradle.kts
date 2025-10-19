plugins { id("com.javiersc.semver") }

semver {
  tagPrefix = "v"
  mapVersion { "0.2.0" }
}

tasks.register("version") { doLast { println(project.version) } }
