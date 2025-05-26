package net.techandgraphics.wastemanagement.ui.screen.client.invoice

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import net.techandgraphics.wastemanagement.R

fun bold(context: Context): Typeface? =
  ResourcesCompat.getFont(context, R.font.merri_weather_sans_bold)

fun light(context: Context): Typeface? =
  ResourcesCompat.getFont(context, R.font.merri_weather_sans_light)

fun regular(context: Context): Typeface? =
  ResourcesCompat.getFont(context, R.font.merri_weather_sans_regular)

fun medium(context: Context): Typeface? =
  ResourcesCompat.getFont(context, R.font.merri_weather_sans_medium)

fun semiBold(context: Context): Typeface? =
  ResourcesCompat.getFont(context, R.font.merri_weather_sans_semi_bold)

fun extraBold(context: Context): Typeface? =
  ResourcesCompat.getFont(context, R.font.merri_weather_sans_extra_bold)

fun mailMan(context: Context): Typeface? =
  ResourcesCompat.getFont(context, R.font.mail_man_regular)
