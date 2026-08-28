@file:OptIn(ExperimentalSerializationApi::class)

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Decodes proto2 enums so an unrecognized number does not abort the message.
 *
 * Official proto2 treats an unknown enum value as missing and leaves the field at its default.
 * These serializers consume the varint and fall back the same way: non-null properties use their
 * Kotlin default, and already-nullable properties become null.
 */
internal fun <T : Enum<T>> proto2EnumSerializer(
  generated: KSerializer<T>,
  values: List<T>,
  fallback: T,
): KSerializer<T> {
  val byNumber = protoNumberMap(generated, values)
  val byValue = byNumber.entries.associate { (number, value) -> value to number }
  return object : KSerializer<T> {
    override val descriptor: SerialDescriptor =
      PrimitiveSerialDescriptor(generated.descriptor.serialName, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: T) {
      encoder.encodeInt(byValue.getValue(value))
    }

    override fun deserialize(decoder: Decoder): T = byNumber[decoder.decodeInt()] ?: fallback
  }
}

internal fun <T : Enum<T>> proto2NullableEnumSerializer(
  generated: KSerializer<T>,
  values: List<T>,
): KSerializer<T?> {
  val byNumber = protoNumberMap(generated, values)
  val byValue = byNumber.entries.associate { (number, value) -> value to number }
  return object : KSerializer<T?> {
    override val descriptor: SerialDescriptor =
      PrimitiveSerialDescriptor(generated.descriptor.serialName, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: T?) {
      encoder.encodeInt(byValue.getValue(checkNotNull(value)))
    }

    override fun deserialize(decoder: Decoder): T? = byNumber[decoder.decodeInt()]
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

internal object NullableDropOffPickupTypeSerializer :
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
