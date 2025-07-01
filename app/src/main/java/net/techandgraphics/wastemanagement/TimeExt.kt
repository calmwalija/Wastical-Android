package net.techandgraphics.wastemanagement

import java.time.LocalTime
import java.time.ZonedDateTime

fun getTimeOfDay() = when (LocalTime.now().hour) {
  in 5..11 -> "Morning"
  in 12..17 -> "Afternoon"
  in 18..20 -> "Evening"
  else -> "Night"
}

fun ZonedDateTime.toTimeAgo(): String {
  val now = ZonedDateTime.now(this.zone)
  val duration = java.time.Duration.between(this, now)

  return when {
    duration.seconds < 60 -> "moments ago"

    duration.toMinutes() < 60 -> {
      val minutes = duration.toMinutes()
      if (minutes == 1L) "$minutes minute ago" else "$minutes minutes ago"
    }

    duration.toHours() < 24 -> {
      val hours = duration.toHours()
      if (hours == 1L) "$hours hour ago" else "$hours hours ago"
    }

    duration.toDays() < 7 -> {
      val days = duration.toDays()
      if (days == 1L) "$days day ago" else "$days days ago"
    }

    duration.toDays() < 30 -> {
      val weeks = duration.toDays() / 7
      if (weeks == 1L) "$weeks week ago" else "$weeks weeks ago"
    }

    duration.toDays() < 365 -> {
      val months = duration.toDays() / 30
      if (months == 1L) "$months month ago" else "$months months ago"
    }

    else -> this.defaultDateTime()
  }
}
