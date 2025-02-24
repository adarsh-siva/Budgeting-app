package com.example.budgetingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


import kotlin.text.toDoubleOrNull

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.text.format

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetingApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetingApp() {
    val transactionManager = remember { TransactionManager() }
    var showAddDialog by remember { mutableStateOf(false) }
    var transactions by remember { mutableStateOf(transactionManager.transactions) }
    var transactionsMap by remember { mutableStateOf(transactionManager.getOrderedTransactions()) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabItems = listOf("Transactions", "Dashboard", "Categories")

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Budgeting App",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    actions = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Filled.Add, "Add Transaction")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabItems.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            when (selectedTabIndex) {
                0 -> TransactionList(
                    transactions = transactionsMap,
                    modifier = Modifier.padding(innerPadding)
                )
                1 -> DashboardScreen(modifier = Modifier.padding(innerPadding),transactionManager.getBalance())
                //2 -> CategoriesScreen(modifier = Modifier.padding(innerPadding))
            }
            if (showAddDialog) {
                AddTransactionDialog(
                    onDismiss = { showAddDialog = false },
                    onAddTransaction = { amount, date, name, type, category ->
                        transactionManager.addTransaction(amount, date, name, type, category)
                        transactions = transactionManager.transactions // Update the list
                        transactionsMap = transactionManager.getOrderedTransactions()
                        showAddDialog = false
                    }
                )
            }
        }
    )
}

@Composable
fun DashboardScreen(modifier: Modifier, balance: Double) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Total Balance",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "$${String.format("%.2f", balance)}",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = if (balance >= 0) Color(0xFF388E3C) else Color(0xFFC62828) // Green if positive, red if negative
        )
    }
}

@Composable
fun TransactionList(transactions: Map<LocalDate, List<Transaction>>, modifier: Modifier = Modifier) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for((date, transactions) in transactions) {
            item {
                dateHeader(date = date)
            }
            for(transaction in transactions) {
                item {
                    TransactionItem(transaction = transaction)
                }
            }

        }
    }
}
@Composable
fun dateHeader(date: LocalDate)
{
    val formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    Text(
        text = formattedDate,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )

}

@Composable
fun TransactionItem(transaction: Transaction) {
    val backgroundColor = if (transaction.TransactionType) Color(0xFFE8F5E9) else Color(0xFFFCE4EC) // Light green for income, light red for expense
    val textColor = if (transaction.TransactionType) Color(0xFF388E3C) else Color(0xFFC62828) // Dark green for income, dark red for expense
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click if needed */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), //width of list item
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {


                Text(text = transaction.name, fontWeight = FontWeight.Bold, color = textColor)

            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = transaction.category, color = Color.Gray, textAlign = TextAlign.Start)
            }
            Text(text = "${if (transaction.TransactionType) "+ $" else "- $"}${String.format("%.2f", transaction.amount)}", fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun DatePickerTest()
{
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pre-select a date for January 4, 2020
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = 1578096000000)
        DatePicker(state = datePickerState, modifier = Modifier.padding(16.dp))

        Text(
            "Selected date timestamp: ${datePickerState.selectedDateMillis ?: "no selection"}",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onAddTransaction: (Double, LocalDate, String, Boolean, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    var transactionName by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val transactionManager = remember { TransactionManager() }
    var categoryNames by remember { mutableStateOf(transactionManager.categories) }
    var category by remember { mutableStateOf(-1) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: 0).atZone(
                            ZoneOffset.UTC).toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        text = {

            Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                var selectedIndex by remember { mutableIntStateOf(0) }
                val options = listOf("Expense", "Income")
                SingleChoiceSegmentedButtonRow {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            onClick = { selectedIndex = index
                                if(selectedIndex == 0) {isIncome = false}
                                      else {isIncome = true}},
                            selected = index == selectedIndex,
                            label = { Text(label) }
                        )
                    }
                }
            }

            Column {
                Spacer(modifier = Modifier.height(50.dp))

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {showDatePicker = true}
                ) {
                    Text(formattedDate)
                }

                OutlinedTextField(
                    value = transactionName,
                    singleLine = true,
                    onValueChange = { transactionName = it },
                    label = { Text("Transaction Name") },
                    modifier = Modifier.fillMaxWidth(),

                    )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    singleLine = true,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )




            }


            Column {
                Spacer(modifier = Modifier.height(250.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    categoryNames.forEachIndexed {index, label ->
                        InputChip(
                            selected = category == index,
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = { category = index},
                            label = { Text(label) }
                        )
                    }
                }
            }
        },

        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
            {
                Button(onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (parsedAmount != null && transactionName.isNotBlank() && category != -1) {
                        onAddTransaction(parsedAmount, date, transactionName, isIncome, categoryNames[category])
                    }
                }) {
                    Text("Add Transaction")
                }
            }

        }
    )

}