package dev.sargunv.mobilitydata.utils.serialization

import kotlinx.datetime.LocalDate
import kotlinx.datetime.serializers.FormattedLocalDateSerializer

/**
 * Serializer for [LocalDate] using the ISO 8601 basic format (YYYYMMDD).
 *
 * Example: 20211109 for November 9th, 2021.
 */
public object IsoBasicLocalDateSerializer :
  FormattedLocalDateSerializer(
    name = "dev.sargunv.mobilitydata.utils.serialization.IsoBasicLocalDateSerializer",
    format = LocalDate.Formats.ISO_BASIC,
  )
