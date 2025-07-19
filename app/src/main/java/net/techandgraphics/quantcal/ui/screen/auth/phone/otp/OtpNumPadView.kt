package net.techandgraphics.quantcal.ui.screen.auth.phone.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme


@Composable fun OtpNumberPadView(onEvent: (String) -> Unit) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    val numberPad = listOf(
      listOf("1", "2", "3"),
      listOf("4", "5", "6"),
      listOf("7", "8", "9"),
      listOf("", "0", "⌫")
    )
    numberPad.forEach { row ->
      Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 10.dp)
      ) {
        row.forEach { number ->
          TextButton(
            enabled = number.isNotEmpty(),
            onClick = { onEvent(number) },
            modifier = Modifier.weight(1f),
          ) {
            Text(
              text = number,
              style = MaterialTheme.typography.titleLarge
            )
          }
        }
      }
    }
  }
}


@Preview(showBackground = true)
@Composable
private fun VerifyOptScreenPreview() {
  QuantcalTheme {
    OtpNumberPadView {}
  }
}
