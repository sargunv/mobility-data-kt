package dev.sargunv.mobilitydata.utils

import dev.sargunv.mobilitydata.utils.serialization.IntBooleanSerializer
import kotlinx.serialization.Serializable

/** Boolean value represented as an integer (1 for true, 0 for false). */
public typealias IntBoolean = @Serializable(with = IntBooleanSerializer::class) Boolean
