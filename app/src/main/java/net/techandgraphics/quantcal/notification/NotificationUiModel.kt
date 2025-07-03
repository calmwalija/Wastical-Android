package net.techandgraphics.quantcal.notification

import androidx.core.app.NotificationCompat

data class NotificationUiModel(
  val type: NotificationType,
  val title: String,
  val body: String,
  val style: NotificationCompat.Style? = null,
)
