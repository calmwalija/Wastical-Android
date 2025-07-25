package net.techandgraphics.wastical

private val units = arrayOf(
  "",
  "one",
  "two",
  "three",
  "four",
  "five",
  "six",
  "seven",
  "eight",
  "nine",
  "ten",
  "eleven",
  "twelve",
  "thirteen",
  "fourteen",
  "fifteen",
  "sixteen",
  "seventeen",
  "eighteen",
  "nineteen",
)

private val tens = arrayOf(
  "",
  "",
  "twenty",
  "thirty",
  "forty",
  "fifty",
  "sixty",
  "seventy",
  "eighty",
  "ninety",
)

private val scales = listOf(
  "billion" to 1_000_000_000L,
  "million" to 1_000_000L,
  "thousand" to 1_000L,
  "" to 1L,
)

fun Number.toEnglishWords(): String {
  val number = this.toLong()
  if (number == 0L) return "zero"

  fun convertLessThanThousand(n: Int): String {
    return when {
      n == 0 -> ""
      n < 20 -> units[n]
      n < 100 -> tens[n / 10] + if (n % 10 != 0) "-${units[n % 10]}" else ""
      else -> {
        val hundred = "${units[n / 100]} hundred"
        val remainder = n % 100
        if (remainder != 0) "$hundred ${convertLessThanThousand(remainder)}" else hundred
      }
    }
  }

  var remaining = number
  val result = mutableListOf<String>()

  for ((scaleName, scaleValue) in scales) {
    if (remaining == 0L) break
    if (remaining >= scaleValue) {
      val count = (remaining / scaleValue).toInt()
      val chunk = convertLessThanThousand(count)
      if (chunk.isNotEmpty()) {
        result.add("$chunk${if (scaleName.isNotEmpty()) " $scaleName" else ""}")
      }
      remaining %= scaleValue
    }
  }

  return result.joinToString(" ").ifEmpty { "zero" }
}
