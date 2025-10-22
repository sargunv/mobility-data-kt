# GTFS Schedule

The GTFS Schedule module provides an implementation of the
[General Transit Feed Specification (GTFS) Schedule](https://gtfs.org/documentation/schedule/reference/)
for Kotlin Multiplatform.

GTFS Schedule is a standardized data format for public transit schedules and
associated geographic information. It consists of a ZIP archive containing CSV
files that describe transit routes, stops, trips, and schedules.

## Features

- CSV encoding and decoding with
  [kotlin-dsv](https://github.com/sargunv/kotlin-dsv)
- Strong type safety, with appropriate standard library or kotlinx-datetime
  types
- Kotlin Multiplatform support (JVM, Native, JS, WASM)

## Status

This module is currently in progress and provides a partial implementation of
the GTFS Schedule specification. The following files are supported:

- agency.txt
- routes.txt
- trips.txt
- stops.txt
- stop_times.txt
- calendar.txt
- calendar_dates.txt

See [issue #8](https://github.com/sargunv/mobility-data-kt/issues/8) for the
current roadmap and implementation status.

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("dev.sargunv.mobility-data:gtfs-schedule:{{ gradle.project_version }}")
}
```

## Example

```kotlin
--8<-- "gtfs-schedule/src/jvmTest/kotlin/dev/sargunv/mobilitydata/gtfs/schedule/DocsSnippet.kt:example"
```

1. Open a GTFS CSV file (typically extracted from a ZIP archive).
2. Use the `GtfsCsv` format which is preconfigured for GTFS .txt files.
3. Decode the CSV file into a sequence of strongly-typed entities and process
   them as needed.

## API Reference

For detailed API documentation, see the
[API Reference](api/gtfs-schedule/index.html).
