package net.techandgraphics.wastical

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import java.io.File
import java.security.MessageDigest

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

fun getMimeType(file: File): String {
  val extension = file.extension.lowercase()
  return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
}

@SuppressLint("QueryPermissionsNeeded")
fun File.preview(context: Context) {
  val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", this)
  val mimeType = getMimeType(this)
  val flag = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
  val intent = Intent(Intent.ACTION_VIEW).apply {
    setDataAndType(uri, mimeType)
    flags = flag or Intent.FLAG_ACTIVITY_NEW_TASK
  }
  val resolveInfos =
    context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

  resolveInfos.forEach { resolveInfo ->
    val packageName = resolveInfo.activityInfo.packageName
    context.grantUriPermission(packageName, uri, flag)
  }
  kotlin.runCatching {
    context.startActivity(intent)
  }.onFailure { context.toast("No app found to open this file type") }
}

fun File.share(context: Context) {
  val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", this)
  val shareIntent = Intent(Intent.ACTION_SEND).apply {
    type = "application/pdf"
    putExtra(Intent.EXTRA_SUBJECT, "Share Invoice")
    putExtra(Intent.EXTRA_STREAM, uri)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    if (context !is Activity) {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  }
  val chooser = Intent.createChooser(shareIntent, "Share PDF File").apply {
    if (context !is Activity) {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  }
  context.startActivity(chooser)
}

fun Context.workingDir(): File = filesDir

private const val TYPE = "application/json"

fun Context.write(jsonData: String, fileName: String): File {
  openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
    outputStream.write(jsonData.toByteArray())
  }
  return File(workingDir(), fileName)
}

fun Context.share(file: File) {
  val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
  ShareCompat.IntentBuilder(this)
    .setType(TYPE)
    .setSubject("Shared files")
    .addStream(uri)
    .setChooserTitle("Share JSON File")
    .startChooser()
}

fun Long.hash(text: String, algorithm: String = "SHA-512"): String {
  val theKey = toString()
    .substring(5, toString().length.minus(3))
    .toInt()
    .times(toString().sumOf { it.digitToInt() })
    .toString()
  val bytes =
    MessageDigest.getInstance(algorithm).digest(theKey.plus(text).plus(theKey).toByteArray())
  return bytes.joinToString("") { "%02x".format(it) }
}

fun Context.getFile(uri: Uri): File {
  val tempFile = File(workingDir(), "wastical.json")
  contentResolver.openInputStream(uri)?.use { input ->
    tempFile.outputStream().use { output ->
      input.copyTo(output)
    }
  }
  return tempFile
}
