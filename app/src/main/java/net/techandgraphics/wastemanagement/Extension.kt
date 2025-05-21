package net.techandgraphics.wastemanagement

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
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

fun Context.copyTextToClipboard(text: String) {
  val clipboard = getSystemService(this, ClipboardManager::class.java)
  val clip = ClipData.newPlainText(this.javaClass.simpleName, text)
  clipboard?.setPrimaryClip(clip)
}


@Suppress("DEPRECATION")
fun Uri.toBitmap(context: Context): Bitmap? = if (Build.VERSION.SDK_INT < 28) {
  MediaStore.Images.Media.getBitmap(context.contentResolver, this)
} else {
  val source = ImageDecoder.createSource(context.contentResolver, this)
  ImageDecoder.decodeBitmap(source)
}

fun Bitmap.toSoftwareBitmap(): Bitmap {
  return this.copy(Bitmap.Config.ARGB_8888, true)
}


fun Bitmap.image2Text(onResult: (Result<String>) -> Unit) {
  TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    .process(InputImage.fromBitmap(this, 0))
    .addOnSuccessListener { onResult(Result.success(it.text)) }
    .addOnFailureListener { onResult(Result.failure(it)) }
}

fun Context.onTextToClipboard(text: String) = copyTextToClipboard(text)

