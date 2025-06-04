package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.theme.Blue
import net.techandgraphics.wastemanagement.ui.theme.Green
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


private val leftCompanyHomeItems = listOf(
  CompanyHomeItemModel(
    title = "Company",
    drawableRes = R.drawable.ic_warehouse,
    containerColor = Blue,
    event = CompanyHomeEvent.Goto.Profile
  ),
  CompanyHomeItemModel(
    title = "Clients",
    drawableRes = R.drawable.ic_supervisor_account,
    containerColor = Green,
    event = CompanyHomeEvent.Goto.Clients
  ),
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeSectionsView(
  onEvent: (CompanyHomeEvent) -> Unit,
) {
  FlowRow(
    maxItemsInEachRow = 2,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
  ) {
    leftCompanyHomeItems.forEach { item ->
      OutlinedCard(
        onClick = { onEvent(item.event) },
        modifier = Modifier
          .weight(.5f)
          .padding(4.dp),
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
              .padding(bottom = 8.dp)
              .size(32.dp)
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
}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeSectionsViewPreview() {
  WasteManagementTheme {
    CompanyHomeSectionsView(
      onEvent = {}
    )
  }
}
