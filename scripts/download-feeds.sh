#!/bin/bash

# Script to download all feeds from a discovery URL
# Usage: ./download-feeds.sh <discovery_url> <output_directory>

set -e

if [ $# -ne 2 ]; then
    echo "Usage: $0 <discovery_url> <output_directory>"
    echo "Example: $0 'https://data.lime.bike/api/partners/v1/gbfs/seattle/gbfs.json' ./gbfs-v1/src/ktorTest/resources/lime-seattle"
    exit 1
fi

DISCOVERY_URL="$1"
OUTPUT_DIR="$2"

# Create output directory
mkdir -p "$OUTPUT_DIR"

echo "Downloading feeds from: $DISCOVERY_URL"
echo "Output directory: $OUTPUT_DIR"

# Download the discovery file
echo "Downloading discovery file..."
DISCOVERY_FILE="$OUTPUT_DIR/gbfs.json"
curl -s "$DISCOVERY_URL" -o "$DISCOVERY_FILE"

if [ ! -f "$DISCOVERY_FILE" ]; then
    echo "Error: Failed to download discovery file"
    exit 1
fi

echo "Downloaded: gbfs.json"

# Extract feed URLs and download each feed
echo "Downloading individual feeds..."

# Parse with jq and extract feed URLs, then download each one
FEEDS=$(jq -r '.data.en.feeds[] | "\(.name) \(.url)"' "$DISCOVERY_FILE")

while IFS= read -r line; do
    if [ -n "$line" ]; then
        NAME=$(echo "$line" | cut -d' ' -f1)
        URL=$(echo "$line" | cut -d' ' -f2-)

        echo "Downloading feed: $NAME"
        OUTPUT_FILE="$OUTPUT_DIR/${NAME}.json"

        if curl -s "$URL" -o "$OUTPUT_FILE"; then
            echo "  ✓ Downloaded: ${NAME}.json"
        else
            echo "  ✗ Failed to download: $NAME from $URL"
        fi
    fi
done <<< "$FEEDS"

echo ""
echo "Download complete!"
echo "Files downloaded to: $OUTPUT_DIR"
ls -la "$OUTPUT_DIR"
