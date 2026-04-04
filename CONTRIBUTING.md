## Git-LFS

This project uses Git-LFS to manage sample datasets used in tests. Make sure that you have
[installed and configured Git-LFS](https://docs.github.com/en/repositories/working-with-files/managing-large-files/installing-git-large-file-storage)
before cloning the repo.

## Dev Setup

This project uses [mise](https://mise.jdx.dev/) to manage tools and tasks. After cloning the repo,
install all required tools:

```sh
mise install
```

This installs the correct versions of Java, dprint, hk, pkl, and ktfmt, and runs `hk install --mise`
to set up the pre-commit hook.

## IDE Setup

Install the [dprint IDE plugin](https://dprint.dev/install/#editor-extensions) for your editor to
get format-on-save support. The project's `dprint.json` configures all formatting rules.

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

## Documentation

Serve the documentation site locally:

```sh
mise run docs
```
