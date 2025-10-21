#!/bin/bash
# Script to download and extract Sound Transit GTFS data

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
URL="https://gtfs.sound.obaweb.org/prod/gtfs_puget_sound_consolidated.zip"
ZIP_FILE="$SCRIPT_DIR/gtfs_puget_sound_consolidated.zip"

echo "Downloading GTFS data from Sound Transit..."
curl -L -o "$ZIP_FILE" "$URL"

echo "Extracting files..."
unzip -o "$ZIP_FILE" -d "$SCRIPT_DIR"

echo "Cleaning up..."
rm "$ZIP_FILE"

echo "Done! GTFS data has been extracted to $SCRIPT_DIR"
echo "Files:"
ls -lh "$SCRIPT_DIR"/*.txt
