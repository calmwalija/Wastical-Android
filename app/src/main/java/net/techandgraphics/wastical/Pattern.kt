package net.techandgraphics.wastical

object Pattern {

  const val TIME_HH_MM = "HH:mm" // 24-hour format → 14:34
  const val TIME_HH_MM_SS = "HH:mm:ss" // 24-hour format with seconds → 14:34:56
  const val TIME_HH_MM_SS_SSS = "HH:mm:ss.SSS" // 24-hour format with milliseconds → 14:34:56.789
  const val TIME_H_MM_A = "h:mm a" // 12-hour format → 2:34 PM
  const val TIME_K_MM_A = "K:mm a" // 12-hour format without leading zero → 2:34 PM

  const val DATE_YYYY_MM_DD = "yyyy-MM-dd" // ISO date → 2025-03-23
  const val DATE_YYYY_MM = "yyyy-MM"
  const val DATE_DD_MM_YYYY = "dd-MM-yyyy" // European format → 23-03-2025
  const val DATE_MM_DD_YYYY = "MM/dd/yyyy" // US format → 03/23/2025
  const val DATE_EEE_MMM_D_YYYY = "EEE, MMM d, yyyy" // Short weekday → Sat, Mar 23, 2025
  const val DATE_EEE_MMM_D = "EEEE, MMMM d" // Full weekday → Saturday, March 23
  const val DATE_EEEE_MMMM_D_YYYY = "EEEE, MMMM d, yyyy" // Full weekday → Saturday, March 23, 2025
  const val DATE_MMM_YYYY = "MMM yyyy" // Day Month Year → 23 Mar 2025
  const val DATE_MMMM_D_YYYY = "MMMM dd, yyyy" // Month day, year → March 23, 2025
}
