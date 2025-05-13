package net.techandgraphics.wastemanagement.data.local.database.convertor

import androidx.room.TypeConverter
import net.techandgraphics.wastemanagement.data.local.database.enums.Title

class TitleConvertor {
  @TypeConverter fun fromMessage(title: Title) = title.name

  @TypeConverter fun toMessage(value: String): Title = enumValueOf(value)
}
