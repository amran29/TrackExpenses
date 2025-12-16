package com.example.trackexpenses.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.trackexpenses.data.model.ExpenseModel
import com.example.trackexpenses.data.dao.ExpenseDao
import android.content.Context
import androidx.room.Room




@Database(entities = [ExpenseModel::class], version = 1, exportSchema = false)
abstract class ExpensDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object{
        @Volatile
        private var INSTANCE: ExpensDatabase? = null

        fun getDatabase(context: Context): ExpensDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpensDatabase::class.java,
                    "expense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }

}