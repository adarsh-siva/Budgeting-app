package com.example.budgetingapp

import java.time.LocalDate


class Transaction(val id: Int, var amount : Double, var date : LocalDate, var name: String, var TransactionType: Boolean, var category: String)

class TransactionManager {
    val transactions = mutableListOf<Transaction>()
    val categories =
        listOf(
            "Rent",
            "Utilities",
            "Entertainment",
            "Groceries",
            "Restaurants",
            "Shops",
            "Clothing",
            "Other",
        )

    var id = 0
    fun addTransaction(amount : Double, date : LocalDate, name: String, TransactionType: Boolean, category: String) {
        transactions.add(Transaction(id, amount, date, name, TransactionType,category))
        id++
    }
    fun deleteTransaction(id: Int) {
        transactions.removeIf { it.id == id }
    }
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
            it.category = category
            it.TransactionType = TransactionType
        }
    }
    fun getOrderedTransactions(): Map<LocalDate, List<Transaction>>  {
            return transactions.sortedByDescending { it.date }.groupBy { it.date }
    }
    fun getBalance(): Double {
        var balance  = 0.00;
        for(transaction in transactions)
        {
            if(transaction.TransactionType)
                balance += transaction.amount
            else
                balance -= transaction.amount
        }
        return balance;
    }



}