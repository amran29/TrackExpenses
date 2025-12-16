package com.example.trackexpenses.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.*
import com.example.trackexpenses.data.model.ExpenseModel


@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseModel)

    @Query("SELECT * FROM ExpenseModel ORDER BY id DESC")
    fun getAllExpenses(): LiveData<List<ExpenseModel>>

    @Delete
    suspend fun deleteExpense(expense: ExpenseModel)

    @Query("SELECT SUM(amount) FROM ExpenseModel")
    fun getTotalExpenses(): LiveData<Double>


}