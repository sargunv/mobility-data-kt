package dev.sargunv.mobilitydata.gbfs.v2

/**
 * A string that identifies that particular entity of type [T]. An ID:
 * - MUST be unique within like fields (for example, `Id<Station>` MUST be unique among stations).
 * - Does not have to be globally unique, unless otherwise specified.
 * - MUST NOT contain spaces.
 * - MUST be persistent for a given entity ([Station], [PricingPlan], etc).
 * - An exception is floating bike [Bike.bikeId], which MUST NOT be persistent for privacy reasons
 */
public typealias Id<@Suppress("unused") T> = String
