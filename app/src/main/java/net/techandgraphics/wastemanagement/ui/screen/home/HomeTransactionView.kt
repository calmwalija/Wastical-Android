package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.theme.Green50
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeTransactionView(
  onEvent: (HomeEvent) -> Unit
) {

  OutlinedCard(modifier = Modifier.fillMaxWidth()) {
    transactions.forEach {
      Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
          painterResource(it.drawableRes), null,
          modifier = Modifier
            .clip(CircleShape)
            .size(42.dp)
        )
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(4.dp)
        ) {
          Text(text = "K40,000")
          Text(
            text = LocalDate.now().toString(),
            style = MaterialTheme.typography.bodySmall
          )
        }

        Box(contentAlignment = Alignment.Center) {
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .size(26.dp)
              .background(
                brush = Brush.horizontalGradient(
                  listOf(
                    Green50.copy(.7f),
                    Green50.copy(.8f),
                    Green50
                  )
                )
              )
          )
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .size(34.dp)
              .background(Green50.copy(.2f))
          )
          Icon(
            Icons.Default.Check, null,
            modifier = Modifier.size(20.dp),
            tint = Color.White
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
      }
      HorizontalDivider()
    }
  }


}

@Preview(showBackground = true)
@Composable
private fun HomeTransactionViewPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      HomeTransactionView(
        onEvent = {}
      )
    }
  }
}


data class Transaction(
  val paymentMethod: String,
  val numberOfMonths: Int,
  val drawableRes: Int,
  val date: LocalDate = LocalDate.now()
)

private val transactions = listOf(
  Transaction(
    paymentMethod = "Airtel Money",
    numberOfMonths = 1,
    drawableRes = R.drawable.im_airtel_money_logo
  ),
  Transaction(
    paymentMethod = "National Bank",
    numberOfMonths = 1,
    drawableRes = R.drawable.im_national_bank_logo
  ),
)
