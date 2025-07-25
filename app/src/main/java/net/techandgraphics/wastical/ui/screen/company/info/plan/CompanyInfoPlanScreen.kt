package net.techandgraphics.wastical.ui.screen.company.info.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyInfoPlanScreen(
  state: CompanyInfoPlanState,
  onEvent: (CompanyInfoPlanEvent) -> Unit,
) {
  when (state) {
    CompanyInfoPlanState.Loading -> LoadingIndicatorView()
    is CompanyInfoPlanState.Success -> Scaffold(
      topBar = {
        CompanyInfoTopAppBarView(state.company) {
          onEvent(CompanyInfoPlanEvent.Button.BackHandler)
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
            text = "Payment Plan",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
          )
        }
        itemsIndexed(state.plans) { index, plan ->
          OutlinedCard(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.elevatedCardColors()
          ) {
            Row(
              modifier = Modifier
                .clickable { }
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {

              Column(
                modifier = Modifier
                  .padding(horizontal = 8.dp)
                  .weight(1f)
              ) {
                Text(
                  text = "Payment Plan ${index.plus(1)}",
                  style = MaterialTheme.typography.bodySmall,
                  maxLines = 1,
                  overflow = TextOverflow.MiddleEllipsis
                )
                Text(
                  text = plan.name,
                  style = MaterialTheme.typography.titleMedium
                )
              }

              Box(
                modifier = Modifier
                  .padding(horizontal = 16.dp)
                  .background(MaterialTheme.colorScheme.secondary.copy(.1f))
                  .fillMaxHeight(.05f)
                  .width(1.dp)
              )

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = plan.fee.toAmount(),
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = plan.period.name,
                  style = MaterialTheme.typography.bodySmall
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
private fun CompanyInfoPlanScreenPreview() {
  WasticalTheme {
    CompanyInfoPlanScreen(
      state = CompanyInfoPlanState.Success(
        company = company4Preview,
        plans = (1..3).map { paymentPlan4Preview }
      ),
      onEvent = {}
    )
  }
}
