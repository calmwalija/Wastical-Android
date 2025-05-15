package net.techandgraphics.wastemanagement

import android.content.Context
import android.widget.Toast
import java.text.DecimalFormat

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Number.toAmount() = "K${format(this)}"
private fun format(number: Number): String = DecimalFormat("#,###").format(number)
