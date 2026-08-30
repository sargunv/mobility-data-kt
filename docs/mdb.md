# Mobility Database

The `mdb-v1` module is a client for the
[Mobility Database Catalog API](https://mobilitydata.github.io/mobility-feed-api/SwaggerUI/index.html).

The types are handwritten against the OpenAPI assets from the MobilityData/mobility-feed-api release
named in `mdb-v1/specs/pin.toml`.

## Features

- JSON encoding and decoding with kotlinx-serialization
- HTTP client for the catalog using Ktor
- Refresh-token exchange with a single retry on 401
- Kotlin Multiplatform support (JVM, Native, JS, WASM)

## Installation

Add the dependency to your `build.gradle.kts`. The client functionality requires Ktor, so also add a
Ktor engine:

```kotlin
dependencies {
    implementation("dev.sargunv.mobility-data:mdb-v1:{{ gradle.project_version }}")
    implementation("io.ktor:ktor-client-cio:{{ gradle.ktor_version }}") // or another engine
}
```

## Example

```kotlin
--8<-- "mdb-v1/src/ktorTest/kotlin/dev/sargunv/mobilitydata/mdb/v1/DocsSnippet.kt:example"
```

1. Create a catalog client with a refresh token. The client implements `AutoCloseable` so it can be
   used with `.use`.
2. List feeds. The first call posts to `/v1/tokens/access`, then retries the request with the bearer
   token. A bare `getFeeds()` uses the catalog default of 3500 rows. The example passes
   `limit = 10`. Other list paths default to 2500 (GTFS), 1000 (GTFS-RT), 500 (GBFS and datasets),
   or 100 (locations, licenses, availability).
3. Read the sealed `Feed` subclass. GTFS, GTFS-RT, and GBFS share status, official, seasonal, and
   related links. An unrecognized `data_type` decodes as `Feed.Unknown`.

## API Reference

For detailed API documentation, see the [API Reference](api/mdb-v1/index.html).
