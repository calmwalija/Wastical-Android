package net.techandgraphics.wastemanagement.ui.screen.company.payment.location.overview

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.AccountSortOrder
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastemanagement.getToday
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.accountWithPaymentStatus4Preview
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyLocation4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicArea4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastemanagement.ui.theme.Green
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentLocationOverviewScreen(
  state: CompanyPaymentLocationOverviewState,
  onEvent: (CompanyPaymentLocationOverviewEvent) -> Unit,
) {

  when (state) {
    CompanyPaymentLocationOverviewState.Loading -> LoadingIndicatorView()
    is CompanyPaymentLocationOverviewState.Success -> {

      var targetValue by remember { mutableFloatStateOf(0f) }
      val animateAsFloat by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 5_000)
      )

      val progressText = String.format(locale = Locale.getDefault(), "%.1f", animateAsFloat * 100)

      var showSortBy by remember { mutableStateOf(false) }

      LaunchedEffect(state.payment4CurrentMonth) {
        targetValue = state.payment4CurrentMonth.totalPaidAccounts
          .toFloat()
          .div(state.accounts.size)
      }

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyPaymentLocationOverviewEvent.Button.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 4.dp)
        ) {

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = state.demographicArea.name,
                  style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                  text = state.demographicStreet.name,
                )
                Text(
                  text = "${state.payment4CurrentMonth.totalPaidAccounts} of ${state.accounts.size}",
                  color = MaterialTheme.colorScheme.primary,
                )
              }

              FilledIconButton(
                onClick = { showSortBy = true },
                shape = RoundedCornerShape(16),
                colors = IconButtonDefaults.iconButtonColors(
                  containerColor = MaterialTheme.colorScheme.primary
                )
              ) {
                Icon(
                  painterResource(R.drawable.ic_filter),
                  contentDescription = null,
                  modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
                )

                DropdownMenu(showSortBy, onDismissRequest = { showSortBy = false }) {
                  AccountSortOrder.entries.forEach { sortBy ->
                    DropdownMenuItem(
                      text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = sortBy.name,
                            modifier = Modifier.padding(end = 16.dp),
                            color = if (state.sortBy == sortBy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                          )
                          if (state.sortBy == sortBy)
                            Icon(
                              Icons.Rounded.CheckCircle,
                              contentDescription = null,
                              tint = MaterialTheme.colorScheme.primary,
                              modifier = Modifier.size(18.dp)
                            )
                        }
                      },
                      enabled = state.sortBy != sortBy,
                      onClick = {
                        onEvent(CompanyPaymentLocationOverviewEvent.Button.SortBy(sortBy))
                        showSortBy = false
                      })
                  }

                }
              }

            }
          }

          item {
            Column(
              modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {


              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {

                Box(
                  modifier = Modifier
                    .padding(end = 24.dp)
                    .size(160.dp),
                  contentAlignment = Alignment.Center
                ) {
                  CircularProgressIndicator(
                    progress = { animateAsFloat },
                    color = MaterialTheme.colorScheme.primary.copy(.6f),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 16.dp,
                    modifier = Modifier.fillMaxSize()
                  )
                  Text(
                    text = "${progressText}%",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                  )
                }

                Column {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "Collected")
                    Text(
                      text = state.payment4CurrentMonth.totalPaidAmount.toAmount(),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold,
                      color = Green
                    )
                  }

                  Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "Expected")
                    Text(
                      text = state.expectedAmountToCollect.toAmount(),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }

                  Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "Outstanding")
                    Text(
                      text = state.expectedAmountToCollect
                        .minus(state.payment4CurrentMonth.totalPaidAmount)
                        .toAmount(),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.error
                    )
                  }
                }
              }
            }
          }

          items(state.accounts, key = { it.account.id }) { entity ->
            CompanyPaymentLocationClientView(
              entity = entity,
              modifier = Modifier.animateItem(),
              onEvent
            )
          }
        }

      }
    }
  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyPaymentLocationOverviewScreenPreview() {
  WasteManagementTheme {
    CompanyPaymentLocationOverviewScreen(
      state = companyPaymentLocationOverviewStateSuccess(),
      onEvent = {}
    )
  }
}

fun companyPaymentLocationOverviewStateSuccess() =
  CompanyPaymentLocationOverviewState.Success(
    company = company4Preview,
    demographicStreet = demographicStreet4Preview,
    demographicArea = demographicArea4Preview,
    accounts = (1..4).map { accountWithPaymentStatus4Preview },
    payment4CurrentMonth = Payment4CurrentMonth(120, 935_000),
    expectedAmountToCollect = 3_000,
    companyLocation = companyLocation4Preview,
    monthYear = MonthYear(getToday().month, getToday().year)
  )
