## Dev Setup

This project uses [mise](https://mise.jdx.dev/) to manage tools and tasks. Install mise, then from
the repo root install all required tools:

```sh
mise install
```

This installs the pinned versions of Java, dprint, hk, ktfmt, and Git-LFS, and runs `hk install
--mise` to set up the pre-commit hook.

## Git-LFS

This project uses [Git-LFS](https://git-lfs.com/) to manage the sample datasets used in tests.
Git-LFS is provided by mise, so you don't need to install it on your host separately. The datasets
are fetched automatically by a [mise deps](https://mise.jdx.dev/dev-tools/deps.html) provider before
the first `mise run`/`mise exec`; you can also fetch them on demand:

```sh
mise deps
```

A fresh clone keeps `sample-data` as Git-LFS pointers until that fetch runs.

## IDE Setup

Install the [dprint](https://plugins.jetbrains.com/plugin/18192-dprint) plugin for format-on-save
support. The project's `dprint.json` configures all formatting rules.

## Formatting

Auto-fix formatting issues:

```sh
mise run fix
```

Check formatting without making changes:

```sh
mise run check
```

A pre-commit hook is managed by [hk](https://hk.jdx.dev/) and runs formatting checks automatically
before each commit. It is installed as part of `mise install` via the `postinstall` hook.

## Running Tests

Run all tests:

```sh
mise run test
```

Run tests for a specific platform:

```sh
mise run test:jvm          # JVM tests
mise run test:jsnode       # JS Node tests
mise run test:wasmjsnode   # WASM JS Node tests
mise run test:native       # Native tests for the current platform
```

Run a full build and check all targets:

```sh
mise run build
```

## Benchmarks

The `benchmarks` module holds [JMH](https://github.com/openjdk/jmh) benchmarks that decode the feeds
in `sample-data`. Arguments go straight to JMH, so you can filter by regex, pick parameters, and add
profilers:

```sh
mise run bench                                  # everything
mise run bench -prof gc GtfsRealtime            # one class, with allocation per op
mise run bench -p feed=mbta/trip_updates.pb 'GtfsRealtimeBenchmark.decode$'
```

To compare a change, run the same command before and after it.

## Documentation

Serve the documentation site locally:

```sh
mise run docs
```
