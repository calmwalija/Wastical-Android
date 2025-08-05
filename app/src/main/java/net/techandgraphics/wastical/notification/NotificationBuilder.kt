package net.techandgraphics.wastical.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.activity.MainActivity
import net.techandgraphics.wastical.worker.client.payment.INTENT_EXTRA_GOTO

class NotificationBuilder(private val context: Context) {

  private val notificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  fun registerChannels() {
    NotificationType.entries.forEach { type ->
      val channel = NotificationChannel(
        type.id,
        type.description,
        NotificationManager.IMPORTANCE_HIGH,
      ).apply {
        enableLights(true)
        enableVibration(true)
        description = type.description
      }
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun NotificationCompat.Builder.applyConfig(model: NotificationBuilderModel): NotificationCompat.Builder {
    return apply {
      setSmallIcon(model.iconRes ?: R.drawable.ic_logo)
      setContentTitle(model.title)
      setContentText(model.body)
      setStyle(model.style ?: NotificationCompat.BigTextStyle().bigText(model.body))
      setPriority(NotificationCompat.PRIORITY_HIGH)
      setCategory(NotificationCompat.CATEGORY_MESSAGE)
      setAutoCancel(true)
      setDefaults(Notification.DEFAULT_ALL)
      model.contentIntent?.let { setContentIntent(it) }
    }
  }

  @SuppressLint("MissingPermission")
  fun show(
    notificationId: Long,
    model: NotificationBuilderModel,
    vararg actions: NotificationCompat.Action,
  ) {
    if (!hasPermission()) return

    val builder = NotificationCompat.Builder(context, model.type.id)
      .applyConfig(model)
      .apply {
        actions.forEach { addAction(it) }
      }
    NotificationManagerCompat
      .from(context)
      .notify(notificationId.toInt(), builder.build())
  }

  private fun hasPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }
}

fun pendingIntent(
  context: Context,
  gotoToRoute: String,
): PendingIntent {
  val intent = Intent(context, MainActivity::class.java).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    putExtra(INTENT_EXTRA_GOTO, gotoToRoute)
  }
  return PendingIntent.getActivity(
    context,
    0,
    intent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
  )
}
