@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Decodes proto2 enums so an unrecognized number does not abort the message.
 *
 * Official proto2 treats an unknown enum occurrence as an unknown field: it does not overwrite a
 * previously recognized value, and an unknown-only field stays at the property default. Numeric
 * varint handling is protobuf-only; other formats keep the generated enum serializer.
 */
internal fun <T : Enum<T>> proto2EnumSerializer(
  generated: KSerializer<T>,
  values: List<T>,
  fallback: T,
): KSerializer<T> {
  val byNumber = protoNumberMap(generated, values)
  return object : KSerializer<T> {
    override val descriptor: SerialDescriptor = generated.descriptor

    override fun serialize(encoder: Encoder, value: T) {
      generated.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): T {
      if (!decoder.isProtobuf()) return generated.deserialize(decoder)
      val known = byNumber[decoder.decodeInt()]
      return if (known != null) {
        Proto2EnumDecodeSession.remember(decoder, this, known)
      } else {
        Proto2EnumDecodeSession.previous(decoder, this) ?: fallback
      }
    }
  }
}

internal fun <T : Enum<T>> proto2NullableEnumSerializer(
  generated: KSerializer<T>,
  values: List<T>,
): KSerializer<T?> {
  val byNumber = protoNumberMap(generated, values)
  return object : KSerializer<T?> {
    override val descriptor: SerialDescriptor = generated.descriptor

    override fun serialize(encoder: Encoder, value: T?) {
      generated.serialize(encoder, checkNotNull(value))
    }

    override fun deserialize(decoder: Decoder): T? {
      if (!decoder.isProtobuf()) return generated.deserialize(decoder)
      val known = byNumber[decoder.decodeInt()]
      return if (known != null) {
        Proto2EnumDecodeSession.remember(decoder, this, known)
      } else {
        Proto2EnumDecodeSession.previous(decoder, this)
      }
    }
  }
}

private fun <T : Enum<T>> protoNumberMap(generated: KSerializer<T>, values: List<T>): Map<Int, T> {
  val descriptor = generated.descriptor
  val byName = values.associateBy { it.name }
  return buildMap {
    for (index in 0 until descriptor.elementsCount) {
      val value = byName.getValue(descriptor.getElementName(index))
      val number =
        descriptor
          .getElementAnnotations(index)
          .filterIsInstance<ProtoNumber>()
          .singleOrNull()
          ?.number ?: index
      put(number, value)
    }
  }
}

private fun Decoder.isProtobuf(): Boolean = this::class.simpleName.orEmpty().contains("Protobuf")

internal object Proto2EnumDecodeSession {
  private val lastKnown = mutableListOf<LastKnown>()

  fun <T> remember(decoder: Decoder, serializer: Any, value: T): T {
    val existing = lastKnown.indexOfFirst { it.decoder === decoder && it.serializer === serializer }
    if (existing >= 0) {
      lastKnown[existing] = LastKnown(decoder, serializer, value)
    } else {
      lastKnown += LastKnown(decoder, serializer, value)
    }
    return value
  }

  @Suppress("UNCHECKED_CAST")
  fun <T> previous(decoder: Decoder, serializer: Any): T? =
    lastKnown.firstOrNull { it.decoder === decoder && it.serializer === serializer }?.value as T?

  fun clear() {
    lastKnown.clear()
  }

  private class LastKnown(val decoder: Decoder, val serializer: Any, val value: Any?)
}

internal object IncrementalitySerializer :
  KSerializer<FeedHeader.Incrementality> by proto2EnumSerializer(
    generated = FeedHeader.Incrementality.serializer(),
    values = FeedHeader.Incrementality.entries,
    fallback = FeedHeader.Incrementality.FullDataset,
  )

internal object CauseSerializer :
  KSerializer<Alert.Cause> by proto2EnumSerializer(
    generated = Alert.Cause.serializer(),
    values = Alert.Cause.entries,
    fallback = Alert.Cause.UnknownCause,
  )

internal object EffectSerializer :
  KSerializer<Alert.Effect> by proto2EnumSerializer(
    generated = Alert.Effect.serializer(),
    values = Alert.Effect.entries,
    fallback = Alert.Effect.UnknownEffect,
  )

internal object SeverityLevelSerializer :
  KSerializer<Alert.SeverityLevel> by proto2EnumSerializer(
    generated = Alert.SeverityLevel.serializer(),
    values = Alert.SeverityLevel.entries,
    fallback = Alert.SeverityLevel.UnknownSeverity,
  )

internal object TripScheduleRelationshipSerializer :
  KSerializer<TripDescriptor.ScheduleRelationship> by proto2EnumSerializer(
    generated = TripDescriptor.ScheduleRelationship.serializer(),
    values = TripDescriptor.ScheduleRelationship.entries,
    fallback = TripDescriptor.ScheduleRelationship.Scheduled,
  )

internal object StopTimeScheduleRelationshipSerializer :
  KSerializer<TripUpdate.StopTimeUpdate.ScheduleRelationship> by proto2EnumSerializer(
    generated = TripUpdate.StopTimeUpdate.ScheduleRelationship.serializer(),
    values = TripUpdate.StopTimeUpdate.ScheduleRelationship.entries,
    fallback = TripUpdate.StopTimeUpdate.ScheduleRelationship.Scheduled,
  )

internal object PickupTypeSerializer :
  KSerializer<
    TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType?
  > by proto2NullableEnumSerializer(
    generated = TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType.serializer(),
    values = TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType.entries,
  )

internal object DropOffTypeSerializer :
  KSerializer<
    TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType?
  > by proto2NullableEnumSerializer(
    generated = TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType.serializer(),
    values = TripUpdate.StopTimeUpdate.StopTimeProperties.DropOffPickupType.entries,
  )

internal object WheelchairAccessibleSerializer :
  KSerializer<VehicleDescriptor.WheelchairAccessible> by proto2EnumSerializer(
    generated = VehicleDescriptor.WheelchairAccessible.serializer(),
    values = VehicleDescriptor.WheelchairAccessible.entries,
    fallback = VehicleDescriptor.WheelchairAccessible.NoValue,
  )

internal object WheelchairBoardingSerializer :
  KSerializer<Stop.WheelchairBoarding> by proto2EnumSerializer(
    generated = Stop.WheelchairBoarding.serializer(),
    values = Stop.WheelchairBoarding.entries,
    fallback = Stop.WheelchairBoarding.Unknown,
  )

internal object VehicleStopStatusSerializer :
  KSerializer<VehiclePosition.VehicleStopStatus> by proto2EnumSerializer(
    generated = VehiclePosition.VehicleStopStatus.serializer(),
    values = VehiclePosition.VehicleStopStatus.entries,
    fallback = VehiclePosition.VehicleStopStatus.InTransitTo,
  )

internal object NullableCongestionLevelSerializer :
  KSerializer<VehiclePosition.CongestionLevel?> by proto2NullableEnumSerializer(
    generated = VehiclePosition.CongestionLevel.serializer(),
    values = VehiclePosition.CongestionLevel.entries,
  )

internal object OccupancyStatusSerializer :
  KSerializer<VehiclePosition.OccupancyStatus> by proto2EnumSerializer(
    generated = VehiclePosition.OccupancyStatus.serializer(),
    values = VehiclePosition.OccupancyStatus.entries,
    fallback = VehiclePosition.OccupancyStatus.NoDataAvailable,
  )

internal object NullableOccupancyStatusSerializer :
  KSerializer<VehiclePosition.OccupancyStatus?> by proto2NullableEnumSerializer(
    generated = VehiclePosition.OccupancyStatus.serializer(),
    values = VehiclePosition.OccupancyStatus.entries,
  )
