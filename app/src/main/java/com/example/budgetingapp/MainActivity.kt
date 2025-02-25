package com.example.budgetingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.LineChartData
import kotlinx.coroutines.launch

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.text.format
import kotlin.text.isBlank
import kotlin.text.isNotBlank

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
    val BudgetLine = remember { BudgetLine() }
    var showAddDialog by remember { mutableStateOf(false) }
    var transactions by remember { mutableStateOf(transactionManager.transactions) }
    var transactionsMap by remember { mutableStateOf(transactionManager.getOrderedTransactions()) }
    var selectedTabIndex by remember { mutableIntStateOf(1) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var isAddingTransaction by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val tabItems = listOf("Transactions", "Dashboard", "Categories")
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

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
                if (selectedTabIndex == 0) {

                    OutlinedTextField(
                        maxLines = 1,
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            if (it.isBlank()) {
                                transactionsMap = transactionManager.getOrderedTransactions()
                            } else {
                                transactionsMap = transactionManager.searchTransactions(it)
                            }
                        },

                        label = { Text("Search Transactions") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        trailingIcon = {
                            if (searchText.isNotBlank()) {
                                IconButton(onClick = {
                                    searchText = ""
                                    transactionsMap = transactionManager.getOrderedTransactions()
                                }) {
                                    Icon(Icons.Filled.Clear, "Clear Search")
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() } // Hides the keyboard when Enter is pressed
                        )

                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { showAddDialog = true }) {
                            Icon(Icons.Filled.Add, "Add Transaction")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Transaction")
                        }
                    }

                }
            }
        },
        content = { innerPadding ->
            val points = listOf(

                Point(0f, 10f),
                Point(2f, 20f))

            val map = transactionManager.getReverseOrderedTransactions()
            var pointss : MutableList<Point> = mutableListOf()

            var balance = 0.00f
            for((date, transactions) in map)
            {
                balance += transactions.sumOf {it.amount}.toFloat()
                pointss.add(Point(date.dayOfMonth.toFloat(), balance))
            }

            when (selectedTabIndex) {
                0 ->
                    TransactionList(
                    transactions = transactionsMap,
                    modifier = Modifier.padding(innerPadding),
                    onEditTransaction = { transaction ->
                        transactionToEdit = transaction
                        showAddDialog = true
                    }
                )

                1 -> DashboardScreen(modifier = Modifier.padding(innerPadding),transactionManager.getBalance(), BudgetLine.buildGraph(pointss), map.size)
                //2 -> CategoriesScreen(modifier = Modifier.padding(innerPadding))
            }
            if (showAddDialog) {
                AddTransactionDialog(
                    onDismiss = {
                        showAddDialog = false
                        transactionToEdit = null
                                },
                    onAddTransaction = { amount, date, name, type, category ->
                        if (!isAddingTransaction) {
                            isAddingTransaction = true
                            coroutineScope.launch {
                                if (transactionToEdit != null) {
                                    transactionManager.updateTransaction(
                                        transactionToEdit!!.id,
                                        amount,
                                        date,
                                        name,
                                        type,
                                        category
                                    )
                                } else {
                                    transactionManager.addTransaction(amount, date, name, type,category)
                                }
                                transactions = transactionManager.transactions
                                transactionsMap = transactionManager.getOrderedTransactions()
                                showAddDialog = false
                                transactionToEdit = null
                                isAddingTransaction = false
                            }
                        }
                    },
                    transactionToEdit = transactionToEdit,
                    onDeleteTransaction = { transaction ->
                        transactionManager.deleteTransaction(transaction.id)
                        transactions = transactionManager.transactions
                        transactionsMap = transactionManager.getOrderedTransactions()
                        showAddDialog = false
                        transactionToEdit = null
                    }
                )
            }
        }
    )
}

@Composable
fun DashboardScreen(modifier: Modifier, balance: Double, lineChartData: LineChartData, numTransactions : Int) {
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
        if(numTransactions >= 2)
        {
            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                lineChartData = lineChartData
            )
        }

    }
}

@Composable
fun TransactionList(transactions: Map<LocalDate, List<Transaction>>, modifier: Modifier = Modifier, onEditTransaction: (Transaction) -> Unit) {

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
                    TransactionItem(transaction = transaction,onEditTransaction = onEditTransaction)
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
fun TransactionItem(transaction: Transaction,  onEditTransaction: (Transaction) -> Unit) {
    val backgroundColor = if (transaction.TransactionType) Color(0xFFE8F5E9) else Color(0xFFFCE4EC) // Light green for income, light red for expense
    val textColor = if (transaction.TransactionType) Color(0xFF388E3C) else Color(0xFFC62828) // Dark green for income, dark red for expense
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditTransaction(transaction) },
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





@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onAddTransaction: (Double, LocalDate, String, Boolean, String) -> Unit,
    transactionToEdit: Transaction? = null,
    onDeleteTransaction: ((Transaction) -> Unit)? = null
) {
    var amount by remember { mutableStateOf(transactionToEdit?.amount?.toString() ?: "") }
    var date by remember { mutableStateOf(transactionToEdit?.date ?: LocalDate.now()) }
    var formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    var category by remember { mutableStateOf(transactionToEdit?.category ?: "") }
    var isIncome by remember { mutableStateOf(transactionToEdit?.TransactionType ?: false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var transactionName by remember { mutableStateOf(transactionToEdit?.name ?: "") }
    val transactionManager = remember { TransactionManager() }
    var categoryNames by remember { mutableStateOf(transactionManager.categories) }
    var selectedCategoryIndex by remember { mutableStateOf(categoryNames.indexOf(category).takeIf { it >= 0 } ?: 0) }

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
                if(isIncome)
                    selectedIndex = 1
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
                            selected = selectedCategoryIndex == index,
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = { selectedCategoryIndex = index},
                            label = { Text(label) }
                        )
                    }
                }
            }
        },

        confirmButton = @androidx.compose.runtime.Composable {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
            {
                if(transactionToEdit != null)
                {
                    Button(onClick = {
                        if (onDeleteTransaction != null) {
                            onDeleteTransaction(transactionToEdit)
                        }
                    }, colors = ButtonDefaults.buttonColors(Color(0xFFC62828))) {

                        Text("Delete")
                    }
                }
                if(transactionToEdit != null)
                    Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (parsedAmount != null && transactionName.isNotBlank() && selectedCategoryIndex != -1) {
                        onAddTransaction(parsedAmount, date, transactionName, isIncome, categoryNames[selectedCategoryIndex])
                    }
                }) {
                    if(transactionToEdit == null)
                        Text("Add Transaction")
                    else
                        Text("Edit")
                }
            }

        }


    )

}