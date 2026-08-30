---
name: mdb-spec-bump
description: Bump the pinned Mobility Database Catalog OpenAPI specs and the handwritten mdb-v1 types.
---

# Bump the Mobility Database Catalog spec

The public `mdb-v1` types are handwritten. The source of truth is the bundled YAML from a
MobilityData/mobility-feed-api **release**, not `main` and not GitHub Pages.

Pinned files:

- `mdb-v1/specs/pin.toml` (`release`, `repository`, `[files]` SHA-256)
- `mdb-v1/specs/DatabaseCatalogAPI.yaml`
- `mdb-v1/specs/DatabaseCatalogTokenAPI.yaml`

Read the current tag from `release` in `pin.toml`. Do not copy a version out of this skill or the
docs.

## Download the new pin

```bash
tag=v1.16.2  # replace with the release you are bumping to
gh release download "$tag" --repo MobilityData/mobility-feed-api --pattern "*.yaml" --dir /tmp/mdb-spec-bump
```

Use only the two catalog assets. Ignore OperationsAPI and UserServiceAPI.

## Diff against the pin

```bash
diff -u mdb-v1/specs/DatabaseCatalogAPI.yaml /tmp/mdb-spec-bump/DatabaseCatalogAPI.yaml
diff -u mdb-v1/specs/DatabaseCatalogTokenAPI.yaml /tmp/mdb-spec-bump/DatabaseCatalogTokenAPI.yaml
```

If both diffs are empty, stop. The pin is current.

## Replace the pin

Copy the two YAML files into `mdb-v1/specs/`. Set `release` in `pin.toml` to the new tag. Set each
`[files]` hash with Python, not `sha256sum` (that binary is missing on stock macOS):

```bash
python3 - <<'PY'
from hashlib import sha256
from pathlib import Path
for name in ("DatabaseCatalogAPI.yaml", "DatabaseCatalogTokenAPI.yaml"):
    digest = sha256((Path("mdb-v1/specs") / name).read_bytes()).hexdigest()
    print(f'{name} = "{digest}"')
PY
```

Do not reformat the YAML. `dprint.json` excludes `mdb-v1/specs/*.yaml` so the pin hashes stay
stable. `pin.toml` is formatted.

Run `mise run check:mdb-spec`. It must exit 0.

## Edit the Kotlin types

Read the spec diff, then edit only the files that the delta touches.

- New or changed feed fields: `mdb-v1/src/commonMain/kotlin/dev/sargunv/mobilitydata/mdb/v1/Feed.kt`
- Dataset fields: `Dataset.kt`
- Location fields: `Location.kt`
- License fields: `License.kt`
- Token fields: `Token.kt`
- Search envelope: `Search.kt`
- Availability: `Availability.kt` and `MdbV1Client.getGtfsFeedAvailability`
- Reliability: `Reliability.kt` and `MdbV1Client.getGtfsFeedReliability`
- New list or get path: `MdbV1Client.kt` plus a query object in `FeedQuery.kt`
- `GET /v1/gtfs_feeds/{id}/continuous_coverage` is on `main` and not in the current pin. Add it only
  after the pin includes it.
- `POST /v1/licenses:match` stays out. It uses an undefined `ApiKeyAuth`.

Keep `Feed` as a sealed class on `data_type`. GBFS still subclasses `Feed` even when the spec allOfs
`BasicFeed`.

JSON names stay on `@SerialName`. Public properties stay camelCase. IDs stay `FeedId` or `String` as
they are today.

## Tests and ABI

Update the encode/decode goldens in `commonTest` and the MockEngine tests in `ktorTest`. Then:

```bash
mise exec -- ./gradlew :mdb-v1:jvmTest
mise run fix
```

`mise run fix` regenerates `mdb-v1/api/*.api`.
