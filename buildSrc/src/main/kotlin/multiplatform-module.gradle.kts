@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins { id("base-module") }

kotlin {
  js {
    browser {
      // our mock http tests rely on the filesystem
      testTask { enabled = false }
    }
    nodejs { testTask { useMocha { timeout = "10s" } } }
  }

  wasmJs {
    browser {
      // our mock http tests rely on the filesystem
      testTask { enabled = false }
    }
    nodejs {}
    d8 {
      // https://github.com/sargunv/mobility-data-kt/issues/5
      testTask { enabled = false }
    }
  }

  wasmWasi { nodejs {} }

  // native tier 1
  macosArm64()
  iosSimulatorArm64()
  iosArm64()

  // native tier 2
  linuxX64()
  linuxArm64()
  watchosSimulatorArm64()
  watchosArm32()
  watchosArm64()
  tvosSimulatorArm64()
  tvosArm64()

  // native tier 3
  mingwX64()
  androidNativeArm32()
  androidNativeArm64()
  androidNativeX86()
  androidNativeX64()
  watchosDeviceArm64()
  iosX64()
}
