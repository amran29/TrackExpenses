package com.example.trackexpenses.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackexpenses.data.model.ExpenseModel
import com.example.trackexpenses.databinding.ExpenseItemBinding

// حذفنا دالة الـ lambda من هنا لأننا سنحذف بالسحب
class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expensesList = emptyList<ExpenseModel>()

    // دالة مساعدة للحصول على العنصر عند السحب
    fun getExpenseAt(position: Int): ExpenseModel {
        return expensesList[position]
    }

    fun setExpenses(expenses: List<ExpenseModel>) {
        this.expensesList = expenses
        notifyDataSetChanged()
    }

    class ExpenseViewHolder(val binding: ExpenseItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ExpenseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val currentExpense = expensesList[position]
        holder.binding.tvTitle.text = currentExpense.title
        holder.binding.tvAmount.text = "$${currentExpense.amount}"
        holder.binding.tvCategory.text = currentExpense.category
        holder.binding.tvDate.text = currentExpense.date

        // لم نعد بحاجة لـ root.setOnClickListener هنا
    }

    override fun getItemCount() = expensesList.size
}