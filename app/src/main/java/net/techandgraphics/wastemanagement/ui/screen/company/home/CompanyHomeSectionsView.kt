package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.toGradient
import net.techandgraphics.wastemanagement.ui.theme.Blue
import net.techandgraphics.wastemanagement.ui.theme.Brown
import net.techandgraphics.wastemanagement.ui.theme.Cine
import net.techandgraphics.wastemanagement.ui.theme.Green
import net.techandgraphics.wastemanagement.ui.theme.Purple
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


private val rightCompanyHomeItems = listOf(
  CompanyHomeItemModel(
    title = "Plan",
    drawableRes = R.drawable.ic_payments,
    containerColor = Brown
  ),
  CompanyHomeItemModel(
    title = "Methods",
    drawableRes = R.drawable.ic_history,
    containerColor = Cine
  ),
  CompanyHomeItemModel(
    title = "Manage\nClients",
    drawableRes = R.drawable.ic_account,
    containerColor = Purple
  ),
)

private val leftCompanyHomeItems = listOf(
  CompanyHomeItemModel(
    title = "Manage Company",
    drawableRes = R.drawable.ic_house,
    containerColor = Blue
  ),
  CompanyHomeItemModel(
    title = "Manage Clients",
    drawableRes = R.drawable.ic_account,
    containerColor = Green
  ),
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeSectionsView(
  state: CompanyHomeState,
  onEvent: (CompanyHomeEvent) -> Unit
) {


  Row(
    modifier = Modifier
      .height(300.dp)
      .fillMaxWidth()
      .padding(vertical = 8.dp),
  ) {

    Column(
      modifier = Modifier
        .weight(1f)
        .padding(4.dp)
    ) {
      leftCompanyHomeItems.forEach { item ->
        Card(
          onClick = {},
          modifier = Modifier
            .padding(vertical = 4.dp)
            .weight(1f)
            .fillMaxWidth(),
          colors = CardDefaults.elevatedCardColors(
            containerColor = item.containerColor.copy(.4f)
          ),
          shape = RoundedCornerShape(4)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              painterResource(item.drawableRes),
              contentDescription = null,
              modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(CircleShape)
                .background(item.containerColor.toGradient())
                .size(42.dp)
                .padding(8.dp),
              tint = Color.White
            )
            Text(
              text = item.title,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      }
    }

    Column(
      modifier = Modifier
        .fillMaxHeight()
        .weight(1f)
        .padding(8.dp)
    ) {
      rightCompanyHomeItems.forEachIndexed { index, item ->
        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.Center
        ) {
          Surface(tonalElevation = 2.dp) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                painterResource(item.drawableRes),
                contentDescription = null,
                modifier = Modifier
                  .clip(CircleShape)
                  .background(item.containerColor.toGradient())
                  .size(32.dp)
                  .padding(8.dp),
                tint = Color.White
              )
              Text(
                text = item.title,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
              )
            }
          }

        }
      }
    }

  }


}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeSectionsViewPreview() {
  WasteManagementTheme {
    CompanyHomeSectionsView(
      state = CompanyHomeState(),
      onEvent = {}
    )
  }
}
