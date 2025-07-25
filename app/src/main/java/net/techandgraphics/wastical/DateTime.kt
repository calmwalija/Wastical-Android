package net.techandgraphics.wastical

import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.domain.model.payment.PaymentMonthCoveredUiModel
import java.text.DateFormat.SHORT
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getDateTimeInstance
import java.text.DateFormat.getTimeInstance
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.YearMonth
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

fun Today.toZonedDateTime(): ZonedDateTime =
  ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault())

fun PaymentMonthCoveredUiModel.toZonedDateTime(): ZonedDateTime =
  ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault())

fun MonthYear.toZonedDateTime(): ZonedDateTime =
  ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault())

fun MonthYear.toDateTime(): ZonedDateTime = YearMonth.of(year, month)
  .atDay(1)
  .atStartOfDay(ZoneId.systemDefault())

data class RangeZonedDateTime(val start: ZonedDateTime, val end: ZonedDateTime)

fun ZonedDateTime.getRangeZonedDateTime(): RangeZonedDateTime {
  return if (this.dayOfMonth < 20) {
    val previousMonth20th =
      this
        .minusMonths(1)
        .withDayOfMonth(20)
        .truncatedTo(ChronoUnit.DAYS)

    val currentMonth20th =
      this
        .withDayOfMonth(20)
        .truncatedTo(ChronoUnit.DAYS)

    RangeZonedDateTime(previousMonth20th, currentMonth20th)
  } else {
    val currentMonth20th =
      this
        .withDayOfMonth(20)
        .truncatedTo(ChronoUnit.DAYS)

    val nextMonth20th =
      this
        .plusMonths(1)
        .withDayOfMonth(20)
        .truncatedTo(ChronoUnit.DAYS)

    RangeZonedDateTime(currentMonth20th, nextMonth20th)
  }
}
