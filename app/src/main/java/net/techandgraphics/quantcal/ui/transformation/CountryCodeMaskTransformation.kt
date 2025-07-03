package net.techandgraphics.quantcal.ui.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CountryCodeMaskTransformation(
  private val mask: String,
  private val countryCode: String = "+265",
) : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    val maxDigits = mask.count { it == 'X' }
    val cleanedText = text.text.take(maxDigits)

    if (cleanedText.isEmpty()) {
      return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
    }

    val formatted = buildString {
      append("$countryCode-")

      var maskIndex = 0
      var textIndex = 0

      while (textIndex < cleanedText.length && maskIndex < mask.length) {
        when (mask[maskIndex]) {
          'X' -> {
            append(cleanedText[textIndex])
            textIndex++
          }

          else -> append(mask[maskIndex])
        }
        maskIndex++
      }
    }

    val offsetMapping = object : OffsetMapping {
      override fun originalToTransformed(offset: Int): Int {
        if (cleanedText.isEmpty()) return 0
        if (offset == 0) return countryCode.length + 1

        var transformedPos = countryCode.length + 1
        var originalCount = 0

        for (i in mask.indices) {
          if (originalCount >= offset) break
          when (mask[i]) {
            'X' -> {
              originalCount++
              transformedPos++
            }

            else -> transformedPos++
          }
        }

        return transformedPos.coerceAtMost(formatted.length)
      }

      override fun transformedToOriginal(offset: Int): Int {
        if (cleanedText.isEmpty()) return 0
        if (offset <= countryCode.length + 1) return 0

        var originalCount = 0
        var currentPos = countryCode.length + 1

        for (i in mask.indices) {
          if (currentPos >= offset) break
          when (mask[i]) {
            'X' -> {
              originalCount++
              currentPos++
            }

            else -> currentPos++
          }
        }

        return originalCount
      }
    }

    return TransformedText(AnnotatedString(formatted), offsetMapping)
  }
}
