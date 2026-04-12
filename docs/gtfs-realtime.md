# GTFS Realtime

The GTFS Realtime module implements the
[General Transit Feed Specification (GTFS) Realtime](https://gtfs.org/documentation/realtime/reference/)
for Kotlin Multiplatform.

GTFS Realtime is a standardized data feed for real-time transit information such as trip updates,
vehicle positions, and service alerts. It uses Protocol Buffers as its wire format.

## Features

- Protobuf encoding and decoding with kotlinx-serialization
- HTTP client for fetching feeds using Ktor
- Strong type safety with Kotlin data classes and enums
- Kotlin Multiplatform support (JVM, Native, JS, WASM)

## Installation

Add the dependency to your `build.gradle.kts`. The client functionality requires Ktor, so also add a
Ktor engine:

```kotlin
dependencies {
    implementation("dev.sargunv.mobility-data:gtfs-realtime:{{ gradle.project_version }}")
    implementation("io.ktor:ktor-client-cio:{{ gradle.ktor_version }}") // or another engine
}
```

## Example

```kotlin
--8<-- "gtfs-realtime/src/ktorTest/kotlin/dev/sargunv/mobilitydata/gtfs/realtime/DocsSnippet.kt:example"
```

1. Create a GTFS Realtime client instance. The client implements `AutoCloseable` so it can be used
   with `.use` to ensure proper cleanup.
2. Fetch and decode a GTFS Realtime protobuf feed from a URL.
3. Inspect the feed entities for trip updates, vehicle positions, and alerts.

## API Reference

See the [API Reference](api/gtfs-realtime/index.html).
