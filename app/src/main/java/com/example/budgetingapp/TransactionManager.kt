package com.example.budgetingapp

import java.time.LocalDate

// Represents a financial transaction
class Transaction(
    val id: Int,                 // Unique identifier for the transaction
    var amount: Double,          // Amount of the transaction
    var date: LocalDate,         // Date of the transaction
    var name: String,            // Name or description of the transaction
    var isIncome: Boolean,// true for income, false for expense
    var category: String         // Category of the transaction (e.g., Rent, Food, etc.)
)

class TransactionManager {
    val transactions = mutableListOf<Transaction>() // List to store transactions

    // Predefined categories for expenses
    val categories = listOf(
        "Rent",
        "Utilities",
        "Entertainment",
        "Groceries",
        "Restaurants",
        "Shops",
        "Clothing",
        "Other"
    )

    var id = 0 // Counter to generate unique transaction IDs

    // Adds a new transaction to the list
    fun addTransaction(amount: Double, date: LocalDate, name: String, TransactionType: Boolean, category: String) {
        transactions.add(Transaction(id, amount, date, name, TransactionType, category))
        id++ // Increment ID for the next transaction
    }

    // Searches for transactions by name, category, or amount and returns them grouped by date
    fun searchTransactions(search: String): Map<LocalDate, List<Transaction>> {
        return transactions
            .filter {
                it.name.lowercase().contains(search.lowercase()) ||
                        it.category.lowercase().contains(search.lowercase()) ||
                        it.amount.toString().contains(search)
            }
            .groupBy { it.date }
    }

    // Deletes a transaction by its ID
    fun deleteTransaction(id: Int) {
        transactions.removeIf { it.id == id }
    }

    // Updates an existing transaction by ID
    fun updateTransaction(
        id: Int,
        amount: Double,
        date: LocalDate,
        name: String,
        TransactionType: Boolean,
        category: String
    ) {
        transactions.find { it.id == id }?.let {
            it.amount = amount
            it.date = date
            it.name = name
            it.isIncome = TransactionType
            it.category = category
        }
    }

    // Returns transactions ordered by date (newest first), grouped by date
    fun getOrderedTransactions(): Map<LocalDate, List<Transaction>> {
        return transactions.sortedByDescending { it.date }.groupBy { it.date }
    }

    // Returns transactions ordered by date (oldest first), grouped by date
    fun getReverseOrderedTransactions(): Map<LocalDate, List<Transaction>> {
        return transactions.sortedBy { it.date }.groupBy { it.date }
    }

    // Calculates the current balance based on income and expenses
    fun getBalance(): Double {
        var balance = 0.00
        for (transaction in transactions) {
            if (transaction.isIncome)
                balance += transaction.amount // Add income
            else
                balance -= transaction.amount // Subtract expense
        }
        return balance
    }
}
