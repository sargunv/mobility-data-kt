package dev.sargunv.mobilitydata.mdb.v1

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object FeedSerializer : KSerializer<Feed> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("dev.sargunv.mobilitydata.mdb.v1.Feed")

  override fun serialize(encoder: Encoder, value: Feed) {
    val output = encoder as JsonEncoder
    val element =
      when (value) {
        is Feed.Gtfs ->
          output.json.encodeToJsonElement(Feed.Gtfs.serializer(), value).withDataType("gtfs")
        is Feed.GtfsRt ->
          output.json.encodeToJsonElement(Feed.GtfsRt.serializer(), value).withDataType("gtfs_rt")
        is Feed.Gbfs ->
          output.json.encodeToJsonElement(Feed.Gbfs.serializer(), value).withDataType("gbfs")
        is Feed.Unknown -> output.json.encodeToJsonElement(Feed.Unknown.serializer(), value)
      }
    output.encodeJsonElement(element)
  }

  override fun deserialize(decoder: Decoder): Feed {
    val input = decoder as JsonDecoder
    val element = input.decodeJsonElement()
    val dataType = element.jsonObject["data_type"]?.jsonPrimitive?.contentOrNull
    val deserializer =
      when (dataType) {
        "gtfs" -> Feed.Gtfs.serializer()
        "gtfs_rt" -> Feed.GtfsRt.serializer()
        "gbfs" -> Feed.Gbfs.serializer()
        else -> Feed.Unknown.serializer()
      }
    return input.json.decodeFromJsonElement(deserializer, element)
  }
}

private fun JsonElement.withDataType(type: String): JsonElement {
  val contents = jsonObject.toMutableMap()
  contents["data_type"] = JsonPrimitive(type)
  return JsonObject(contents)
}
