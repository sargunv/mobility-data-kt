@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException

private const val VARINT = 0
private const val FIXED64 = 1
private const val LEN = 2
private const val START_GROUP = 3
private const val END_GROUP = 4
private const val FIXED32 = 5

/**
 * Hand-written proto2 decoder for the GTFS Realtime model, reading straight from the input array.
 *
 * Each `read` function mirrors one data class. Its `when` matches the whole tag, `field shl 3 or
 * wireType`, so an occurrence with an unexpected wire type falls through to [skipField] like an
 * unknown field does. Unrecognized enum numbers are ignored, so the last recognized occurrence
 * wins. Singular fields take the last occurrence, embedded messages included (they are replaced,
 * not merged, as kotlinx does), and repeated fields append. Malformed input and missing required
 * fields throw a [SerializationException].
 *
 * Keep this in sync with the data classes: a field added there must be read here too.
 */
internal class GtfsRealtimeDecoder private constructor(private val bytes: ByteArray) {
  private var offset = 0
  private var limit = bytes.size

  private fun readFeedMessage(): FeedMessage {
    var header: FeedHeader? = null
    var entity: MutableList<FeedEntity>? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> header = readMessage(::readFeedHeader)
        2 shl 3 or LEN -> entity = entity.append(readMessage(::readFeedEntity))
        else -> skipField(tag)
      }
    }
    return FeedMessage(
      header = header ?: missing("header", FeedMessage.serializer()),
      entity = entity ?: emptyList(),
    )
  }

  private fun readFeedHeader(): FeedHeader {
    var gtfsRealtimeVersion: String? = null
    var incrementality = FeedHeader.Incrementality.FullDataset
    var timestamp: Long? = null
    var feedVersion: String? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> gtfsRealtimeVersion = readString()
        2 shl 3 or VARINT -> incrementality = incrementalities[readInt32()] ?: incrementality
        3 shl 3 or VARINT -> timestamp = readInt64()
        4 shl 3 or LEN -> feedVersion = readString()
        else -> skipField(tag)
      }
    }
    return FeedHeader(
      gtfsRealtimeVersion =
        gtfsRealtimeVersion ?: missing("gtfsRealtimeVersion", FeedHeader.serializer()),
      incrementality = incrementality,
      timestamp = timestamp,
      feedVersion = feedVersion,
    )
  }

  private fun readFeedEntity(): FeedEntity {
    var id: String? = null
    var isDeleted = false
    var tripUpdate: TripUpdate? = null
    var vehicle: VehiclePosition? = null
    var alert: Alert? = null
    var shape: Shape? = null
    var stop: Stop? = null
    var tripModifications: TripModifications? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> id = readString()
        2 shl 3 or VARINT -> isDeleted = readBool()
        3 shl 3 or LEN -> tripUpdate = readMessage(::readTripUpdate)
        4 shl 3 or LEN -> vehicle = readMessage(::readVehiclePosition)
        5 shl 3 or LEN -> alert = readMessage(::readAlert)
        6 shl 3 or LEN -> shape = readMessage(::readShape)
        7 shl 3 or LEN -> stop = readMessage(::readStop)
        8 shl 3 or LEN -> tripModifications = readMessage(::readTripModifications)
        else -> skipField(tag)
      }
    }
    return FeedEntity(
      id = id ?: missing("id", FeedEntity.serializer()),
      isDeleted = isDeleted,
      tripUpdate = tripUpdate,
      vehicle = vehicle,
      alert = alert,
      shape = shape,
      stop = stop,
      tripModifications = tripModifications,
    )
  }

  private fun readTripUpdate(): TripUpdate {
    var trip: TripDescriptor? = null
    var vehicle: VehicleDescriptor? = null
    var stopTimeUpdate: MutableList<TripUpdate.StopTimeUpdate>? = null
    var timestamp: Long? = null
    var delay: Int? = null
    var tripProperties: TripUpdate.TripProperties? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> trip = readMessage(::readTripDescriptor)
        3 shl 3 or LEN -> vehicle = readMessage(::readVehicleDescriptor)
        2 shl 3 or LEN -> stopTimeUpdate = stopTimeUpdate.append(readMessage(::readStopTimeUpdate))
        4 shl 3 or VARINT -> timestamp = readInt64()
        5 shl 3 or VARINT -> delay = readInt32()
        6 shl 3 or LEN -> tripProperties = readMessage(::readTripProperties)
        else -> skipField(tag)
      }
    }
    return TripUpdate(
      trip = trip ?: missing("trip", TripUpdate.serializer()),
      vehicle = vehicle,
      stopTimeUpdate = stopTimeUpdate ?: emptyList(),
      timestamp = timestamp,
      delay = delay,
      tripProperties = tripProperties,
    )
  }

  private fun readStopTimeEvent(): TripUpdate.StopTimeEvent {
    var delay: Int? = null
    var time: Long? = null
    var uncertainty: Int? = null
    var scheduledTime: Long? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or VARINT -> delay = readInt32()
        2 shl 3 or VARINT -> time = readInt64()
        3 shl 3 or VARINT -> uncertainty = readInt32()
        4 shl 3 or VARINT -> scheduledTime = readInt64()
        else -> skipField(tag)
      }
    }
    return TripUpdate.StopTimeEvent(
      delay = delay,
      time = time,
      uncertainty = uncertainty,
      scheduledTime = scheduledTime,
    )
  }

  private fun readStopTimeUpdate(): TripUpdate.StopTimeUpdate {
    var stopSequence: Int? = null
    var stopId: String? = null
    var arrival: TripUpdate.StopTimeEvent? = null
    var departure: TripUpdate.StopTimeEvent? = null
    var departureOccupancyStatus: VehiclePosition.OccupancyStatus? = null
    var scheduleRelationship = TripUpdate.StopTimeUpdate.ScheduleRelationship.Scheduled
    var stopTimeProperties: TripUpdate.StopTimeUpdate.StopTimeProperties? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or VARINT -> stopSequence = readInt32()
        4 shl 3 or LEN -> stopId = readString()
        2 shl 3 or LEN -> arrival = readMessage(::readStopTimeEvent)
        3 shl 3 or LEN -> departure = readMessage(::readStopTimeEvent)
        7 shl 3 or VARINT ->
          departureOccupancyStatus = occupancyStatuses[readInt32()] ?: departureOccupancyStatus
        5 shl 3 or VARINT ->
          scheduleRelationship = stopTimeScheduleRelationships[readInt32()] ?: scheduleRelationship
        6 shl 3 or LEN -> stopTimeProperties = readMessage(::readStopTimeProperties)
        else -> skipField(tag)
      }
    }
    return TripUpdate.StopTimeUpdate(
      stopSequence = stopSequence,
      stopId = stopId,
      arrival = arrival,
      departure = departure,
      departureOccupancyStatus = departureOccupancyStatus,
      scheduleRelationship = scheduleRelationship,
      stopTimeProperties = stopTimeProperties,
    )
  }

  private fun readStopTimeProperties(): TripUpdate.StopTimeUpdate.StopTimeProperties {
    var assignedStopId: String? = null
    var stopHeadsign: String? = null
    var pickupType: TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType? = null
    var dropOffType: TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> assignedStopId = readString()
        2 shl 3 or LEN -> stopHeadsign = readString()
        3 shl 3 or VARINT -> pickupType = dropOffPickupTypes[readInt32()] ?: pickupType
        4 shl 3 or VARINT -> dropOffType = dropOffPickupTypes[readInt32()] ?: dropOffType
        else -> skipField(tag)
      }
    }
    return TripUpdate.StopTimeUpdate.StopTimeProperties(
      assignedStopId = assignedStopId,
      stopHeadsign = stopHeadsign,
      pickupType = pickupType,
      dropOffType = dropOffType,
    )
  }

  private fun readTripProperties(): TripUpdate.TripProperties {
    var tripId: String? = null
    var startDate: String? = null
    var startTime: String? = null
    var shapeId: String? = null
    var tripHeadsign: String? = null
    var tripShortName: String? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> tripId = readString()
        2 shl 3 or LEN -> startDate = readString()
        3 shl 3 or LEN -> startTime = readString()
        4 shl 3 or LEN -> shapeId = readString()
        5 shl 3 or LEN -> tripHeadsign = readString()
        6 shl 3 or LEN -> tripShortName = readString()
        else -> skipField(tag)
      }
    }
    return TripUpdate.TripProperties(
      tripId = tripId,
      startDate = startDate,
      startTime = startTime,
      shapeId = shapeId,
      tripHeadsign = tripHeadsign,
      tripShortName = tripShortName,
    )
  }

  private fun readTripDescriptor(): TripDescriptor {
    var tripId: String? = null
    var routeId: String? = null
    var directionId: Int? = null
    var startTime: String? = null
    var startDate: String? = null
    var scheduleRelationship: TripDescriptor.ScheduleRelationship? = null
    var modifiedTrip: TripDescriptor.ModifiedTripSelector? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> tripId = readString()
        5 shl 3 or LEN -> routeId = readString()
        6 shl 3 or VARINT -> directionId = readInt32()
        2 shl 3 or LEN -> startTime = readString()
        3 shl 3 or LEN -> startDate = readString()
        4 shl 3 or VARINT ->
          scheduleRelationship = tripScheduleRelationships[readInt32()] ?: scheduleRelationship
        7 shl 3 or LEN -> modifiedTrip = readMessage(::readModifiedTripSelector)
        else -> skipField(tag)
      }
    }
    return TripDescriptor(
      tripId = tripId,
      routeId = routeId,
      directionId = directionId,
      startTime = startTime,
      startDate = startDate,
      scheduleRelationship = scheduleRelationship,
      modifiedTrip = modifiedTrip,
    )
  }

  private fun readModifiedTripSelector(): TripDescriptor.ModifiedTripSelector {
    var modificationsId: String? = null
    var affectedTripId: String? = null
    var startTime: String? = null
    var startDate: String? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> modificationsId = readString()
        2 shl 3 or LEN -> affectedTripId = readString()
        3 shl 3 or LEN -> startTime = readString()
        4 shl 3 or LEN -> startDate = readString()
        else -> skipField(tag)
      }
    }
    return TripDescriptor.ModifiedTripSelector(
      modificationsId = modificationsId,
      affectedTripId = affectedTripId,
      startTime = startTime,
      startDate = startDate,
    )
  }

  private fun readVehicleDescriptor(): VehicleDescriptor {
    var id: String? = null
    var label: String? = null
    var licensePlate: String? = null
    var wheelchairAccessible = VehicleDescriptor.WheelchairAccessible.NoValue
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> id = readString()
        2 shl 3 or LEN -> label = readString()
        3 shl 3 or LEN -> licensePlate = readString()
        4 shl 3 or VARINT ->
          wheelchairAccessible = wheelchairAccessibles[readInt32()] ?: wheelchairAccessible
        else -> skipField(tag)
      }
    }
    return VehicleDescriptor(
      id = id,
      label = label,
      licensePlate = licensePlate,
      wheelchairAccessible = wheelchairAccessible,
    )
  }

  private fun readShape(): Shape {
    var shapeId: String? = null
    var encodedPolyline: String? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> shapeId = readString()
        2 shl 3 or LEN -> encodedPolyline = readString()
        else -> skipField(tag)
      }
    }
    return Shape(shapeId = shapeId, encodedPolyline = encodedPolyline)
  }

  private fun readStop(): Stop {
    var stopId: String? = null
    var stopCode: TranslatedString? = null
    var stopName: TranslatedString? = null
    var ttsStopName: TranslatedString? = null
    var stopDesc: TranslatedString? = null
    var stopLat: Float? = null
    var stopLon: Float? = null
    var zoneId: String? = null
    var stopUrl: TranslatedString? = null
    var parentStation: String? = null
    var stopTimezone: String? = null
    var wheelchairBoarding = Stop.WheelchairBoarding.Unknown
    var levelId: String? = null
    var platformCode: TranslatedString? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> stopId = readString()
        2 shl 3 or LEN -> stopCode = readMessage(::readTranslatedString)
        3 shl 3 or LEN -> stopName = readMessage(::readTranslatedString)
        4 shl 3 or LEN -> ttsStopName = readMessage(::readTranslatedString)
        5 shl 3 or LEN -> stopDesc = readMessage(::readTranslatedString)
        6 shl 3 or FIXED32 -> stopLat = readFloat()
        7 shl 3 or FIXED32 -> stopLon = readFloat()
        8 shl 3 or LEN -> zoneId = readString()
        9 shl 3 or LEN -> stopUrl = readMessage(::readTranslatedString)
        11 shl 3 or LEN -> parentStation = readString()
        12 shl 3 or LEN -> stopTimezone = readString()
        13 shl 3 or VARINT ->
          wheelchairBoarding = wheelchairBoardings[readInt32()] ?: wheelchairBoarding
        14 shl 3 or LEN -> levelId = readString()
        15 shl 3 or LEN -> platformCode = readMessage(::readTranslatedString)
        else -> skipField(tag)
      }
    }
    return Stop(
      stopId = stopId,
      stopCode = stopCode,
      stopName = stopName,
      ttsStopName = ttsStopName,
      stopDesc = stopDesc,
      stopLat = stopLat,
      stopLon = stopLon,
      zoneId = zoneId,
      stopUrl = stopUrl,
      parentStation = parentStation,
      stopTimezone = stopTimezone,
      wheelchairBoarding = wheelchairBoarding,
      levelId = levelId,
      platformCode = platformCode,
    )
  }

  private fun readTripModifications(): TripModifications {
    var selectedTrips: MutableList<TripModifications.SelectedTrips>? = null
    var startTimes: MutableList<String>? = null
    var serviceDates: MutableList<String>? = null
    var modifications: MutableList<TripModifications.Modification>? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> selectedTrips = selectedTrips.append(readMessage(::readSelectedTrips))
        2 shl 3 or LEN -> startTimes = startTimes.append(readString())
        3 shl 3 or LEN -> serviceDates = serviceDates.append(readString())
        4 shl 3 or LEN -> modifications = modifications.append(readMessage(::readModification))
        else -> skipField(tag)
      }
    }
    return TripModifications(
      selectedTrips = selectedTrips ?: emptyList(),
      startTimes = startTimes ?: emptyList(),
      serviceDates = serviceDates ?: emptyList(),
      modifications = modifications ?: emptyList(),
    )
  }

  private fun readSelectedTrips(): TripModifications.SelectedTrips {
    var tripIds: MutableList<String>? = null
    var shapeId: String? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> tripIds = tripIds.append(readString())
        2 shl 3 or LEN -> shapeId = readString()
        else -> skipField(tag)
      }
    }
    return TripModifications.SelectedTrips(tripIds = tripIds ?: emptyList(), shapeId = shapeId)
  }

  private fun readModification(): TripModifications.Modification {
    var startStopSelector: StopSelector? = null
    var endStopSelector: StopSelector? = null
    var propagatedModificationDelay = 0
    var replacementStops: MutableList<ReplacementStop>? = null
    var serviceAlertId: String? = null
    var lastModifiedTime: Long? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> startStopSelector = readMessage(::readStopSelector)
        2 shl 3 or LEN -> endStopSelector = readMessage(::readStopSelector)
        3 shl 3 or VARINT -> propagatedModificationDelay = readInt32()
        4 shl 3 or LEN ->
          replacementStops = replacementStops.append(readMessage(::readReplacementStop))
        5 shl 3 or LEN -> serviceAlertId = readString()
        6 shl 3 or VARINT -> lastModifiedTime = readInt64()
        else -> skipField(tag)
      }
    }
    return TripModifications.Modification(
      startStopSelector = startStopSelector,
      endStopSelector = endStopSelector,
      propagatedModificationDelay = propagatedModificationDelay,
      replacementStops = replacementStops ?: emptyList(),
      serviceAlertId = serviceAlertId,
      lastModifiedTime = lastModifiedTime,
    )
  }

  private fun readStopSelector(): StopSelector {
    var stopSequence: Int? = null
    var stopId: String? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or VARINT -> stopSequence = readInt32()
        2 shl 3 or LEN -> stopId = readString()
        else -> skipField(tag)
      }
    }
    return StopSelector(stopSequence = stopSequence, stopId = stopId)
  }

  private fun readReplacementStop(): ReplacementStop {
    var travelTimeToStop: Int? = null
    var stopId: String? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or VARINT -> travelTimeToStop = readInt32()
        2 shl 3 or LEN -> stopId = readString()
        else -> skipField(tag)
      }
    }
    return ReplacementStop(travelTimeToStop = travelTimeToStop, stopId = stopId)
  }

  private fun readVehiclePosition(): VehiclePosition {
    var trip: TripDescriptor? = null
    var vehicle: VehicleDescriptor? = null
    var position: Position? = null
    var currentStopSequence: Int? = null
    var stopId: String? = null
    var currentStatus: VehiclePosition.VehicleStopStatus? = null
    var timestamp: Long? = null
    var congestionLevel: VehiclePosition.CongestionLevel? = null
    var occupancyStatus: VehiclePosition.OccupancyStatus? = null
    var occupancyPercentage: Int? = null
    var multiCarriageDetails: MutableList<VehiclePosition.CarriageDetails>? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> trip = readMessage(::readTripDescriptor)
        8 shl 3 or LEN -> vehicle = readMessage(::readVehicleDescriptor)
        2 shl 3 or LEN -> position = readMessage(::readPosition)
        3 shl 3 or VARINT -> currentStopSequence = readInt32()
        7 shl 3 or LEN -> stopId = readString()
        4 shl 3 or VARINT -> currentStatus = vehicleStopStatuses[readInt32()] ?: currentStatus
        5 shl 3 or VARINT -> timestamp = readInt64()
        6 shl 3 or VARINT -> congestionLevel = congestionLevels[readInt32()] ?: congestionLevel
        9 shl 3 or VARINT -> occupancyStatus = occupancyStatuses[readInt32()] ?: occupancyStatus
        10 shl 3 or VARINT -> occupancyPercentage = readInt32()
        11 shl 3 or LEN ->
          multiCarriageDetails = multiCarriageDetails.append(readMessage(::readCarriageDetails))
        else -> skipField(tag)
      }
    }
    return VehiclePosition(
      trip = trip,
      vehicle = vehicle,
      position = position,
      currentStopSequence = currentStopSequence,
      stopId = stopId,
      currentStatus = currentStatus,
      timestamp = timestamp,
      congestionLevel = congestionLevel,
      occupancyStatus = occupancyStatus,
      occupancyPercentage = occupancyPercentage,
      multiCarriageDetails = multiCarriageDetails ?: emptyList(),
    )
  }

  private fun readCarriageDetails(): VehiclePosition.CarriageDetails {
    var id: String? = null
    var label: String? = null
    var occupancyStatus = VehiclePosition.OccupancyStatus.NoDataAvailable
    var occupancyPercentage = -1
    var carriageSequence: Int? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> id = readString()
        2 shl 3 or LEN -> label = readString()
        3 shl 3 or VARINT -> occupancyStatus = occupancyStatuses[readInt32()] ?: occupancyStatus
        4 shl 3 or VARINT -> occupancyPercentage = readInt32()
        5 shl 3 or VARINT -> carriageSequence = readInt32()
        else -> skipField(tag)
      }
    }
    return VehiclePosition.CarriageDetails(
      id = id,
      label = label,
      occupancyStatus = occupancyStatus,
      occupancyPercentage = occupancyPercentage,
      carriageSequence = carriageSequence,
    )
  }

  private fun readPosition(): Position {
    var latitude = 0f
    var hasLatitude = false
    var longitude = 0f
    var hasLongitude = false
    var bearing: Float? = null
    var odometer: Double? = null
    var speed: Float? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or FIXED32 -> {
          latitude = readFloat()
          hasLatitude = true
        }
        2 shl 3 or FIXED32 -> {
          longitude = readFloat()
          hasLongitude = true
        }
        3 shl 3 or FIXED32 -> bearing = readFloat()
        4 shl 3 or FIXED64 -> odometer = readDouble()
        5 shl 3 or FIXED32 -> speed = readFloat()
        else -> skipField(tag)
      }
    }
    if (!hasLatitude) missing("latitude", Position.serializer())
    if (!hasLongitude) missing("longitude", Position.serializer())
    return Position(
      latitude = latitude,
      longitude = longitude,
      bearing = bearing,
      odometer = odometer,
      speed = speed,
    )
  }

  @Suppress("DEPRECATION")
  private fun readAlert(): Alert {
    var activePeriod: MutableList<TimeRange>? = null
    var communicationPeriod: MutableList<TimeRange>? = null
    var impactPeriod: MutableList<TimeRange>? = null
    var informedEntity: MutableList<EntitySelector>? = null
    var cause = Alert.Cause.UnknownCause
    var effect = Alert.Effect.UnknownEffect
    var url: TranslatedString? = null
    var headerText: TranslatedString? = null
    var descriptionText: TranslatedString? = null
    var ttsHeaderText: TranslatedString? = null
    var ttsDescriptionText: TranslatedString? = null
    var severityLevel = Alert.SeverityLevel.UnknownSeverity
    var image: TranslatedImage? = null
    var imageAlternativeText: TranslatedString? = null
    var causeDetail: TranslatedString? = null
    var effectDetail: TranslatedString? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> activePeriod = activePeriod.append(readMessage(::readTimeRange))
        2 shl 3 or LEN ->
          communicationPeriod = communicationPeriod.append(readMessage(::readTimeRange))
        3 shl 3 or LEN -> impactPeriod = impactPeriod.append(readMessage(::readTimeRange))
        5 shl 3 or LEN -> informedEntity = informedEntity.append(readMessage(::readEntitySelector))
        6 shl 3 or VARINT -> cause = causes[readInt32()] ?: cause
        7 shl 3 or VARINT -> effect = effects[readInt32()] ?: effect
        8 shl 3 or LEN -> url = readMessage(::readTranslatedString)
        10 shl 3 or LEN -> headerText = readMessage(::readTranslatedString)
        11 shl 3 or LEN -> descriptionText = readMessage(::readTranslatedString)
        12 shl 3 or LEN -> ttsHeaderText = readMessage(::readTranslatedString)
        13 shl 3 or LEN -> ttsDescriptionText = readMessage(::readTranslatedString)
        14 shl 3 or VARINT -> severityLevel = severityLevels[readInt32()] ?: severityLevel
        15 shl 3 or LEN -> image = readMessage(::readTranslatedImage)
        16 shl 3 or LEN -> imageAlternativeText = readMessage(::readTranslatedString)
        17 shl 3 or LEN -> causeDetail = readMessage(::readTranslatedString)
        18 shl 3 or LEN -> effectDetail = readMessage(::readTranslatedString)
        else -> skipField(tag)
      }
    }
    return Alert(
      activePeriod = activePeriod ?: emptyList(),
      communicationPeriod = communicationPeriod ?: emptyList(),
      impactPeriod = impactPeriod ?: emptyList(),
      informedEntity = informedEntity ?: emptyList(),
      cause = cause,
      effect = effect,
      url = url,
      headerText = headerText,
      descriptionText = descriptionText,
      ttsHeaderText = ttsHeaderText,
      ttsDescriptionText = ttsDescriptionText,
      severityLevel = severityLevel,
      image = image,
      imageAlternativeText = imageAlternativeText,
      causeDetail = causeDetail,
      effectDetail = effectDetail,
    )
  }

  private fun readTimeRange(): TimeRange {
    var start: Long? = null
    var end: Long? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or VARINT -> start = readInt64()
        2 shl 3 or VARINT -> end = readInt64()
        else -> skipField(tag)
      }
    }
    return TimeRange(start = start, end = end)
  }

  private fun readEntitySelector(): EntitySelector {
    var agencyId: String? = null
    var routeId: String? = null
    var routeType: Int? = null
    var trip: TripDescriptor? = null
    var stopId: String? = null
    var directionId: Int? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> agencyId = readString()
        2 shl 3 or LEN -> routeId = readString()
        3 shl 3 or VARINT -> routeType = readInt32()
        4 shl 3 or LEN -> trip = readMessage(::readTripDescriptor)
        5 shl 3 or LEN -> stopId = readString()
        6 shl 3 or VARINT -> directionId = readInt32()
        else -> skipField(tag)
      }
    }
    return EntitySelector(
      agencyId = agencyId,
      routeId = routeId,
      routeType = routeType,
      trip = trip,
      stopId = stopId,
      directionId = directionId,
    )
  }

  private fun readTranslatedString(): TranslatedString {
    var translation: MutableList<TranslatedString.Translation>? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> translation = translation.append(readMessage(::readTranslation))
        else -> skipField(tag)
      }
    }
    return TranslatedString(translation = translation ?: emptyList())
  }

  private fun readTranslation(): TranslatedString.Translation {
    var text: String? = null
    var language: String? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> text = readString()
        2 shl 3 or LEN -> language = readString()
        else -> skipField(tag)
      }
    }
    return TranslatedString.Translation(
      text = text ?: missing("text", TranslatedString.Translation.serializer()),
      language = language,
    )
  }

  private fun readTranslatedImage(): TranslatedImage {
    var localizedImage: MutableList<TranslatedImage.LocalizedImage>? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> localizedImage = localizedImage.append(readMessage(::readLocalizedImage))
        else -> skipField(tag)
      }
    }
    return TranslatedImage(localizedImage = localizedImage ?: emptyList())
  }

  private fun readLocalizedImage(): TranslatedImage.LocalizedImage {
    var url: String? = null
    var mediaType: String? = null
    var language: String? = null
    while (offset < limit) {
      when (val tag = readTag()) {
        1 shl 3 or LEN -> url = readString()
        2 shl 3 or LEN -> mediaType = readString()
        3 shl 3 or LEN -> language = readString()
        else -> skipField(tag)
      }
    }
    return TranslatedImage.LocalizedImage(
      url = url ?: missing("url", TranslatedImage.LocalizedImage.serializer()),
      mediaType = mediaType ?: missing("mediaType", TranslatedImage.LocalizedImage.serializer()),
      language = language,
    )
  }

  /** Reads a length-delimited embedded message with [read], bounded by its length. */
  private inline fun <T> readMessage(read: () -> T): T {
    val length = readLength()
    val outerLimit = limit
    limit = offset + length
    val message = read()
    limit = outerLimit
    return message
  }

  /**
   * Reads a tag. Like kotlinx and protobuf-java, only the low 32 bits of an overlong tag varint
   * count.
   */
  private fun readTag(): Int = readVarint().toInt()

  /** int32 and uint32 fields keep the low 32 bits, so negative int32s arrive as 10-byte varints. */
  private fun readInt32(): Int = readVarint().toInt()

  private fun readInt64(): Long = readVarint()

  /** kotlinx accepts only 0 and 1 (after truncation to 32 bits), where proto2 reads any nonzero. */
  private fun readBool(): Boolean =
    when (val value = readVarint().toInt()) {
      0 -> false
      1 -> true
      else -> malformed("Unexpected boolean value: $value")
    }

  private fun readFloat(): Float = Float.fromBits(readFixed32())

  private fun readDouble(): Double = Double.fromBits(readFixed64())

  /** Invalid UTF-8 decodes to replacement characters rather than failing. */
  private fun readString(): String {
    val length = readLength()
    val start = offset
    offset += length
    return bytes.decodeToString(start, offset)
  }

  private fun readVarint(): Long {
    if (offset < limit) {
      val byte = bytes[offset]
      if (byte >= 0) {
        offset++
        return byte.toLong()
      }
    }
    return readVarintSlowPath()
  }

  private fun readVarintSlowPath(): Long {
    var result = 0L
    var shift = 0
    while (shift < 64) {
      if (offset >= limit) malformed("Truncated protobuf varint")
      val byte = bytes[offset++].toInt()
      result = result or ((byte and 0x7F).toLong() shl shift)
      if (byte and 0x80 == 0) return result
      shift += 7
    }
    malformed("Protobuf varint too long")
  }

  private fun readFixed32(): Int {
    requireBytes(4)
    var result = 0
    for (i in 0 until 4) result = result or ((bytes[offset++].toInt() and 0xFF) shl (8 * i))
    return result
  }

  private fun readFixed64(): Long {
    requireBytes(8)
    var result = 0L
    for (i in 0 until 8) result = result or ((bytes[offset++].toLong() and 0xFF) shl (8 * i))
    return result
  }

  private fun readLength(): Int {
    val length = readVarint()
    if (length !in 0..(limit - offset).toLong()) malformed("Truncated protobuf bytes")
    return length.toInt()
  }

  private fun skip(count: Int) {
    requireBytes(count)
    offset += count
  }

  private fun requireBytes(count: Int) {
    if (count > limit - offset) malformed("Truncated protobuf bytes")
  }

  /**
   * Skips an unknown field, or a known one with an unexpected wire type. Field number 0 is
   * rejected, except as a group, which kotlinx never saw because the old stripper dropped groups.
   */
  private fun skipField(tag: Int) {
    val wireType = tag and 7
    if (tag ushr 3 == 0 && wireType != START_GROUP) malformed("Protobuf field number 0")
    when (wireType) {
      VARINT -> readVarint()
      FIXED64 -> skip(8)
      LEN -> skip(readLength())
      FIXED32 -> skip(4)
      START_GROUP -> skipGroup(tag ushr 3)
      else -> malformed("Unexpected protobuf wire type $wireType")
    }
  }

  /** Skips a group and any groups nested in it, without recursion. */
  private fun skipGroup(number: Int) {
    var open = IntArray(4)
    open[0] = number
    var depth = 1
    while (depth > 0) {
      val tag = readTag()
      when (val wireType = tag and 7) {
        VARINT -> readVarint()
        FIXED64 -> skip(8)
        LEN -> skip(readLength())
        FIXED32 -> skip(4)
        START_GROUP -> {
          if (depth == open.size) open = open.copyOf(depth * 2)
          open[depth++] = tag ushr 3
        }
        END_GROUP -> if (open[--depth] != tag ushr 3) malformed("Mismatched protobuf end group")
        else -> malformed("Unexpected protobuf wire type $wireType")
      }
    }
  }

  internal companion object {
    private val incrementalities =
      ProtoEnumNumbers(FeedHeader.Incrementality.serializer(), FeedHeader.Incrementality.entries)
    private val causes = ProtoEnumNumbers(Alert.Cause.serializer(), Alert.Cause.entries)
    private val effects = ProtoEnumNumbers(Alert.Effect.serializer(), Alert.Effect.entries)
    private val severityLevels =
      ProtoEnumNumbers(Alert.SeverityLevel.serializer(), Alert.SeverityLevel.entries)
    private val tripScheduleRelationships =
      ProtoEnumNumbers(
        TripDescriptor.ScheduleRelationship.serializer(),
        TripDescriptor.ScheduleRelationship.entries,
      )
    private val stopTimeScheduleRelationships =
      ProtoEnumNumbers(
        TripUpdate.StopTimeUpdate.ScheduleRelationship.serializer(),
        TripUpdate.StopTimeUpdate.ScheduleRelationship.entries,
      )
    private val dropOffPickupTypes =
      ProtoEnumNumbers(
        TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType.serializer(),
        TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType.entries,
      )
    private val wheelchairAccessibles =
      ProtoEnumNumbers(
        VehicleDescriptor.WheelchairAccessible.serializer(),
        VehicleDescriptor.WheelchairAccessible.entries,
      )
    private val wheelchairBoardings =
      ProtoEnumNumbers(Stop.WheelchairBoarding.serializer(), Stop.WheelchairBoarding.entries)
    private val vehicleStopStatuses =
      ProtoEnumNumbers(
        VehiclePosition.VehicleStopStatus.serializer(),
        VehiclePosition.VehicleStopStatus.entries,
      )
    private val congestionLevels =
      ProtoEnumNumbers(
        VehiclePosition.CongestionLevel.serializer(),
        VehiclePosition.CongestionLevel.entries,
      )
    private val occupancyStatuses =
      ProtoEnumNumbers(
        VehiclePosition.OccupancyStatus.serializer(),
        VehiclePosition.OccupancyStatus.entries,
      )

    fun decodeFeedMessage(bytes: ByteArray): FeedMessage =
      GtfsRealtimeDecoder(bytes).readFeedMessage()
  }
}

/** Looks up enum values by their `@ProtoNumber`, read from the generated serializer. */
private class ProtoEnumNumbers<T : Enum<T>>(generated: KSerializer<T>, values: List<T>) {
  private val byNumber: List<T?> =
    protoNumberMap(generated, values).let { map ->
      List((map.keys.maxOrNull() ?: -1) + 1) { map[it] }
    }

  /** The value numbered [number], or null if this enum doesn't recognize it. */
  operator fun get(number: Int): T? = byNumber.getOrNull(number)
}

/** Appends [element], creating the list on the first occurrence of a repeated field. */
private fun <T> MutableList<T>?.append(element: T): MutableList<T> =
  (this ?: ArrayList()).apply { add(element) }

private fun missing(field: String, serializer: KSerializer<*>): Nothing =
  throw MissingFieldException(field, serializer.descriptor.serialName)

private fun malformed(message: String): Nothing = throw SerializationException(message)
