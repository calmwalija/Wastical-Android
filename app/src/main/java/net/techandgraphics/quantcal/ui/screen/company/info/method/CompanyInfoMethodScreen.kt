package net.techandgraphics.quantcal.ui.screen.company.info.method

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import net.techandgraphics.quantcal.gatewayDrawableRes
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

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
      contentWindowInsets = WindowInsets.safeGestures
    ) {
      LazyColumn(
        contentPadding = it,
        modifier = Modifier.padding(vertical = 32.dp)
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
            modifier = Modifier.padding(vertical = 8.dp),
            colors = CardDefaults.elevatedCardColors()
          ) {
            Row(
              modifier = Modifier
                .clickable { }
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {

              Image(
                painterResource(gatewayDrawableRes[ofType.gateway.id.minus(1).toInt()]),
                contentDescription = null,
                modifier = Modifier
                  .clip(CircleShape)
                  .size(32.dp),
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
  QuantcalTheme {
    CompanyInfoMethodScreen(
      state = CompanyInfoMethodState.Success(
        company = company4Preview,
        methods = (1..3).map { listOf(paymentMethodWithGatewayAndPlan4Preview) }.flatten()
      ),
      onEvent = {}
    )
  }
}
