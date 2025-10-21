import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType
import ru.vyarus.gradle.plugin.mkdocs.task.MkdocsTask

plugins {
  id("org.jetbrains.kotlinx.kover")
  id("org.jetbrains.dokka")
  id("semver")
  id("ru.vyarus.mkdocs-build")
}

dokka {
  moduleName = "Mobility Data for Kotlin"
  dokkaPublications { html { outputDirectory = rootDir.absoluteFile.resolve("docs/api") } }
  pluginsConfiguration { html { footerMessage = "Copyright &copy; 2025 Sargun Vohra" } }
}

mkdocs {
  sourcesDir = "."
  strict = true
  publish {
    docPath = null // single version site
  }
}

tasks.withType<MkdocsTask>().configureEach {
  dependsOn("dokkaGenerateHtml")
  extras.assign(
    provider {
      mapOf(
        "project_version" to project.version.toString(),
        "ktor_version" to libs.versions.ktor.get(),
      )
    }
  )
}

kover {
  reports {
    total {
      log {
        // default groups by module
        groupBy = GroupingEntityType.PACKAGE
      }
    }
  }
}

dependencies {
  dokka(project(":utils"))
  kover(project(":utils"))

  dokka(project(":gbfs-v1"))
  kover(project(":gbfs-v1"))

  dokka(project(":gbfs-v2"))
  kover(project(":gbfs-v2"))

  dokka(project(":gbfs-v3"))
  kover(project(":gbfs-v3"))

  dokka(project(":gofs-v1"))
  kover(project(":gofs-v1"))

  dokka(project(":gtfs-schedule"))
  kover(project(":gtfs-schedule"))
}
