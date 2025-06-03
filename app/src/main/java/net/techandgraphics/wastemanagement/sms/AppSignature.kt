package net.techandgraphics.wastemanagement.sms

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@Suppress("DEPRECATION")
class AppSignature(context: Context) : ContextWrapper(context) {

  companion object {
    val TAG: String = AppSignature::class.java.simpleName

    private const val HASH_TYPE = "SHA-256"
    const val NUM_HASHED_BYTES = 9
    const val NUM_BASE64_CHAR = 11
  }

  fun getAppSignatures(): ArrayList<String> {
    val appCodes = ArrayList<String>()

    try {
      val signatures = packageManager.getPackageInfo(
        packageName,
        PackageManager.GET_SIGNATURES,
      ).signatures

      if (signatures != null) {
        for (signature in signatures) {
          val hash = hash(packageName, signature.toCharsString())
          if (hash != null) {
            appCodes.add(String.format("%s", hash))
          }
        }
      }
    } catch (e: PackageManager.NameNotFoundException) {
      Log.e(TAG, "Unable to find package to obtain hash.", e)
    }
    return appCodes
  }

  private fun hash(packageName: String, signature: String): String? {
    val appInfo = "$packageName $signature"
    try {
      val messageDigest = MessageDigest.getInstance(HASH_TYPE)
      messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
      var hashSignature = messageDigest.digest()

      hashSignature = hashSignature.copyOfRange(0, NUM_HASHED_BYTES)
      var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
      base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

      Log.e(TAG, String.format("pkg: %s -- hash: %s", packageName, base64Hash))
      return base64Hash
    } catch (e: NoSuchAlgorithmException) {
      Log.e(TAG, "hash:NoSuchAlgorithm", e)
    }
    return null
  }
}
