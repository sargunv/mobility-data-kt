package dev.sargunv.mobilitydata.utils

import de.westnordost.osm_opening_hours.model.OpeningHours
import de.westnordost.osm_opening_hours.parser.toOpeningHours
import dev.sargunv.mobilitydata.utils.serialization.OsmOpeningHoursSerializer
import kotlinx.serialization.Serializable

/**
 * Opening hours in the [OSM opening_hours](https://wiki.openstreetmap.org/wiki/Key:opening_hours)
 * format.
 *
 * This is [OpeningHours] from
 * [osm-opening-hours](https://github.com/westnordost/osm-opening-hours), serialized as an OSM
 * opening hours string. Decoding uses lenient parsing so values commonly found in GBFS feeds
 * (single-digit month days, `PH` mixed with weekdays) are accepted.
 */
public typealias OsmOpeningHours =
  @Serializable(with = OsmOpeningHoursSerializer::class) OpeningHours

/**
 * Parses [value] as OSM opening hours.
 *
 * @param value OSM opening hours string
 * @param lenient when `true` (the default), accept unambiguous syntax that is common in GBFS feeds
 *   but not strictly valid per the specification
 */
public fun OsmOpeningHours(value: String, lenient: Boolean = true): OpeningHours =
  value.toOpeningHours(lenient)
