# CLAUDE.md

## Dev workflow

Tooling is managed by `mise`. Run `mise install` first (installs Java, dprint, hk, downloads ktfmt
jar, and sets up git hooks).

Key tasks:

- `mise run build` — compile and run all checks across all platforms
- `mise run test` — all tests (JVM, JS, WASM, native)
- `mise run test:jvm` / `test:jsnode` / `test:wasmjsnode` / `test:native` — individual platforms
- `mise run check` — lint and format checks (hk)
- `mise run fix` — auto-fix formatting

Run a single test: `./gradlew :module:jvmTest --tests "*SomeTest*"`

## Pitfalls

**After changing any public API**, run `mise run fix` to regenerate the `.api` files — the build
fails without this.

## gtfs-realtime

The Kotlin data classes in this module are handwritten to match the
[official GTFS Realtime protobuf schema](https://github.com/google/transit/blob/master/gtfs-realtime/proto/gtfs-realtime.proto).
They are not generated from the `.proto` file. When the upstream spec changes, update the data
classes and `@ProtoNumber` annotations manually.
