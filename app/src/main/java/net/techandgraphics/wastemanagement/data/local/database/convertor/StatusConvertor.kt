package net.techandgraphics.wastemanagement.data.local.database.convertor

import androidx.room.TypeConverter
import net.techandgraphics.wastemanagement.data.local.database.enums.Status

class StatusConvertor {
  @TypeConverter fun fromStatus(status: Status) = status.name

  @TypeConverter fun toStatus(value: String): Status = enumValueOf(value)
}
