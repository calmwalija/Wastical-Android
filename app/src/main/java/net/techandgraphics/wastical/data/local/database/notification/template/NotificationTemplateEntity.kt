package net.techandgraphics.wastical.data.local.database.notification.template

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "notification_template")
data class NotificationTemplateEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  @ColumnInfo("title") val title: String,
  @ColumnInfo("body") val body: String,
  @ColumnInfo("scope") val scope: String,
  @ColumnInfo("created_at") val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
  @ColumnInfo("updated_at") val updatedAt: Long = ZonedDateTime.now().toEpochSecond(),
)

enum class NotificationTemplateScope {
  COMPANY,
  LOCATION,
  ACCOUNT,
}
