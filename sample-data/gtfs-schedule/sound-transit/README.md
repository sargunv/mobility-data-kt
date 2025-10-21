# Sound Transit GTFS Data

This directory should contain the GTFS data files from Sound Transit's
consolidated feed.

## Source

https://gtfs.sound.obaweb.org/prod/gtfs_puget_sound_consolidated.zip

## Instructions

1. Download the ZIP file from the URL above
2. Extract all files to this directory
3. The files should be named with .txt extension (e.g., agency.txt, routes.txt,
   etc.)

## Expected Files

At minimum, the following files are needed for current tests:

- agency.txt
- routes.txt
- trips.txt
- stops.txt
- stop_times.txt
- calendar.txt
- calendar_dates.txt

Additional files from the ZIP should be kept for future implementation.
