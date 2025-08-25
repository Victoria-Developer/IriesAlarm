package com.iries.alarm.domain.constants

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DayIdSerializer::class)
enum class Day(var id: Int, var weekName: String?) {
    SUNDAY(1, "Sunday"),
    MONDAY(2, "Monday"),
    TUESDAY(3, "Tuesday"),
    WEDNESDAY(4, "Wednesday"),
    THURSDAY(5, "Thursday"),
    FRIDAY(6, "Friday"),
    SATURDAY(7, "Saturday");

    companion object {
        fun getById(id: Int): Day {
            return entries.first { it.id == id }
        }
    }
}

object DayIdSerializer : KSerializer<Day> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Day", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Day) {
        encoder.encodeInt(value.id)
    }

    override fun deserialize(decoder: Decoder): Day {
        val id = decoder.decodeInt()
        return Day.getById(id)
    }
}