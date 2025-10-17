import dev.detekt.gradle.extensions.FailOnSeverity
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
  id("multiplatform-module")
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
  id("org.jetbrains.kotlinx.kover")
  id("semver")
  id("dev.detekt")
}

group = "dev.sargunv.mobility-data"

kotlin {
  explicitApi()
  compilerOptions {
    freeCompilerArgs =
      listOf(
        // Will be the default soon: https://youtrack.jetbrains.com/issue/KT-11914
        "-Xconsistent-data-class-copy-visibility"
      )
  }
  abiValidation {
    @OptIn(ExperimentalAbiValidation::class)
    enabled = true
  }
}

detekt {
  source.setFrom("src/commonMain/kotlin")
  config.setFrom(rootProject.file("detekt.yml"))
  failOnSeverity = FailOnSeverity.Warning
  basePath.set(rootProject.rootDir)
}

dokka {
  dokkaSourceSets {
    configureEach {
      includes.from("MODULE.md")
      sourceLink {
        remoteUrl("https://github.com/sargunv/mobility-data-kt/tree/v${project.version}/")
        localDirectory = rootDir
      }
      externalDocumentationLinks {
        create("kotlinx-serialization") { url("https://kotlinlang.org/api/kotlinx.serialization/") }
        create("kotlinx-io") { url("https://kotlinlang.org/api/kotlinx-io/") }
      }
    }
  }
}

mavenPublishing {
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()

  pom {
    url = "https://github.com/sargunv/mobility-data-kt/"

    scm {
      url = "https://github.com/sargunv/mobility-data-kt"
      connection = "scm:git:git://github.com/sargunv/mobility-data-kt.git"
      developerConnection = "scm:git:git://github.com/sargunv/mobility-data-kt.git"
    }

    licenses {
      license {
        name = "Apache-2.0"
        url = "https://opensource.org/licenses/Apache-2.0"
        distribution = "repo"
      }
    }

    developers {
      developer {
        id = "sargunv"
        name = "Sargun Vohra"
      }
    }
  }
}
