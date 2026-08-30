#!/usr/bin/env bash
set -euo pipefail

root="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
pin="$root/mdb-v1/specs/PIN.md"
catalog="$root/mdb-v1/specs/DatabaseCatalogAPI.yaml"
token="$root/mdb-v1/specs/DatabaseCatalogTokenAPI.yaml"

if [[ ! -f $pin || ! -f $catalog || ! -f $token ]]; then
  echo "mdb-spec-check: missing pin or YAML under mdb-v1/specs" >&2
  exit 1
fi

expected_catalog="$(awk '/DatabaseCatalogAPI\.yaml$/{print $1; exit}' "$pin")"
expected_token="$(awk '/DatabaseCatalogTokenAPI\.yaml$/{print $1; exit}' "$pin")"
actual_catalog="$(sha256sum "$catalog" | awk '{print $1}')"
actual_token="$(sha256sum "$token" | awk '{print $1}')"

status=0
if [[ $expected_catalog != "$actual_catalog" ]]; then
  echo "mdb-spec-check: DatabaseCatalogAPI.yaml SHA-256 does not match PIN.md" >&2
  echo "  pin:  $expected_catalog" >&2
  echo "  file: $actual_catalog" >&2
  status=1
fi
if [[ $expected_token != "$actual_token" ]]; then
  echo "mdb-spec-check: DatabaseCatalogTokenAPI.yaml SHA-256 does not match PIN.md" >&2
  echo "  pin:  $expected_token" >&2
  echo "  file: $actual_token" >&2
  status=1
fi

if [[ $status -eq 0 ]]; then
  echo "mdb-spec-check: pin matches DatabaseCatalogAPI.yaml and DatabaseCatalogTokenAPI.yaml"
fi
exit "$status"
