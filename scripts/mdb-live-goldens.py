#!/usr/bin/env python3
"""Fetch one representative payload per catalog endpoint. Requires MOBILITY_DATABASE_REFRESH_TOKEN."""

from __future__ import annotations

import json
import os
import sys
import urllib.error
import urllib.request
from pathlib import Path
from typing import Any

BASE = "https://api.mobilitydatabase.org"
OUT = Path(sys.argv[1] if len(sys.argv) > 1 else "mdb-v1/src/jvmTest/resources/goldens")


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, *args: object, **kwargs: object) -> None:
        return None


def request(
    method: str,
    path: str,
    token: str | None = None,
    body: dict[str, str] | None = None,
) -> tuple[int, Any]:
    data = None if body is None else json.dumps(body).encode()
    headers = {"Accept": "application/json"}
    if body is not None:
        headers["Content-Type"] = "application/json"
    if token:
        headers["Authorization"] = f"Bearer {token}"
    req = urllib.request.Request(f"{BASE}{path}", data=data, headers=headers, method=method)
    opener = urllib.request.build_opener(NoRedirect)
    try:
        with opener.open(req, timeout=60) as resp:
            raw = resp.read()
            return resp.status, json.loads(raw) if raw else None
    except urllib.error.HTTPError as e:
        raw = e.read()
        try:
            parsed = json.loads(raw) if raw else {"error": e.reason}
        except json.JSONDecodeError:
            parsed = {"error": e.reason, "body_prefix": raw[:200].decode("utf-8", "replace")}
        return e.code, parsed


def write(name: str, payload: Any, status: int, decode_as: str, manifest: list[dict[str, object]]) -> None:
    path = OUT / name
    text = json.dumps(payload, indent=2, ensure_ascii=False) + "\n"
    path.write_text(text)
    manifest.append(
        {
            "file": name,
            "decode_as": decode_as,
            "status": status,
            "bytes": path.stat().st_size,
        }
    )
    print(f"{status} {name} {decode_as} {path.stat().st_size}B")


def first_id(items: Any, key: str = "id") -> str | None:
    if not isinstance(items, list):
        return None
    for item in items:
        if isinstance(item, dict) and item.get(key):
            return str(item[key])
    return None


def first_id_of_type(items: Any, data_type: str) -> str | None:
    if not isinstance(items, list):
        return None
    for item in items:
        if isinstance(item, dict) and item.get("data_type") == data_type and item.get("id"):
            return str(item["id"])
    return None


def main() -> int:
    refresh = os.environ.get("MOBILITY_DATABASE_REFRESH_TOKEN")
    if not refresh:
        print("MOBILITY_DATABASE_REFRESH_TOKEN is unset", file=sys.stderr)
        return 2

    OUT.mkdir(parents=True, exist_ok=True)
    manifest: list[dict[str, object]] = []

    status, token_body = request("POST", "/v1/tokens/access", body={"refresh_token": refresh})
    if status != 200 or not isinstance(token_body, dict) or not token_body.get("access_token"):
        print(f"token exchange failed status={status}", file=sys.stderr)
        return 1
    access = str(token_body["access_token"])
    redacted = dict(token_body)
    redacted["access_token"] = "REDACTED"
    write("tokens_access.json", redacted, status, "AccessToken", manifest)

    def get(path: str) -> tuple[int, Any]:
        return request("GET", path, token=access)

    status, feeds = get("/v1/feeds?limit=8")
    write("get_feeds.json", feeds, status, "List<Feed>", manifest)
    gtfs_id = first_id_of_type(feeds, "gtfs")
    gtfs_rt_id = first_id_of_type(feeds, "gtfs_rt")
    gbfs_id = first_id_of_type(feeds, "gbfs")

    status, gtfs_feeds = get("/v1/gtfs_feeds?limit=2")
    write("get_gtfs_feeds.json", gtfs_feeds, status, "List<Feed.Gtfs>", manifest)
    gtfs_id = gtfs_id or first_id(gtfs_feeds)

    status, gtfs_rt_feeds = get("/v1/gtfs_rt_feeds?limit=2")
    write("get_gtfs_rt_feeds.json", gtfs_rt_feeds, status, "List<Feed.GtfsRt>", manifest)
    gtfs_rt_id = gtfs_rt_id or first_id(gtfs_rt_feeds)

    status, gbfs_feeds = get("/v1/gbfs_feeds?limit=2")
    write("get_gbfs_feeds.json", gbfs_feeds, status, "List<Feed.Gbfs>", manifest)
    gbfs_id = gbfs_id or first_id(gbfs_feeds)

    if gtfs_id:
        status, body = get(f"/v1/feeds/{gtfs_id}")
        write("get_feed_gtfs.json", body, status, "Feed", manifest)
        status, body = get(f"/v1/gtfs_feeds/{gtfs_id}")
        write("get_gtfs_feed.json", body, status, "Feed.Gtfs", manifest)
        status, datasets = get(f"/v1/gtfs_feeds/{gtfs_id}/datasets?limit=2")
        write("get_gtfs_feed_datasets.json", datasets, status, "List<GtfsDataset>", manifest)
        status, body = get(f"/v1/gtfs_feeds/{gtfs_id}/gtfs_rt_feeds")
        write("get_gtfs_feed_gtfs_rt_feeds.json", body, status, "List<Feed.GtfsRt>", manifest)
        status, body = get(f"/v1/gtfs_feeds/{gtfs_id}/availability?limit=5")
        write("get_gtfs_feed_availability.json", body, status, "GtfsFeedAvailabilityResponse", manifest)
        status, body = get(f"/v1/gtfs_feeds/{gtfs_id}/reliability")
        write("get_gtfs_feed_reliability.json", body, status, "FeedReliabilityReport", manifest)
        dataset_id = first_id(datasets)
        if dataset_id:
            status, body = get(f"/v1/datasets/gtfs/{dataset_id}")
            write("get_dataset_gtfs.json", body, status, "GtfsDataset", manifest)

    if gtfs_rt_id:
        status, body = get(f"/v1/feeds/{gtfs_rt_id}")
        write("get_feed_gtfs_rt.json", body, status, "Feed", manifest)
        status, body = get(f"/v1/gtfs_rt_feeds/{gtfs_rt_id}")
        write("get_gtfs_rt_feed.json", body, status, "Feed.GtfsRt", manifest)

    if gbfs_id:
        status, body = get(f"/v1/feeds/{gbfs_id}")
        write("get_feed_gbfs.json", body, status, "Feed", manifest)
        status, body = get(f"/v1/gbfs_feeds/{gbfs_id}")
        write("get_gbfs_feed.json", body, status, "Feed.Gbfs", manifest)

    status, body = get("/v1/metadata")
    write("get_metadata.json", body, status, "Metadata", manifest)
    status, body = get("/v1/search?search_query=bus&limit=5")
    write("search_feeds.json", body, status, "SearchFeedsResponse", manifest)
    status, body = get("/v1/locations?search_query=montreal&limit=5")
    write("get_locations.json", body, status, "LocationSearchResponse", manifest)
    status, licenses = get("/v1/licenses?limit=5")
    write("get_licenses.json", licenses, status, "List<License>", manifest)
    license_id = first_id(licenses)
    if license_id:
        status, body = get(f"/v1/licenses/{license_id}")
        write("get_license.json", body, status, "License", manifest)

    (OUT / "manifest.json").write_text(json.dumps(manifest, indent=2) + "\n")
    bad = [row for row in manifest if row["status"] != 200]
    print(f"wrote {len(manifest)} goldens to {OUT}")
    if bad:
        print("non-200:", bad, file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
