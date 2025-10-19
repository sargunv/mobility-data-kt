# Agent Guide for mobility-data-kt

This is a library containing utilities for working with mobility data formats in
Kotlin Multiplatform.

## Modules

- `./gbfs-v2` - General Bikeshare Feed Specification v2 client

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
