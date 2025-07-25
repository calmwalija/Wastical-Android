package net.techandgraphics.wcompanion.extensions

import java.security.MessageDigest

object StringExtension {

  fun hash(text: String, algorithm: String = "SHA-512"): String {
    val theKey = toString()
      .substring(5, toString().length.minus(3))
      .toInt()
      .times(toString().sumOf { it.digitToInt() })
      .toString()
    val bytes =
      MessageDigest
        .getInstance(algorithm)
        .digest(theKey.plus(text).plus(theKey).toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }.take(24)
  }
}
