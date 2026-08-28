# Mobility Data for Kotlin

A set of Kotlin Multiplatform libraries for working with open transportation data formats (GTFS,
GBFS, and GOFS). Each module is a distinct library published to Maven Central.

## Project map

- `utils` — shared serialization helpers and internal utilities
- `gbfs-v1` / `gbfs-v2` / `gbfs-v3` — General Bikeshare Feed Specification clients
- `gofs-v1` — General On-Demand Feed Specification client
- `gtfs-schedule` — GTFS Schedule (static) support
- `gtfs-realtime` — GTFS Realtime protobuf types (see invariants below)
- `sample-data` — Git-LFS-tracked feed fixtures used by tests
- `docs` — MkDocs documentation site

## Dev tool commands

Tooling is managed by [mise](https://mise.jdx.dev). Install mise, then run `mise install` to install
the pinned tools (Java, dprint, hk, pkl, ktfmt, git-lfs) and set up the git hooks. The Git-LFS
sample datasets used by tests are fetched automatically by a `mise deps` provider before the first
`mise run`/`mise exec`; run `mise deps` to fetch them on demand.

```bash
# Install/refresh all tools and git hooks
mise install

# List available tasks
mise tasks --all

# Compile all platforms and run all checks (Detekt, ABI, formatting)
mise run build

# Run all tests (JVM, JS, WASM, native)
mise run test

# Test specific platforms
mise run test:jvm
mise run test:jsnode
mise run test:wasmjsnode
mise run test:native

# Lint and format checks / auto-fix
mise run check
mise run fix

# Run a single test
mise exec -- ./gradlew :module:jvmTest --tests "*SomeTest*"

# Serve the documentation site locally
mise run docs
```

Formatters and linters run automatically on pre-commit via [hk](https://hk.jdx.dev); you usually
don't need to run them manually. The environment is managed by mise, so run any tool that isn't
already a mise task with `mise exec -- <command>`.

## Project invariants

- **After changing any public API**, run `mise run fix` to regenerate the `.api` files — the build
  fails without this.
- The Kotlin data classes in `gtfs-realtime` are handwritten to match the
  [official GTFS Realtime protobuf schema](https://github.com/google/transit/blob/master/gtfs-realtime/proto/gtfs-realtime.proto).
  They are not generated from the `.proto` file. When the upstream spec changes, update the data
  classes and `@ProtoNumber` annotations manually.
