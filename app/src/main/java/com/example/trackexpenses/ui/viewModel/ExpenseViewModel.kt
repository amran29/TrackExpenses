package com.example.trackexpenses.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.trackexpenses.data.database.ExpensDatabase
import com.example.trackexpenses.repository.ExpenseRepository
import com.example.trackexpenses.data.model.ExpenseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    val allExpenses: LiveData<List<ExpenseModel>>
    val totalExpenses: LiveData<Double>

    init {
        val dao = ExpensDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(dao)
        allExpenses = repository.allExpenses
        totalExpenses = repository.totalExpenses
    }

    fun insertExpense(expense: ExpenseModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteExpense(expense)
        }
    }
}