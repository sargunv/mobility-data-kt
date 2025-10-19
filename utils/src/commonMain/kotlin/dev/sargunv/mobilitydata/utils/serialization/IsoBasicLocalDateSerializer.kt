package dev.sargunv.mobilitydata.utils.serialization

import kotlinx.datetime.LocalDate
import kotlinx.datetime.serializers.FormattedLocalDateSerializer

public object IsoBasicLocalDateSerializer :
  FormattedLocalDateSerializer(
    name = "dev.sargunv.mobilitydata.utils.serialization.IsoBasicLocalDateSerializer",
    format = LocalDate.Formats.ISO_BASIC,
  )
