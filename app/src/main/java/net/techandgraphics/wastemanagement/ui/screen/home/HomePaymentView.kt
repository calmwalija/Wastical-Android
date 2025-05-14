package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomePaymentView(
  state: HomeState,
  onEvent: (HomeEvent) -> Unit
) {

  ElevatedCard(modifier = Modifier.fillMaxWidth()) {


    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {


      Icon(
        Icons.Outlined.Info, null,
        modifier = Modifier.size(42.dp),
      )

      Column(
        modifier = Modifier
          .padding(start = 8.dp)
          .weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = "Next Payment",
          style = MaterialTheme.typography.bodySmall,
        )
        Text(
          text = LocalDate.now().toString(),
          fontWeight = FontWeight.Bold,
        )
        Badge(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
          Text(
            text = "Overdue",
            fontWeight = FontWeight.Bold
          )
        }
      }


      OutlinedButton(
        onClick = {},
        colors = ButtonDefaults.buttonColors(
          containerColor = CardDefaults.elevatedCardColors().containerColor
        )
      ) { Text(text = "Pay") }

      Spacer(modifier = Modifier.width(8.dp))

    }


  }

}

@Preview(showBackground = true)
@Composable
private fun HomePaymentViewPreview() {
  WasteManagementTheme {
    HomePaymentView(
      state = HomeState(),
      onEvent = {}
    )
  }
}
