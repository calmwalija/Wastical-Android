package net.techandgraphics.wastemanagement

import java.text.DateFormat.SHORT
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getDateTimeInstance
import java.text.DateFormat.getTimeInstance
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

fun Long.defaultTime(): String = getTimeInstance(SHORT).format(this)
fun Long.defaultDate(): String = getDateInstance(SHORT).format(this)
fun Long.defaultDateTime(): String = getDateTimeInstance(SHORT, SHORT).format(this)

fun Long.dateTime(pattern: String = Pattern.TIME_HH_MM): String =
  SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun ZonedDateTime.withPatten(pattern: String = Pattern.DATE_MMM_YYYY): String {
  val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
  return this.format(formatter)
}

fun Long.toZonedDateTime(): ZonedDateTime =
  ZonedDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())

fun ZonedDateTime.defaultTime(): String =
  DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(this)

fun ZonedDateTime.defaultDate(): String =
  DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(this)

fun ZonedDateTime.defaultDateTime(): String =
  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(this)

fun Long.timeAgo(): String {
  val currentDateTime = ZonedDateTime.now()
  val oldDateTime = Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault())

  val seconds = ChronoUnit.SECONDS.between(oldDateTime, currentDateTime)
  val minutes = ChronoUnit.MINUTES.between(oldDateTime, currentDateTime)
  val hours = ChronoUnit.HOURS.between(oldDateTime, currentDateTime)
  val days = ChronoUnit.DAYS.between(oldDateTime, currentDateTime)

  return when {
    seconds < 60 -> "just now"
    minutes == 1L -> "a minute ago"
    minutes < 60 -> "$minutes minutes ago"
    hours == 1L -> "an hour ago"
    hours < 24 -> "$hours hours ago"
    days == 1L -> "yesterday"
    days < 10 -> "$days days ago"
    else -> defaultDateTime()
  }
}
