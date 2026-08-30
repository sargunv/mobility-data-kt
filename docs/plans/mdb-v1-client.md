# Mobility Database client plan

This program adds a published `mdb-v1` module. Importers get a Kotlin client for the Mobility
Database Catalog API. The rule is pin the OpenAPI spec and handwrite the public API. The PR ids in
order are mdb-1, mdb-2, mdb-3, mdb-4, and mdb-5.

## How to read this

One box is one unit of work. Every box names the evidence that checks it. A nested box is a sub-step
of the box above it. Check a box only when its evidence exists, a file, a log line, a screenshot, a
test run, or a SHA. The body is a how-to. The appendices explain and record.

The program runs `pstack/skills/poteto-mode/playbooks/autopilot-stack.md`. The operator lands the
Graphite stack. mdb-1, mdb-2, mdb-3, mdb-4, and mdb-5 stop at merge-ready.

Tests alone are not sufficient verification. A PR is verified only when its unit, live, and perf
boxes are all checked.

## Program checklist

### Arm the program

- [ ] State the protocol and this plan to the operator, then stop. Start execution only on her
      explicit go.
- [ ] On her go, arm a `/goal` with this exact text. "`docs/plans/mdb-v1-client.md`. PR ids mdb-1,
      mdb-2, mdb-3, mdb-4, mdb-5. Tests alone are not sufficient verification. A PR is verified only
      when its unit, live, and perf boxes are all checked. The operator lands the stack. Done when
      `mdb-v1` compiles, the pinned specs match MobilityData release `v1.16.2`, and the spec-bump
      skill plus check script exist."
- [ ] Read these from trunk at program start. Re-read them at every tick.
  - [ ] `git show origin/main:pstack/skills/poteto-mode/playbooks/autopilot-stack.md`
  - [ ] `git show origin/main:pstack/skills/swarm/SKILL.md`
  - [ ] `git show origin/main:pstack/skills/control-cli/SKILL.md`
  - [ ] `git show origin/main:pstack/skills/poteto-mode/playbooks/opening-a-pr.md`
  - [ ] `git show origin/main:pstack/skills/how/SKILL.md`
  - [ ] `git show origin/main:pstack/skills/architect/SKILL.md`
  - [ ] `git show origin/main:pstack/skills/technical-writing/SKILL.md`
- [ ] Arm the 30-minute audit tick. In a local session, a real terminal `/loop`. In a cloud root, a
      cloud-sleeper wake chain. Never leave the cadence to memory.
- [ ] Use this tick prompt, verbatim. "Re-read the execution playbook from trunk and the armed
      /goal. Audit the operation against both and fix drift in this tick. Probe every active lane
      and judge progress by side effects only. Stand down a stuck lane and dispatch its replacement
      now. Then send the operator a status message, whether or not anything changed, with the queue
      table of PR, owner, state, and head SHA, the verdicts since the last tick, what merged, open
      operator gates, and blockers."
- [ ] On the operator's hold or stand-down, send every owner a zero-writes order at once.

### Spawn owners

- [ ] Spawn one owner per PR with the full lifecycle the execution playbook names.
- [ ] Follow this dependency graph. Start dependent work only after its parent merges, or base it on
      the parent branch when the execution playbook stacks.
  - [ ] mdb-1 is first. It branches from `main`.
  - [ ] mdb-2 after mdb-1.
  - [ ] mdb-3 after mdb-2.
  - [ ] mdb-4 after mdb-3.
  - [ ] mdb-5 after mdb-4.
- [ ] Hold the file boundaries. mdb-1 touches only `mdb-v1/**`, `settings.gradle.kts`,
      `build.gradle.kts`, `AGENTS.md`, `CLAUDE.md`, and the pinned spec files. mdb-2 touches only
      `mdb-v1/src/commonMain/**`, `mdb-v1/src/commonTest/**`, `mdb-v1/api/**`, and
      `sample-data/mdb-v1/**`. mdb-3 touches only `mdb-v1/src/ktorMain/**`,
      `mdb-v1/src/ktorTest/**`, and `mdb-v1/build.gradle.kts`. mdb-4 stays in `mdb-v1/**` and
      `sample-data/mdb-v1/**`. mdb-5 touches `.cursor/skills/mdb-spec-bump/**`,
      `scripts/mdb-spec-check*`, `docs/**`, `README.md`, `mise.toml`, and `mdb-v1/MODULE.md`.
- [ ] Hold the review gate. mdb-2, mdb-3, mdb-4, and mdb-5 change an interaction. They wait for the
      operator's review in chat with screenshots and a video before merge.

### PR mechanics, for every PR

- [ ] Open the PR ready, never draft, with `gh pr create` and `draft: false`, or with Graphite `gt`
      for a stack.
- [ ] Run the repo's lint and typecheck once before the PR-facing push. Push with hooks on.
- [ ] Run `/deslop` before each commit and `/no-comments` before review.
- [ ] Triage every Bugbot and security-reviewer comment per `../references/bugbot-triage.md`.
- [ ] Rebase onto current trunk before babysit and again before the merge-ready report.

### Verdict and merge, for every PR

- [ ] At the merge-ready head SHA, run the swarm per `pstack/skills/swarm/SKILL.md`. One gates lane.
      The ten live lanes from the PR's **Verify, live** block. The perf lane from its **Verify,
      perf** block. One audit lane that reads the diff and the receipts and distrusts the PR body.
- [ ] Clean only when every lane is `PASS`. Findings go back to the owner. A new head gets a fresh
      swarm and a fresh verdict.
- [ ] The root appends the PR to the Graphite stack. The operator lands it. After restack, compare
      `git patch-id` at each verdict SHA against the new head. Drift voids the old verdict.

### Boot recipe, for every live lane

Each live lane runs on its own cloud VM at the PR head. Drive through `control-cli` from
`cursor-team-kit`. Docs pages in mdb-5 also use `control-ui`.

- [ ] `git fetch origin <head-branch> && git checkout <head SHA>`.
- [ ] Run `mise install` if tools are missing. Wait until `java -version` prints 21.
- [ ] Deliver input only through `control-cli` gradle and mise commands. Read-only diagnostics are
      `git status`, `git rev-parse HEAD`, and the gradle test HTML under
      `mdb-v1/build/reports/tests/`.
- [ ] Save every screenshot to `/tmp/swarm-<pr-id>/worker-<n>/<slug>.png` and return the paths with
      the report.

## Pin the spec and wire the empty module (mdb-1)

**Depends on.** None.

**Files.**

- [ ] Create `mdb-v1/specs/DatabaseCatalogAPI.yaml`.
- [ ] Create `mdb-v1/specs/DatabaseCatalogTokenAPI.yaml`.
- [ ] Create `mdb-v1/specs/PIN.md`.
- [ ] Create `mdb-v1/build.gradle.kts`.
- [ ] Create `mdb-v1/MODULE.md`.
- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/.gitkeep`.
- [ ] Edit `settings.gradle.kts`.
- [ ] Edit `build.gradle.kts`.
- [ ] Edit `AGENTS.md`.
- [ ] Edit `CLAUDE.md`.

**Build.**

- [ ] Add `:mdb-v1` with `published-library`, the same `ktorMain` source set as
      `gofs-v1/build.gradle.kts`, and the two bundled OpenAPI files from MobilityData release
      `v1.16.2`.
- [ ] Write `mdb-v1/specs/PIN.md` with the release tag, the asset SHA256, and the download command.

**You see.**

- [ ] `./gradlew :mdb-v1:compileKotlinJvm` exits 0.
- [ ] `git show HEAD:mdb-v1/specs/PIN.md` names `v1.16.2`.

**Verify, unit.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] `mdb-v1/src/commonTest` has no tests yet. Run
      `mise exec -- ./gradlew :mdb-v1:compileKotlinJvm :mdb-v1:compileTestKotlinJvm`.

**Verify, live.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked. Ten lanes on `grok-4.6-fast-xhigh` at the PR head, per the
boot recipe.

- [ ] Lane 1. Print `settings.gradle.kts` and confirm `:mdb-v1`. Save `mdb-1-settings.png`. Pass
      when the include list contains `:mdb-v1`.
- [ ] Lane 2. Run `./gradlew :mdb-v1:compileKotlinJvm`. Save `mdb-1-compile.png`. Pass when the
      command exits 0.
- [ ] Lane 3. SHA256 the pinned catalog spec and compare it to the `v1.16.2` release asset. Save
      `mdb-1-catalog-hash.png`. Pass when the hashes match.
- [ ] Lane 4. SHA256 the pinned token spec and compare it to the `v1.16.2` release asset. Save
      `mdb-1-token-hash.png`. Pass when the hashes match.
- [ ] Lane 5. Run `./gradlew :mdb-v1:apiDump`. Save `mdb-1-api-dump.png`. Pass when
      `mdb-v1/api/mdb-v1.api` exists.
- [ ] Lane 6. Run `./gradlew :mdb-v1:detekt`. Save `mdb-1-detekt.png`. Pass when detekt exits 0.
- [ ] Lane 7. Run `mise run check`. Save `mdb-1-mise-check.png`. Pass when hk check exits 0.
- [ ] Lane 8. Read `mdb-v1/MODULE.md`. Save `mdb-1-module-md.png`. Pass when the file titles the
      module `mdb-v1`.
- [ ] Lane 9. Read `AGENTS.md` and `CLAUDE.md`. Save `mdb-1-agents.png`. Pass when both name
      `mdb-v1`.
- [ ] Lane 10. Read `build.gradle.kts` dokka and kover blocks. Save `mdb-1-root-wiring.png`. Pass
      when both reference `project(":mdb-v1")`.

**Verify, perf.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] Metric. Wall time of `./gradlew :mdb-v1:compileKotlinJvm` after a warm daemon.
- [ ] Probe. Run the compile twice on trunk (task missing is the baseline) and twice at the head,
      interleaved.
- [ ] Baseline. Record the trunk result first. Trunk has no `:mdb-v1` task.
- [ ] Rule. Head compile after warmup must stay under 60 seconds. Fail at 60 seconds or more.

**Review gate.** None. mdb-1 is not review-gated.

**Merge.**

- [ ] Root's clean verdict at the exact head SHA.
- [ ] Bugbot triage done.
- [ ] Rebased onto current trunk after the verdict, patch-id unchanged.
- [ ] The root appends the PR to the Graphite stack and the operator lands it.

## Model the catalog types (mdb-2)

**Depends on.** mdb-1.

**Files.**

- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/Feed.kt`.
- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/Dataset.kt`.
- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/Location.kt`.
- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/License.kt`.
- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/Metadata.kt`.
- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/Token.kt`.
- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/MdbJson.kt`.
- [ ] Create
      `mdb-v1/src/commonTest/kotlin/dev/sargunv/mobilitydata/mdb/v1/FeedSerializationTest.kt`.
- [ ] Create `sample-data/mdb-v1/`.

**Build.**

- [ ] Encode the catalog as Kotlin types. `Feed` is a sealed class on `data_type`. `FeedId` is a
      value class. JSON names stay on `@SerialName`. Public properties are camelCase.
- [ ] Run `pstack/skills/architect/SKILL.md` on this PR before the first type file. The sketch is
      the contract.

**You see.**

- [ ] `mise exec -- ./gradlew :mdb-v1:jvmTest` exits 0.
- [ ] `mdb-v1/api/mdb-v1.api` lists `Feed`, `FeedId`, and `MdbJson`.

**Verify, unit.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] `FeedSerializationTest` decodes GTFS, GTFS-RT, and GBFS fixtures. Run
      `mise exec -- ./gradlew :mdb-v1:jvmTest --tests "*FeedSerializationTest*"`.

**Verify, live.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked. Ten lanes on `grok-4.6-fast-xhigh` at the PR head, per the
boot recipe.

- [ ] Lane 1. Decode a GTFS feed fixture and print `id` and `provider`. Save `mdb-2-gtfs.png`. Pass
      when the printed id matches the fixture.
- [ ] Lane 2. Decode a GTFS-RT feed fixture. Save `mdb-2-gtfs-rt.png`. Pass when the value is
      `Feed.GtfsRt`.
- [ ] Lane 3. Decode a GBFS feed fixture. Save `mdb-2-gbfs.png`. Pass when the value is `Feed.Gbfs`.
- [ ] Lane 4. Decode a token response fixture. Save `mdb-2-token.png`. Pass when `access_token` maps
      to `AccessToken`.
- [ ] Lane 5. Decode a dataset fixture. Save `mdb-2-dataset.png`. Pass when `downloaded_at` is an
      Instant.
- [ ] Lane 6. Decode a location search fixture. Save `mdb-2-location.png`. Pass when `country_code`
      maps to a typed property.
- [ ] Lane 7. Decode a license fixture. Save `mdb-2-license.png`. Pass when rules deserialize.
- [ ] Lane 8. Feed a JSON object with an extra key into `MdbJson`. Save `mdb-2-unknown-key.png`.
      Pass when decode succeeds.
- [ ] Lane 9. Feed a JSON object with an unknown `data_type` into `Feed`. Save
      `mdb-2-unknown-type.png`. Pass when decode fails with a typed error.
- [ ] Lane 10. Open the Dokka page for `Feed`. Save `mdb-2-dokka.png`. Pass when KDoc is visible and
      has no HTML tags.

**Verify, perf.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] Metric. Time to decode a 3500-item `Feeds` fixture with `MdbJson`.
- [ ] Probe. A jvm test that decodes the fixture 20 times and prints median milliseconds. Run it on
      the parent SHA with a stub clock if needed, then at the head, interleaved.
- [ ] Baseline. Record the parent median first. Parent has no decoder, so record "absent".
- [ ] Rule. Head median must stay under 200 ms on the lane VM. Fail at 200 ms or more.

**Review gate.** The operator reviews before merge.

- [ ] Copy lane 1 and lane 10 screenshots into `/tmp/mdb-review/mdb-2-review-gtfs.png` and
      `/tmp/mdb-review/mdb-2-review-dokka.png`.
- [ ] Record a 30 to 60 second video of decode plus the Dokka page on a lane VM. Save it as
      `/tmp/mdb-review/mdb-2-review.mp4`.
- [ ] Post the screenshots and the video in chat. Stop at merge-ready. Wait for the operator's
      click.

**Merge.**

- [ ] Root's clean verdict at the exact head SHA.
- [ ] Bugbot triage done.
- [ ] Rebased onto current trunk after the verdict, patch-id unchanged.
- [ ] The root appends the PR to the Graphite stack and the operator lands it.

## Build the authenticated client (mdb-3)

**Depends on.** mdb-2.

**Files.**

- [ ] Create `mdb-v1/src/ktorMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/MdbV1Client.kt`.
- [ ] Create `mdb-v1/src/ktorMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/CatalogAuth.kt`.
- [ ] Create `mdb-v1/src/ktorMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/FeedQuery.kt`.
- [ ] Create `mdb-v1/src/ktorTest/kotlin/dev/sargunv/mobilitydata/mdb/v1/TokenRefreshTest.kt`.
- [ ] Create `mdb-v1/src/ktorTest/kotlin/dev/sargunv/mobilitydata/mdb/v1/GetFeedsTest.kt`.
- [ ] Create
      `mdb-v1/src/ktorTest/kotlin/dev/sargunv/mobilitydata/mdb/v1/ResultErrorHandlingTest.kt`.
- [ ] Edit `mdb-v1/build.gradle.kts`.

**Build.**

- [ ] Add `MdbV1Client` in the house style of `GofsV1Client`. One class, `AutoCloseable`, `Result`
      returns, injectable `HttpClientEngine`, `expectSuccess = true`.
- [ ] Accept `CatalogAuth.Refresh` or `CatalogAuth.Access`. Refresh posts to `/v1/tokens/access` and
      retries once on 401.

**You see.**

- [ ] `TokenRefreshTest` prints `POST /v1/tokens/access` then `GET /v1/feeds`.
- [ ] `GetFeedsTest` decodes a mock list through `client.getFeeds()`.

**Verify, unit.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] `TokenRefreshTest`, `GetFeedsTest`, and `ResultErrorHandlingTest`. Run
      `mise exec -- ./gradlew :mdb-v1:jvmTest --tests "*TokenRefreshTest*" --tests "*GetFeedsTest*" --tests "*ResultErrorHandlingTest*"`.

**Verify, live.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked. Ten lanes on `grok-4.6-fast-xhigh` at the PR head, per the
boot recipe.

- [ ] Lane 1. Construct the client with a refresh token and list feeds against `MockEngine`. Save
      `mdb-3-refresh-list.png`. Pass when the mock log has the token POST before the feeds GET.
- [ ] Lane 2. Construct the client with an access token and skip the token POST. Save
      `mdb-3-access-only.png`. Pass when the mock log has no `/v1/tokens/access`.
- [ ] Lane 3. Replay a 401 then a new token then a 200. Save `mdb-3-retry.png`. Pass when the second
      GET succeeds.
- [ ] Lane 4. Call `getFeed(FeedId("mdb-1210"))`. Save `mdb-3-get-feed.png`. Pass when the path is
      `/v1/feeds/mdb-1210`.
- [ ] Lane 5. Call `getGtfsFeeds` with a `GtfsFeedQuery`. Save `mdb-3-gtfs-query.png`. Pass when
      `limit`, `offset`, and `country_code` are on the request.
- [ ] Lane 6. Call `getGtfsRtFeeds`. Save `mdb-3-gtfs-rt.png`. Pass when the path is
      `/v1/gtfs_rt_feeds`.
- [ ] Lane 7. Call `getGbfsFeeds`. Save `mdb-3-gbfs.png`. Pass when the path is `/v1/gbfs_feeds`.
- [ ] Lane 8. Force HTTP 500 from the mock. Save `mdb-3-error.png`. Pass when the return is
      `Result.failure`.
- [ ] Lane 9. Call `close()` then a fetch. Save `mdb-3-close.png`. Pass when the second call fails
      because the engine is closed.
- [ ] Lane 10. Run a `DocsSnippet` that prints a provider name from the mock. Save
      `mdb-3-snippet.png`. Pass when the printed name matches the fixture.

**Verify, perf.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] Metric. Time for `getFeeds` against `MockEngine` returning the 3500-item fixture.
- [ ] Probe. A jvm test that calls `getFeeds` 20 times and prints median milliseconds. Run it on
      mdb-2 then at the head, interleaved.
- [ ] Baseline. Record the mdb-2 median first. mdb-2 has no client, so record "absent".
- [ ] Rule. Head median must stay under 250 ms on the lane VM. Fail at 250 ms or more.

**Review gate.** The operator reviews before merge.

- [ ] Copy lane 1 and lane 10 screenshots into `/tmp/mdb-review/mdb-3-review-refresh.png` and
      `/tmp/mdb-review/mdb-3-review-snippet.png`.
- [ ] Record a 30 to 60 second video of the snippet on a lane VM. Save it as
      `/tmp/mdb-review/mdb-3-review.mp4`.
- [ ] Post the screenshots and the video in chat. Stop at merge-ready. Wait for the operator's
      click.

**Merge.**

- [ ] Root's clean verdict at the exact head SHA.
- [ ] Bugbot triage done.
- [ ] Rebased onto current trunk after the verdict, patch-id unchanged.
- [ ] The root appends the PR to the Graphite stack and the operator lands it.

## Cover the remaining catalog endpoints (mdb-4)

**Depends on.** mdb-3.

**Files.**

- [ ] Edit `mdb-v1/src/ktorMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/MdbV1Client.kt`.
- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/Availability.kt`.
- [ ] Create `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/Reliability.kt`.
- [ ] Create `mdb-v1/src/ktorTest/kotlin/dev/sargunv/mobilitydata/mdb/v1/SearchTest.kt`.
- [ ] Create `mdb-v1/src/ktorTest/kotlin/dev/sargunv/mobilitydata/mdb/v1/DatasetsTest.kt`.
- [ ] Create `mdb-v1/src/ktorTest/kotlin/dev/sargunv/mobilitydata/mdb/v1/BetaEndpointsTest.kt`.
- [ ] Create `sample-data/mdb-v1/search.json`.

**Build.**

- [ ] Add search, locations, datasets, licenses, and metadata on `MdbV1Client`.
- [ ] Mark availability and reliability with `ExperimentalMobilityDataApi`. Leave
      `POST /v1/licenses:match` out. Leave `GET /v1/gtfs_feeds/{id}/continuous_coverage` out until
      the pin moves past `v1.16.2`.

**You see.**

- [ ] `SearchTest` decodes a mock `/v1/search` body.
- [ ] `BetaEndpointsTest` fails to compile a call without
      `@OptIn(ExperimentalMobilityDataApi::class)`.

**Verify, unit.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] `SearchTest`, `DatasetsTest`, and `BetaEndpointsTest`. Run
      `mise exec -- ./gradlew :mdb-v1:jvmTest --tests "*SearchTest*" --tests "*DatasetsTest*" --tests "*BetaEndpointsTest*"`.

**Verify, live.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked. Ten lanes on `grok-4.6-fast-xhigh` at the PR head, per the
boot recipe.

- [ ] Lane 1. Call `searchFeeds` with a query string. Save `mdb-4-search.png`. Pass when the mock
      path is `/v1/search`.
- [ ] Lane 2. Call `getLocations`. Save `mdb-4-locations.png`. Pass when `search_query` is on the
      request.
- [ ] Lane 3. Call `getGtfsFeedDatasets`. Save `mdb-4-datasets.png`. Pass when the path contains
      `/datasets`.
- [ ] Lane 4. Call `getDatasetGtfs`. Save `mdb-4-dataset-one.png`. Pass when the path is
      `/v1/datasets/gtfs/{id}`.
- [ ] Lane 5. Call `getMetadata`. Save `mdb-4-metadata.png`. Pass when the body decodes.
- [ ] Lane 6. Call `getLicenses` and `getLicense`. Save `mdb-4-licenses.png`. Pass when both mock
      paths match the spec.
- [ ] Lane 7. Call `getGtfsFeedAvailability` under `@OptIn`. Save `mdb-4-availability.png`. Pass
      when the path ends with `/availability`.
- [ ] Lane 8. Call `getGtfsFeedReliability` under `@OptIn`. Save `mdb-4-reliability.png`. Pass when
      the path ends with `/reliability`.
- [ ] Lane 9. Grep the public API for `licenses:match` and `continuous_coverage`. Save
      `mdb-4-excluded.png`. Pass when neither string is in `mdb-v1/src`.
- [ ] Lane 10. Walk a `FeedQuery` page by raising `offset`. Save `mdb-4-page.png`. Pass when the
      second request uses the new offset.

**Verify, perf.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] Metric. Time for `searchFeeds` against a 100-hit mock body.
- [ ] Probe. A jvm test that calls `searchFeeds` 20 times and prints median milliseconds. Run it on
      mdb-3 then at the head, interleaved.
- [ ] Baseline. Record the mdb-3 median first. mdb-3 has no search, so record "absent".
- [ ] Rule. Head median must stay under 100 ms on the lane VM. Fail at 100 ms or more.

**Review gate.** The operator reviews before merge.

- [ ] Copy lane 1 and lane 7 screenshots into `/tmp/mdb-review/mdb-4-review-search.png` and
      `/tmp/mdb-review/mdb-4-review-beta.png`.
- [ ] Record a 30 to 60 second video of search plus one beta call on a lane VM. Save it as
      `/tmp/mdb-review/mdb-4-review.mp4`.
- [ ] Post the screenshots and the video in chat. Stop at merge-ready. Wait for the operator's
      click.

**Merge.**

- [ ] Root's clean verdict at the exact head SHA.
- [ ] Bugbot triage done.
- [ ] Rebased onto current trunk after the verdict, patch-id unchanged.
- [ ] The root appends the PR to the Graphite stack and the operator lands it.

## Add the spec-bump skill and the docs (mdb-5)

**Depends on.** mdb-4.

**Files.**

- [ ] Create `.cursor/skills/mdb-spec-bump/SKILL.md`.
- [ ] Create `scripts/mdb-spec-check.sh`.
- [ ] Edit `mise.toml`.
- [ ] Edit `docs/index.md`.
- [ ] Create `docs/mdb.md`.
- [ ] Edit `mkdocs.yml`.
- [ ] Edit `README.md`.
- [ ] Edit `mdb-v1/MODULE.md`.
- [ ] Create `mdb-v1/src/ktorTest/kotlin/dev/sargunv/mobilitydata/mdb/v1/DocsSnippet.kt`.

**Build.**

- [ ] Write a skill that downloads the latest bundled release assets, diffs them against
      `mdb-v1/specs/`, and lists the Kotlin files an owner must edit.
- [ ] Add `mise run check:mdb-spec` that fails when the pin file and the checked-in YAML disagree.

**You see.**

- [ ] `mise run check:mdb-spec` exits 0 on a clean tree.
- [ ] `mise run docs` serves a page that includes the `DocsSnippet` example.

**Verify, unit.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] `scripts/mdb-spec-check.sh` against the pinned files. Run `mise run check:mdb-spec`.

**Verify, live.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked. Ten lanes on `grok-4.6-fast-xhigh` at the PR head, per the
boot recipe.

- [ ] Lane 1. Run `mise run check:mdb-spec`. Save `mdb-5-check-clean.png`. Pass when the command
      exits 0.
- [ ] Lane 2. Edit one byte of the pinned YAML and rerun the check. Save `mdb-5-check-dirty.png`.
      Pass when the command exits non-zero.
- [ ] Lane 3. Restore the YAML. Save `mdb-5-check-restored.png`. Pass when the check exits 0 again.
- [ ] Lane 4. Open `docs/mdb.md` in the local MkDocs server. Save `mdb-5-docs-page.png`. Pass when
      the page titles Mobility Database.
- [ ] Lane 5. Confirm the docs example uses `MdbV1Client`. Save `mdb-5-snippet.png`. Pass when the
      snippet contains `MdbV1Client`.
- [ ] Lane 6. Open the README module list. Save `mdb-5-readme.png`. Pass when README names Mobility
      Database.
- [ ] Lane 7. Open the MkDocs nav. Save `mdb-5-nav.png`. Pass when nav has a Mobility Database
      entry.
- [ ] Lane 8. Read the skill file. Save `mdb-5-skill.png`. Pass when the skill names the pin tag and
      the two YAML files.
- [ ] Lane 9. Run `mise run check`. Save `mdb-5-hk.png`. Pass when hk check exits 0.
- [ ] Lane 10. Run `./gradlew :mdb-v1:jvmTest --tests "*DocsSnippet*"`. Save
      `mdb-5-snippet-test.png`. Pass when the test exits 0.

**Verify, perf.** Tests alone are not sufficient verification. A PR is verified only when its unit,
live, and perf boxes are all checked.

- [ ] Metric. Wall time of `mise run check:mdb-spec`.
- [ ] Probe. Run the check three times on mdb-4 (task missing is the baseline) and three times at
      the head, interleaved.
- [ ] Baseline. Record the parent result first. Parent has no task.
- [ ] Rule. Head check must stay under 15 seconds. Fail at 15 seconds or more.

**Review gate.** The operator reviews before merge.

- [ ] Copy lane 4 and lane 5 screenshots into `/tmp/mdb-review/mdb-5-review-docs.png` and
      `/tmp/mdb-review/mdb-5-review-snippet.png`.
- [ ] Record a 30 to 60 second video of the docs page on a lane VM. Save it as
      `/tmp/mdb-review/mdb-5-review.mp4`.
- [ ] Post the screenshots and the video in chat. Stop at merge-ready. Wait for the operator's
      click.

**Merge.**

- [ ] Root's clean verdict at the exact head SHA.
- [ ] Bugbot triage done.
- [ ] Rebased onto current trunk after the verdict, patch-id unchanged.
- [ ] The root appends the PR to the Graphite stack and the operator lands it.

## Close the program

- [ ] Every box above is checked with its evidence.
- [ ] Reply to the operator with the report the execution playbook names.

## Appendix A. Prototype evidence

Codegen from the pinned `v1.16.2` catalog spec does not compile. OpenAPI Generator `kotlin` with
`library=multiplatform` wrote `sealed classsealed class Feed`, doubled `@Serializable`, and
`GtfsFeed : Feed()()`. The throwaway tree is `/tmp/mobility-feed-api-proto/kotlin-mp`. The broken
type is
`/tmp/mobility-feed-api-proto/kotlin-mp/src/commonMain/kotlin/dev/sargunv/mobilitydata/catalog/generated/models/Feed.kt`.

A live GET to `https://api.mobilitydatabase.org/v1/feeds` without a token returns 302 from Google
IAP. `POST /v1/tokens/access` with `{}` returns 400 and `{"error": "Missing refresh_token."}`. CI
cannot call the catalog without a secret refresh token. Fixtures stay checked in.

Unproven items are the following.

- Whether a refresh token for a MobilityData test account can live in org secrets later.
- Whether `kotlinx-datetime` Instant or `kotlin.time.Instant` should be the public date type. The
  house modules use kotlinx types. Follow that unless `architect` on mdb-2 proves a conflict.
- Whether importers want a `Flow` helper for paging. Ship `offset` and `limit` first.

## Appendix B. Alternatives rejected

Publish the OpenAPI Generator tree as the public API. It fails to compile and it splits the client
into `FeedsApi`, `DatasetsApi`, and friends. That is not this repo.

Generate into an internal package and wrap it. The generated models are the part that breaks. A
wrapper cannot hide a type that does not parse.

Write a custom generator. Twenty operations and about forty schemas do not pay for a generator. The
spec-bump skill is the lever.

Ship OperationsAPI or UserServiceAPI in the same module. Those specs are not the public catalog.
Keep them out.

Pin `main` instead of a release. `main` added `continuous_coverage` after `v1.16.2` and still
`$ref`s `BearerTokenSchema.yaml`. Pin the bundled release assets.

Name the module `mobility-feed-api`. The repo names modules after the spec and a version, like
`gofs-v1`. `mdb-v1` matches that.

## Appendix C. Risks

This repo does not vendor `pstack`. Owners read the same skills from the Cursor plugin cache when
`git show origin/main:pstack/...` fails.

There is no library control skill. Live lanes drive gradle and mise through `control-cli`. mdb-5
docs lanes also use `control-ui`.

Catalog calls need a refresh token. Live lanes use `MockEngine` and fixtures. A future secret is
optional and is not a merge blocker.

`POST /v1/licenses:match` names `ApiKeyAuth`, which the catalog spec does not define. Leave that
operation out of mdb-4.

The spec `info.version` is `1.0.0` on every release. Pin the GitHub release tag, not that field.

`Feed` in the spec uses `allOf` and a discriminator that only maps `gtfs` and `gtfs_rt`. GBFS feeds
`allOf` `BasicFeed`. The handwritten sealed class must still decode GBFS. mdb-2 owns that.

## Appendix D. Links and reading list

Read these before editing.

- [Mobility Feed API Swagger UI](https://mobilitydata.github.io/mobility-feed-api/SwaggerUI/index.html)
- [MobilityData/mobility-feed-api](https://github.com/MobilityData/mobility-feed-api)
- Release assets on `v1.16.2`, `DatabaseCatalogAPI.yaml` and `DatabaseCatalogTokenAPI.yaml`
- `gofs-v1/src/ktorMain/kotlin/dev/sargunv/mobilitydata/gofs/v1/GofsV1Client.kt`
- `gofs-v1/build.gradle.kts`
- `buildSrc/src/main/kotlin/published-library.gradle.kts`
- `gbfs-v2/src/ktorTest/kotlin/dev/sargunv/mobilitydata/gbfs/v2/DocsSnippet.kt`
- `docs/gofs.md`

mdb-2 runs `pstack/skills/how/SKILL.md` and `pstack/skills/architect/SKILL.md` and
`pstack/skills/interrogate/SKILL.md` on the type sketch.

mdb-3 runs `pstack/skills/how/SKILL.md` on `GofsV1Client` before writing `MdbV1Client`.

The trail is a local `decisions.tsv` per `pstack/skills/show-me-your-work/SKILL.md`. Do not commit
it.
