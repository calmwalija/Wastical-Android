package net.techandgraphics.wastemanagement.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import net.techandgraphics.wastemanagement.R
import kotlin.random.Random

class NotificationBuilder(private val context: Context) {

  private val notificationManager =
    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

  fun registerChannels() {
    NotificationType.entries.forEach { notification ->
      notificationManager.createNotificationChannel(
        NotificationChannel(
          notification.id,
          notification.description,
          NotificationManager.IMPORTANCE_HIGH,
        ),
      )
    }
  }

  private fun NotificationCompat.Builder.configs(notification: NotificationUiModel) {
    setSmallIcon(R.drawable.ic_alternate)
    setPriority(NotificationCompat.PRIORITY_HIGH)
    setColor(ContextCompat.getColor(context, R.color.teal_700))
    setCategory(NotificationCompat.CATEGORY_MESSAGE)
    setAutoCancel(true)
    setDefaults(Notification.DEFAULT_ALL)
    setContentText(notification.body)
    setContentTitle(notification.title)
    setStyle(notification.style)
  }

  fun withActions(
    vararg actions: NotificationCompat.Action,
    notification: NotificationUiModel,
  ) = builder(notification)
    .run {
      configs(notification)
      actions.forEach(::addAction)
      if (ActivityCompat.checkSelfPermission(
          context, Manifest.permission.POST_NOTIFICATIONS,
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
      NotificationManagerCompat.from(context)
        .notify(Random.nextInt(), build())
    }


  fun show(notification: NotificationUiModel) {
    builder(notification).run {
      configs(notification)
      if (ActivityCompat.checkSelfPermission(
          context, Manifest.permission.POST_NOTIFICATIONS,
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
      NotificationManagerCompat.from(context)
        .notify(Random.nextInt(), build())
    }
  }

  fun builder(notification: NotificationUiModel): NotificationCompat.Builder =
    NotificationCompat.Builder(context, notification.type.id)


}
