package net.techandgraphics.wastemanagement

import java.time.LocalTime

fun getTimeOfDay() = when (LocalTime.now().hour) {
  in 5..11 -> "Morning"
  in 12..17 -> "Afternoon"
  in 18..20 -> "Evening"
  else -> "Night"
}
