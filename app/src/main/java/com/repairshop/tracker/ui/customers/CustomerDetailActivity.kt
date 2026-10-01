package com.repairshop.tracker.ui.customers

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.repairshop.tracker.R
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.ActivityCustomerDetailBinding
import com.repairshop.tracker.model.Customer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomerDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerDetailBinding
    private var customerId: Long = -1
    private var currentCustomer: Customer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customerId = intent.getLongExtra("CUSTOMER_ID", 1)

        setupToolbar()
        setupButtons()
        loadCustomerData()
    }

    override fun onResume() {
        super.onResume()
        loadCustomerData()
    }

    private fun setupToolbar() {
        binding.toolbarCustomerDetail.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupButtons() {
        binding.btnAddPayment.setOnClickListener {
            showAddPaymentDialog()
        }

        binding.btnViewCustomerHistory.setOnClickListener {
            Toast.makeText(this, "Showing all history for ${currentCustomer?.name ?: "customer"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCustomerData() {
        val db = RepairShopApp.database
        currentCustomer = db.getCustomerById(customerId)

        val customer = currentCustomer ?: return

        // Customer Header
        binding.tvDetailCustomerName.text = customer.name
        binding.tvDetailCustomerPhone.text = "📞 ${customer.phone}"
        binding.tvDetailCustomerAddress.text = "📍 ${if (customer.address.isNotEmpty()) customer.address else "Delhi"}"

        // Repair Summary
        val pref = RepairShopApp.prefManager
        binding.tvDetailTotalCharge.text = pref.formatCurrency(customer.totalCharge)
        binding.tvDetailPaidAdvance.text = pref.formatCurrency(customer.paidAdvance)
        binding.tvDetailRemainingBalance.text = pref.formatCurrency(customer.remainingBalance)

        // Load items in repair
        val transactions = db.getTransactionsForCustomer(customerId)
        val activeTx = transactions.firstOrNull { it.status.equals("In Repair", ignoreCase = true) }
            ?: transactions.firstOrNull()

        if (activeTx != null) {
            binding.tvCurrentItemTitle.text = activeTx.itemName
            binding.tvCurrentItemSubtitle.text = activeTx.itemModel.ifEmpty { activeTx.itemCategory }
            binding.tvCurrentItemBadge.text = activeTx.status
            binding.tvCurrentItemDateIn.text = "IN: ${activeTx.dateIn}"
            binding.tvCurrentItemsHeader.text = "Current Items (${transactions.size})"
        } else {
            binding.tvCurrentItemTitle.text = "No active repair items"
            binding.tvCurrentItemSubtitle.text = "All items delivered"
            binding.tvCurrentItemBadge.visibility = View.GONE
            binding.tvCurrentItemDateIn.visibility = View.GONE
        }

        // Load payments
        renderPayments(customerId)
    }

    private fun renderPayments(custId: Long) {
        val db = RepairShopApp.database
        val payments = db.getPaymentsForCustomer(custId)

        binding.containerPaymentHistory.removeAllViews()

        if (payments.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "No payment records yet"
                setTextColor(ContextCompat.getColor(context, R.color.text_muted))
                setPadding(0, 16, 0, 16)
            }
            binding.containerPaymentHistory.addView(emptyTv)
            return
        }

        for ((index, payment) in payments.withIndex()) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 8)
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val icon = android.widget.ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(48, 48)
                setImageResource(if (payment.isPending) R.drawable.ic_history else R.drawable.ic_check_circle)
                setColorFilter(
                    ContextCompat.getColor(
                        context,
                        if (payment.isPending) R.color.stat_orange_icon else R.color.stat_green_icon
                    )
                )
            }

            val dateTv = TextView(this).apply {
                text = payment.date
                setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                textSize = 13f
                setPadding(16, 0, 16, 0)
            }

            val titleTv = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = payment.title
                setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                textSize = 14f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val amountTv = TextView(this).apply {
                text = RepairShopApp.prefManager.formatCurrency(payment.amount)
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (payment.isPending) R.color.badge_offline_text else R.color.stat_green_text
                    )
                )
                textSize = 15f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            row.addView(icon)
            row.addView(dateTv)
            row.addView(titleTv)
            row.addView(amountTv)

            binding.containerPaymentHistory.addView(row)

            if (index < payments.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2
                    ).apply {
                        setMargins(0, 8, 0, 8)
                    }
                    setBackgroundColor(ContextCompat.getColor(context, R.color.divider_color))
                }
                binding.containerPaymentHistory.addView(divider)
            }
        }
    }

    private fun showAddPaymentDialog() {
        val curr = RepairShopApp.prefManager.currencySymbol
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Enter amount in $curr"
            setPadding(40, 30, 40, 30)
        }

        AlertDialog.Builder(this)
            .setTitle("Add Payment")
            .setMessage("Record a payment for ${currentCustomer?.name}")
            .setView(input)
            .setPositiveButton("Record") { _, _ ->
                val amountStr = input.text.toString().trim()
                val amount = amountStr.toDoubleOrNull()
                if (amount != null && amount > 0) {
                    val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                    RepairShopApp.database.addCustomerPayment(customerId, amount, "Part Payment", dateFormatted)
                    Toast.makeText(this, "Payment recorded successfully!", Toast.LENGTH_SHORT).show()
                    loadCustomerData()
                } else {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
