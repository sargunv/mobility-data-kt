# Agent Guide for mobility-data-kt

This is a library containing utilities for working with mobility data formats in
Kotlin Multiplatform.

## Modules

- `./utils` - Common utilities for all modules
- `./gtfs-schedule` - General Transit Feed Specification (GTFS) - Schedule
  client
    - Reference:
      https://raw.githubusercontent.com/google/transit/refs/heads/master/gtfs/spec/en/reference.md
- `./gtfs-realtime` - (TODO) GTFS - Realtime client
    - Reference:
      https://raw.githubusercontent.com/google/transit/refs/heads/master/gtfs-realtime/spec/en/reference.md
    - Protocol:
      https://raw.githubusercontent.com/google/transit/refs/heads/master/gtfs-realtime/proto/gtfs-realtime.proto
- `./gbfs-v1` - General Bikeshare Feed Specification (GBFS) v1 client
    - Reference:
      https://raw.githubusercontent.com/MobilityData/gbfs/refs/tags/v1.1/gbfs.md
- `./gbfs-v2` - GBFS v2 client
    - Reference:
      https://raw.githubusercontent.com/MobilityData/gbfs/refs/tags/v2.3/gbfs.md
- `./gbfs-v3` - GBFS v3 client
    - Reference:
      https://raw.githubusercontent.com/MobilityData/gbfs/refs/tags/v3.0/gbfs.md
- `./gofs-v1` - General On-Demand Feed Specification (GOFS) v1 client
    - Reference:
      https://raw.githubusercontent.com/MobilityData/GOFS/refs/heads/main/reference.md

## Commands

- Build: `./gradlew build` or `just build`
- Lint: `./gradlew detekt` or `just detekt`
- Format: `just format` (runs pre-commit hooks)
- Test (all): `./gradlew allTests` or `just test`
- Test (JVM): `./gradlew jvmTest` or `just test-jvm`
- Test (single file): `./gradlew jvmTest --tests ClassName`
- Coverage: `./gradlew :koverHtmlReport` or `just coverage`
- Docs: `./gradlew :dokkaGenerateHtml` or `just build-dokka`

## Commit Guidelines

Never make a commit unless explicitly asked to do so. Such permission only
extends to that one commit, not to future commits in that session.

Before staging files for a commit, format all files and update the ABI dump:

```
pre-commit run --all-files --hook-stage manual
```

When writing commit messages, always include a signoff in the commit message
following this format:

```
🤖 Generated with [Agent Name](https://agent-url)

Co-Authored-By: Agent Name <example@agent-domain>
```

Examples:

- Claude: `🤖 Generated with [Claude Code](https://claude.com/claude-code)` and
  `Co-Authored-By: Claude <noreply@anthropic.com>`
- OpenCode: `🤖 Generated with [OpenCode](https://opencode.ai)` and
  `Co-Authored-By: OpenCode <noreply@opencode.ai>`
- Amp: `🤖 Generated with [Amp](https://ampcode.com)` and
  `Co-Authored-By: Amp <amp@ampcode.com>`

Each coding agent should use their own Author and URL but maintain the same
format.
