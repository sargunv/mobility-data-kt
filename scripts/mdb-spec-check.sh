#!/usr/bin/env bash
set -euo pipefail

root="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
exec python3 - "$root" <<'PY'
import hashlib
import sys
import tomllib
from pathlib import Path

root = Path(sys.argv[1])
pin_path = root / "mdb-v1" / "specs" / "pin.toml"
specs = root / "mdb-v1" / "specs"

if not pin_path.is_file():
    print("mdb-spec-check: missing mdb-v1/specs/pin.toml", file=sys.stderr)
    sys.exit(1)

pin = tomllib.loads(pin_path.read_text())
files = pin.get("files")
if not isinstance(files, dict) or not files:
    print("mdb-spec-check: pin.toml has no [files] table", file=sys.stderr)
    sys.exit(1)

status = 0
for name, expected in files.items():
    path = specs / name
    if not path.is_file():
        print(f"mdb-spec-check: missing {path}", file=sys.stderr)
        status = 1
        continue
    actual = hashlib.sha256(path.read_bytes()).hexdigest()
    if actual != expected:
        print(f"mdb-spec-check: {name} SHA-256 does not match pin.toml", file=sys.stderr)
        print(f"  pin:  {expected}", file=sys.stderr)
        print(f"  file: {actual}", file=sys.stderr)
        status = 1

if status == 0:
    release = pin.get("release", "unknown")
    print(f"mdb-spec-check: pin.toml {release} matches {', '.join(files)}")
sys.exit(status)
PY
