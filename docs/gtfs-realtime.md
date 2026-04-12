# GTFS Realtime

The `gtfs-realtime` module provides Kotlin Multiplatform models for the
[GTFS Realtime](https://gtfs.org/documentation/realtime/reference/) protobuf schema.

This module implements the official GTFS Realtime protobuf schema for Kotlin Multiplatform. It
ships:

- Kotlin models for the upstream schema
- A binary protobuf codec for `FeedMessage`
- A Ktor-based client for fetching live GTFS Realtime feeds
- The vendored upstream `gtfs-realtime.proto` file as a checked-in reference

## Features

- GTFS Realtime protobuf models using `kotlinx.serialization`
- Binary decode and encode helpers for `FeedMessage`
- HTTP client for fetching protobuf feeds using Ktor
- Kotlin Multiplatform support (JVM, Native, JS, WASM)
- Real-feed fixture coverage with checked-in protobuf samples
- Additional JVM interop coverage against the official Java GTFS Realtime bindings

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
2. Fetch a GTFS Realtime protobuf feed from a public endpoint.
3. Count the number of trip updates, vehicle positions, and alerts in the response.

## Upstream Schema

The canonical upstream protobuf definition is checked in at
`gtfs-realtime/proto/gtfs-realtime.proto`.

To update it manually:

```bash
mise run update:gtfs-realtime:proto
```

To refresh the checked-in real-feed fixtures:

```bash
mise run update:gtfs-realtime:fixtures
```

## API Reference

For detailed API documentation, see the API Reference:

- [GTFS Realtime API Reference](./api/gtfs-realtime/index.html)
