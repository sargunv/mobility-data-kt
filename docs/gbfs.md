# GBFS

The GBFS modules provides implementations of the
[General Bikeshare Feed Specification (GBFS)](https://gbfs.org) for Kotlin
Multiplatform.

GBFS is a standardized data feed for real-time access to information about
bikeshare, scooter-share, and other shared mobility services.

## Modules

- `gbfs-v1`: Implementation of the GBFS v1.1 specification.
- `gbfs-v2`: Implementation of the GBFS v2.3 specification.
- `gbfs-v3`: Implementation of the GBFS v3.0 specification.

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

=== "GBFS v3.0"

    ```kotlin
    dependencies {
        implementation("dev.sargunv.mobility-data:gbfs-v3:{{ gradle.project_version }}")
        implementation("io.ktor:ktor-client-cio:{{ gradle.ktor_version }}") // or another engine
    }
    ```

=== "GBFS v2.3"

    ```kotlin
    dependencies {
        implementation("dev.sargunv.mobility-data:gbfs-v2:{{ gradle.project_version }}")
        implementation("io.ktor:ktor-client-cio:{{ gradle.ktor_version }}") // or another engine
    }
    ```

=== "GBFS v1.1"

    ```kotlin
    dependencies {
        implementation("dev.sargunv.mobility-data:gbfs-v1:{{ gradle.project_version }}")
        implementation("io.ktor:ktor-client-cio:{{ gradle.ktor_version }}") // or another engine
    }    ```

## Example

=== "GBFS v3.0"

    ```kotlin
    --8<-- "gbfs-v2/src/ktorTest/kotlin/dev/sargunv/mobilitydata/gbfs/v3/DocsSnippet.kt:example"
    ```

    1. Create a GBFS client instance. The client implements `AutoCloseable` so it
       can be used with `.use` to ensure proper cleanup.
    2. Fetch the manifest (auto-discovery file) which contains URLs for all
       available feeds.
    3. Use a
       [context parameter](https://kotlinlang.org/docs/context-parameters.html) to
       specify the service to use for subsequent feed requests.
    4. Fetch system information including the system name, operator, and timezone.
    5. Fetch the list of available vehicle types with their form factors and
       propulsion types.
    6. Fetch real-time status of free-floating vehicles including their locations.

=== "GBFS v2.3"

    ```kotlin
    --8<-- "gbfs-v2/src/ktorTest/kotlin/dev/sargunv/mobilitydata/gbfs/v2/DocsSnippet.kt:example"
    ```

    1. Create a GBFS client instance. The client implements `AutoCloseable` so it
       can be used with `.use` to ensure proper cleanup.
    2. Fetch the manifest (auto-discovery file) which contains URLs for all
       available feeds in different languages.
    3. Use a
       [context parameter](https://kotlinlang.org/docs/context-parameters.html) to
       specify which language service to use for subsequent feed requests.
    4. Fetch system information including the system name, operator, and timezone.
    5. Fetch the list of available vehicle types with their form factors and
       propulsion types.
    6. Fetch real-time status of free-floating vehicles including their locations.

=== "GBFS v1.1"

    ```kotlin
    --8<-- "gbfs-v2/src/ktorTest/kotlin/dev/sargunv/mobilitydata/gbfs/v1/DocsSnippet.kt:example"
    ```

    1. Create a GBFS client instance. The client implements `AutoCloseable` so it
       can be used with `.use` to ensure proper cleanup.
    2. Fetch the manifest (auto-discovery file) which contains URLs for all
       available feeds in different languages.
    3. Use a
       [context parameter](https://kotlinlang.org/docs/context-parameters.html) to
       specify which language service to use for subsequent feed requests.
    4. Fetch system information including the system name, operator, and timezone.
    5. Fetch the list of available stations with their names and capacities.
    6. Fetch real-time status of free-floating vehicles including their locations.

## API Reference

For detailed API documentation, see the API Reference:

- [GBFS v3.0 API Reference](./api/gbfs-v3/index.html)
- [GBFS v2.3 API Reference](./api/gbfs-v2/index.html)
- [GBFS v1.1 API Reference](./api/gbfs-v1/index.html)
