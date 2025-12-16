package com.example.trackexpenses.ui.view

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackexpenses.R // تأكد من استدعاء الـ R الصحيح
import com.example.trackexpenses.data.model.ExpenseModel
import com.example.trackexpenses.databinding.ActivityMainBinding
import com.example.trackexpenses.databinding.DialogAddExpenseBinding
import com.example.trackexpenses.ui.adapter.ExpenseAdapter
import com.example.trackexpenses.ui.viewModel.ExpenseViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. تحميل اللغة المحفوظة قبل بناء الواجهة
        loadLocale()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. تحميل الوضع المظلم المحفوظ
        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("DarkMode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // إعداد ViewModel و Adapter
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        adapter = ExpenseAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        setupSwipeToDelete()

        // 3. زر تغيير اللغة
        binding.btnLanguage.setOnClickListener {
            showLanguageDialog()
        }

        // 4. زر تغيير الوضع (مظلم/فاتح)
        binding.btnTheme.setOnClickListener {
            toggleTheme(isDarkMode)
        }

        // مراقبة البيانات
        viewModel.allExpenses.observe(this) { adapter.setExpenses(it) }
        viewModel.totalExpenses.observe(this) { total ->
            // استخدام String Resource للإجمالي
            binding.tvTotal.text = "${getString(R.string.total)}: $${String.format("%.2f", total ?: 0.0)}"
        }

        binding.fabAdd.setOnClickListener { showAddExpenseDialog() }
    }

    // --- دوال تغيير اللغة ---
    private fun showLanguageDialog() {
        val languages = arrayOf("English", "العربية")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Language / اختر اللغة")
        builder.setItems(languages) { _, which ->
            if (which == 0) setLocale("en") else setLocale("ar")
        }
        builder.show()
    }

    private fun setLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        // حفظ اللغة
        val editor = getSharedPreferences("AppSettings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", langCode)
        editor.apply()

        // إعادة تشغيل النشاط لتطبيق التغيير
        recreate()
    }

    private fun loadLocale() {
        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if (language != "") {
            val locale = Locale(language!!)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }
    }

    // --- دالة تغيير الثيم ---
    private fun toggleTheme(isCurrentlyDark: Boolean) {
        val editor = sharedPreferences.edit()
        if (isCurrentlyDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putBoolean("DarkMode", false)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putBoolean("DarkMode", true)
        }
        editor.apply()
        // الـ Delegate سيعيد بناء الواجهة تلقائياً أحياناً، لكن لضمان ذلك:
        // recreate() // يمكن إزالتها إذا الـ delegate قام بالعمل
    }

    // --- بقية الدوال (Add, Swipe) كما هي ---
    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val expense = adapter.getExpenseAt(position)
                viewModel.deleteExpense(expense)
                Snackbar.make(binding.root, "${getString(R.string.deleted)} ${expense.title}", Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) { viewModel.insertExpense(expense) }
                    .show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun showAddExpenseDialog() {
        val dialogBinding = DialogAddExpenseBinding.inflate(layoutInflater)
        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.add_btn)) { _, _ ->
                val title = dialogBinding.etTitle.text.toString()
                val amountStr = dialogBinding.etAmount.text.toString()
                if (title.isNotEmpty() && amountStr.isNotEmpty()) {
                    val expense = ExpenseModel(
                        title = title,
                        amount = amountStr.toDouble(),
                        category = dialogBinding.etCategory.text.toString(),
                        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    )
                    viewModel.insertExpense(expense)
                }
            }
            .setNegativeButton(getString(R.string.cancel_btn), null)
            .create().show()
    }
}