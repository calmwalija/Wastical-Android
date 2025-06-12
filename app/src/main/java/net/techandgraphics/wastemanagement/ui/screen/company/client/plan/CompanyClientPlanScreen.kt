package net.techandgraphics.wastemanagement.ui.screen.company.client.plan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company.AccountInfoView
import net.techandgraphics.wastemanagement.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientPlanScreen(
  state: CompanyClientPlanState,
  onEvent: (CompanyClientPlanEvent) -> Unit,
) {


  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = { }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
          }
        },
        modifier = Modifier.shadow(0.dp),
        colors = TopAppBarDefaults.topAppBarColors()
      )
    },
  ) {
    Column(modifier = Modifier.padding(it)) {

      Text(
        text = "Payment Plan",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(16.dp)
      )

      if (state is CompanyClientPlanState.Success) {
        AccountInfoView(state.account)
        Spacer(modifier = Modifier.height(16.dp))
        CompanyClientPlanView(state, onEvent)
      } else LoadingIndicatorView()

    }

  }


}


@Preview
@Composable
private fun CompanyClientPlanScreenPreview() {
  WasteManagementTheme {
    CompanyClientPlanScreen(
      state = CompanyClientPlanState.Success(
        account = account4Preview,
        plan = paymentPlan4Preview,
        paymentPlans = listOf(paymentPlan4Preview, paymentPlan4Preview)
      ),
      onEvent = {}
    )
  }
}
