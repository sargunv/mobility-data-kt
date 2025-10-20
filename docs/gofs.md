# GOFS

The GOFS module provides an implementation of the
[General On-Demand Feed Specification (GOFS)](https://github.com/MobilityData/GOFS)
for Kotlin Multiplatform.

GOFS is a standardized data feed for real-time access to information about
on-demand mobility services such as ride-hailing, taxi services, and other
demand-responsive transportation.

## Modules

- `gofs-v1`: Implementation of the GOFS v1.0 specification.

## Features

- JSON encoding and decoding with kotlinx-serialization
- HTTP client for fetching feeds using Ktor
- Strong type safety, with appropriate standard library or kotlinx-datetime
  types
- Kotlin Multiplatform support (JVM, Native, JS, WASM)
- GeoJSON support using [Spatial-K](https://maplibre.org/spatial-k/geojson/)

## Installation

Add the dependency to your `build.gradle.kts`. The client functionality requires
Ktor, so also add a Ktor engine:

```kotlin
dependencies {
    implementation("dev.sargunv.mobility-data:gofs-v1:{{ gradle.project_version }}")
    implementation("io.ktor:ktor-client-cio:{{ gradle.ktor_version }}") // or another engine
}
```

## Example

```kotlin
--8<-- "gofs-v1/src/ktorTest/kotlin/dev/sargunv/mobilitydata/gofs/v1/DocsSnippet.kt:example"
```

1. Create a GOFS client instance. The client implements `AutoCloseable` so it
   can be used with `.use` to ensure proper cleanup.
2. Fetch the manifest (auto-discovery file) which contains URLs for all
   available feeds in different languages.
3. Use a
   [context parameter](https://kotlinlang.org/docs/context-parameters.html) to
   specify which language service to use for subsequent feed requests.
4. Fetch system information including the system name, operator, and timezone.
5. Fetch the list of available service brands with their IDs and names.
6. Fetch real-time wait time estimates for a specific pickup location using
   latitude and longitude coordinates.

## API Reference

For detailed API documentation, see the [API Reference](api/gofs-v1/index.html).
