package net.techandgraphics.wastemanagement.ui.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.min

class MaskTransformation(private val mask: String) : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    val trimmed = if (text.text.length > mask.count { it == 'X' }) {
      text.text.take(mask.count { it == 'X' })
    } else {
      text.text
    }

    val annotatedString = buildAnnotatedString {
      var maskIndex = 0
      var textIndex = 0

      while (textIndex < trimmed.length && maskIndex < mask.length) {
        if (mask[maskIndex] == 'X') {
          append(trimmed[textIndex])
          textIndex++
        } else {
          append(mask[maskIndex])
        }
        maskIndex++
      }
    }

    val offsetMapping = object : OffsetMapping {
      override fun originalToTransformed(offset: Int): Int {
        var textCount = 0
        for (i in 0 until mask.length) {
          if (textCount >= offset) return i
          if (mask[i] == 'X') textCount++
        }
        return mask.length
      }

      override fun transformedToOriginal(offset: Int): Int {
        var textCount = 0
        for (i in 0 until min(offset, mask.length)) {
          if (mask[i] == 'X') textCount++
        }
        return textCount
      }
    }

    return TransformedText(annotatedString, offsetMapping)
  }
}
