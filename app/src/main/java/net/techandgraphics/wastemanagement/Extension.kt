package net.techandgraphics.wastemanagement

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Number.toAmount() = "K${format(this)}"
private fun format(number: Number): String = DecimalFormat("#,###").format(number)

fun Bitmap.toFile(context: Context): File {
  val file = File(context.filesDir, "temp.jpg")
  file.createNewFile()
  val bos = ByteArrayOutputStream()
  compress(Bitmap.CompressFormat.JPEG, 100, bos)
  val bitmapData = bos.toByteArray()
  file.outputStream().use {
    it.write(bitmapData)
    it.flush()
  }
  return file
}
