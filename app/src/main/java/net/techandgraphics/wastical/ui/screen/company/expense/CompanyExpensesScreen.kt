package net.techandgraphics.wastical.ui.screen.company.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.domain.model.ExpenseUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.R
import androidx.compose.ui.res.painterResource
import net.techandgraphics.wastical.data.local.database.expense.ExpenseCategory
import net.techandgraphics.wastical.data.local.database.expense.ExpensePeriod

@Composable
fun CompanyExpensesScreen(
  state: androidx.compose.runtime.State<CompanyExpensesState>,
  onEvent: (CompanyExpensesEvent) -> Unit,
) {
  var showDialog by remember { mutableStateOf(false) }
  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = { showDialog = true }) {
        Icon(painter = painterResource(id = R.drawable.ic_add_photo), contentDescription = null)
      }
    }
  ) { padding ->
    when (val s = state.value) {
      CompanyExpensesState.Loading -> {
        Column(
          modifier = Modifier.fillMaxSize().padding(padding),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) { Text("Loading…") }
      }

      is CompanyExpensesState.Success -> {
        LazyColumn(
          modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          item { SummaryCard(collected = s.collected, expenses = s.totalExpenses, profit = s.profit) }
          items(s.expenses) { expense -> ExpenseItem(expense) }
          item { Spacer(modifier = Modifier.height(24.dp)) }
        }
      }
    }
  }

  if (showDialog) {
    AddExpenseDialog(
      onDismiss = { showDialog = false },
      onAdd = { title, amount, category, period, intervalDays, qty, date ->
        onEvent(
          CompanyExpensesEvent.Add(
            title = title,
            amount = amount,
            category = category,
            period = period,
            intervalDays = intervalDays,
            quantity = qty,
            dateEpochSeconds = date,
          )
        )
        showDialog = false
      }
    )
  }
}

@Composable
private fun SummaryCard(collected: Long, expenses: Long, profit: Long) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text("This month", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(modifier = Modifier.height(6.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Metric("Collected", collected.toAmount())
        Metric("Expenses", expenses.toAmount())
        Metric("Profit", profit.toAmount())
      }
    }
  }
}

@Composable
private fun AddExpenseDialog(
  onDismiss: () -> Unit,
  onAdd: (String, Long, ExpenseCategory, ExpensePeriod, Int?, Int?, Long) -> Unit,
) {
  var title by remember { mutableStateOf("") }
  var amount by remember { mutableStateOf("") }
  var category by remember { mutableStateOf(ExpenseCategory.Fuel) }
  var period by remember { mutableStateOf(ExpensePeriod.DAILY) }
  val now = System.currentTimeMillis() / 1000
  var showCategoryMenu by remember { mutableStateOf(false) }
  var showPeriodMenu by remember { mutableStateOf(false) }
  var customDays by remember { mutableStateOf("") }
  var quantity by remember { mutableStateOf("") }

  fun friendlyCategory(c: ExpenseCategory) = when (c) {
    ExpenseCategory.Fuel -> "Fuel"
    ExpenseCategory.CarHire -> "Car hire"
    ExpenseCategory.Salary -> "Salary"
    ExpenseCategory.Utilities -> "Utilities"
    ExpenseCategory.Maintenance -> "Maintenance"
    ExpenseCategory.Emergency -> "Emergency"
    ExpenseCategory.Other -> "Other"
  }

  fun friendlyPeriod(p: ExpensePeriod) = when (p) {
    ExpensePeriod.DAILY -> "Daily"
    ExpensePeriod.WEEKLY -> "Weekly"
    ExpensePeriod.MONTHLY -> "Monthly"
    ExpensePeriod.CUSTOM_DAYS -> "Every N days"
    ExpensePeriod.ADHOC -> "Ad-hoc"
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(onClick = {
        val parsed = amount.toLongOrNull() ?: 0L
        onAdd(title.trim(), parsed, category, period, customDays.toIntOrNull(), quantity.toIntOrNull(), now)
      }) { Text("Add") }
    },
    dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } },
    title = { Text("Add expense") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = amount, onValueChange = { amount = it.filter { ch -> ch.isDigit() } }, label = { Text("Amount") })

        Column {
          OutlinedTextField(
            value = friendlyCategory(category),
            onValueChange = {},
            label = { Text("Category") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { IconButton(onClick = { showCategoryMenu = true }) { Text("Select") } }
          )
          DropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
            ExpenseCategory.entries.forEach { c ->
              DropdownMenuItem(text = { Text(friendlyCategory(c)) }, onClick = {
                category = c
                showCategoryMenu = false
              })
            }
          }
        }

        Column {
          OutlinedTextField(
            value = friendlyPeriod(period),
            onValueChange = {},
            label = { Text("Period") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { IconButton(onClick = { showPeriodMenu = true }) { Text("Select") } }
          )
          DropdownMenu(expanded = showPeriodMenu, onDismissRequest = { showPeriodMenu = false }) {
            ExpensePeriod.entries.forEach { p ->
              DropdownMenuItem(text = { Text(friendlyPeriod(p)) }, onClick = {
                period = p
                showPeriodMenu = false
              })
            }
          }
          if (period == ExpensePeriod.CUSTOM_DAYS) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
              value = customDays,
              onValueChange = { customDays = it.filter { ch -> ch.isDigit() } },
              label = { Text("Interval (days)") }
            )
          }
        }

        if (category == ExpenseCategory.Salary) {
          OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it.filter { ch -> ch.isDigit() } },
            label = { Text("Number of people") }
          )
        }
      }
    }
  )
}

@Composable
private fun Metric(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
private fun ExpenseItem(expense: ExpenseUiModel) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(expense.title, style = MaterialTheme.typography.titleMedium)
      Spacer(modifier = Modifier.height(4.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(expense.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(expense.amount.toAmount(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}
