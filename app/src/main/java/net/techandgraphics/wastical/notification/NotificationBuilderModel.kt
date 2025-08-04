package net.techandgraphics.wastical.notification

import android.app.PendingIntent
import androidx.core.app.NotificationCompat

data class NotificationBuilderModel(
  val type: NotificationType,
  val title: String,
  val body: String,
  val style: NotificationCompat.Style? = null,
  val contentIntent: PendingIntent? = null,
  val iconRes: Int? = null,
)
