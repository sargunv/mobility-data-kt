package dev.sargunv.mobilitydata.gtfs.schedule

import dev.sargunv.kotlindsv.Csv
import dev.sargunv.kotlindsv.DsvFormat

/**
 * CSV format configuration for GTFS files.
 *
 * GTFS uses standard CSV format with header rows and comma delimiters.
 */
public val GtfsCsv: DsvFormat = Csv
