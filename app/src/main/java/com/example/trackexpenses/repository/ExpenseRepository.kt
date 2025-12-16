package com.example.trackexpenses.repository

import androidx.lifecycle.LiveData
import com.example.trackexpenses.data.dao.ExpenseDao
import com.example.trackexpenses.data.model.ExpenseModel


class ExpenseRepository(private val dao: ExpenseDao) {
    val allExpenses: LiveData<List<ExpenseModel>> = dao.getAllExpenses()
    val totalExpenses: LiveData<Double> = dao.getTotalExpenses()

    suspend fun insertExpense(expense: ExpenseModel) {
        dao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseModel) {
        dao.deleteExpense(expense)
    }



}