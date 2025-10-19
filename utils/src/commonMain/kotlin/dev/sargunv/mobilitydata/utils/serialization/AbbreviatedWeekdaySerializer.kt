package dev.sargunv.mobilitydata.utils.serialization

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Serializer for [DayOfWeek] that uses abbreviated weekday names (mon, tue, wed, etc.). */
public class AbbreviatedWeekdaySerializer : KSerializer<DayOfWeek> {
  private val delegate = String.serializer()
  override val descriptor: SerialDescriptor = delegate.descriptor

  override fun serialize(encoder: Encoder, value: DayOfWeek): Unit =
    delegate.serialize(
      encoder,
      when (value) {
        DayOfWeek.MONDAY -> "mon"
        DayOfWeek.TUESDAY -> "tue"
        DayOfWeek.WEDNESDAY -> "wed"
        DayOfWeek.THURSDAY -> "thu"
        DayOfWeek.FRIDAY -> "fri"
        DayOfWeek.SATURDAY -> "sat"
        DayOfWeek.SUNDAY -> "sun"
      },
    )

  override fun deserialize(decoder: Decoder): DayOfWeek =
    when (delegate.deserialize(decoder)) {
      "mon" -> DayOfWeek.MONDAY
      "tue" -> DayOfWeek.TUESDAY
      "wed" -> DayOfWeek.WEDNESDAY
      "thu" -> DayOfWeek.THURSDAY
      "fri" -> DayOfWeek.FRIDAY
      "sat" -> DayOfWeek.SATURDAY
      "sun" -> DayOfWeek.SUNDAY
      else -> throw IllegalArgumentException("Invalid abbreviated weekday")
    }
}
