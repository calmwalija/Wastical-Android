package net.techandgraphics.wastical.ui.screen.company.info.method

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.gatewayDrawableRes
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyInfoMethodScreen(
  state: CompanyInfoMethodState,
  onEvent: (CompanyInfoMethodEvent) -> Unit,
) {

  when (state) {
    CompanyInfoMethodState.Loading -> LoadingIndicatorView()
    is CompanyInfoMethodState.Success -> Scaffold(
      topBar = {
        CompanyInfoTopAppBarView(state.company) {
          onEvent(CompanyInfoMethodEvent.Button.BackHandler)
        }
      },
      floatingActionButton = {
        FloatingActionButton(
          onClick = {},
          containerColor = MaterialTheme.colorScheme.primary
        ) {
          Icon(Icons.Default.Add, null)
        }
      }
    ) {
      LazyColumn(
        contentPadding = it,
        modifier = Modifier.padding(16.dp)
      ) {
        item {
          Text(
            text = "Payment Method",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
          )
        }
        items(state.methods) { ofType ->
          OutlinedCard(
            modifier = Modifier.padding(vertical = 4.dp),
            colors = CardDefaults.elevatedCardColors(),
            onClick = {}
          ) {
            Row(
              modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Image(
                painterResource(gatewayDrawableRes[ofType.gateway.id.minus(1).toInt()]),
                contentDescription = null,
                modifier = Modifier
                  .clip(CircleShape)
                  .size(42.dp),
                contentScale = ContentScale.Crop,
              )
              Column(
                modifier = Modifier
                  .padding(horizontal = 8.dp)
                  .weight(1f)
              ) {
                Text(
                  text = ofType.method.account,
                  style = MaterialTheme.typography.bodySmall,
                  maxLines = 1,
                  overflow = TextOverflow.MiddleEllipsis
                )
                Text(
                  text = ofType.gateway.name,
                  style = MaterialTheme.typography.titleMedium
                )
              }
            }
          }
        }
      }
    }
  }
}


@Preview
@Composable
private fun CompanyInfoMethodScreenPreview() {
  WasticalTheme {
    CompanyInfoMethodScreen(
      state = CompanyInfoMethodState.Success(
        company = company4Preview,
        methods = (1..3).map { listOf(paymentMethodWithGatewayAndPlan4Preview) }.flatten()
      ),
      onEvent = {}
    )
  }
}
