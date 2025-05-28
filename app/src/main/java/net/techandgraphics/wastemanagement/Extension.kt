package net.techandgraphics.wastemanagement

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat.getSystemService
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.DecimalFormat

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Number.toAmount() = "K${format(this)}"

private fun format(number: Number): String = DecimalFormat("#,###").format(number)

fun Context.copyTextToClipboard(text: String) {
  val clipboard = getSystemService(this, ClipboardManager::class.java)
  val clip = ClipData.newPlainText(this.javaClass.simpleName, text)
  clipboard?.setPrimaryClip(clip)
}

fun Bitmap.image2Text(onResult: (Result<String>) -> Unit) {
  TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    .process(InputImage.fromBitmap(this, 0))
    .addOnSuccessListener { onResult(Result.success(it.text)) }
    .addOnFailureListener { onResult(Result.failure(it)) }
}

fun Context.onTextToClipboard(text: String) = copyTextToClipboard(text)

fun Context.getUCropFile(fileId: Number) = File(cacheDir, "$fileId.jpg")

fun Color.toGradient() = Brush.horizontalGradient(
  listOf(this.copy(.7f), this.copy(.8f), this),
)
