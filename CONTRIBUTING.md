## Dev Setup

This project uses [mise](https://mise.jdx.dev/) to manage tools and tasks. Install mise, then from
the repo root install all required tools:

```sh
mise install
```

This installs the pinned versions of Java, dprint, hk, pkl, ktfmt, and Git-LFS, and runs
`hk install --mise` to set up the pre-commit hook.

## Git-LFS

This project uses [Git-LFS](https://git-lfs.com/) to manage the sample datasets used in tests.
Git-LFS is provided by mise, so you don't need to install it on your host separately. The datasets
are fetched automatically by a [mise deps](https://mise.jdx.dev/dev-tools/deps.html) provider before
the first `mise run`/`mise exec`; you can also fetch them on demand:

```sh
mise deps
```

If you cloned before running `mise install`, the sample files start out as Git-LFS pointer files;
running the command above materializes their contents.

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

## Documentation

Serve the documentation site locally:

```sh
mise run docs
```

The docs task passes versions derived from the Git tags, which the site prints as the coordinates to
depend on. Gradle on its own uses the `0.0.0` placeholders from `gradle.properties`.

## Versions

Releases are tagged `vMAJOR.MINOR.PATCH`. `gradle.properties` carries placeholder versions, and
`.mise/bin/version-args` derives the real ones from the tags. Only the tasks that publish or
document a version pass them to Gradle, so an ordinary build needs no tags in the checkout.

```sh
mise run version            # what this commit would build as
mise run version snapshot   # what the nightly job would publish
mise run version release    # what the release workflow would publish
```

A tagged commit builds as that release; every other commit builds as a snapshot of the next patch.

To cut a release, tag `main` and push the tag. The Release workflow publishes that version to Maven
Central and deploys the documentation site:

```sh
git tag v0.5.0
git push origin v0.5.0
```
