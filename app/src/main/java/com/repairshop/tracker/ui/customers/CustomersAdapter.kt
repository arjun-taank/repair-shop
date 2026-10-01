package com.repairshop.tracker.ui.customers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.repairshop.tracker.R
import com.repairshop.tracker.databinding.ItemCustomerBinding
import com.repairshop.tracker.model.Customer
import java.text.NumberFormat
import java.util.Locale

class CustomersAdapter(
    private var customers: List<Customer>,
    private val onItemClick: (Customer) -> Unit
) : RecyclerView.Adapter<CustomersAdapter.ViewHolder>() {

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    inner class ViewHolder(val binding: ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val customer = customers[position]
        val context = holder.itemView.context

        holder.binding.tvCustomerName.text = customer.name
        holder.binding.tvCustomerPhone.text = customer.phone

        val balanceInt = customer.remainingBalance.toInt()
        val formattedBalance = com.repairshop.tracker.RepairShopApp.prefManager.formatCurrency(balanceInt)
        holder.binding.tvCustomerBalanceBadge.text = "Balance\n$formattedBalance"

        if (balanceInt > 0) {
            holder.binding.tvCustomerBalanceBadge.setBackgroundResource(R.drawable.bg_stat_green)
            holder.binding.tvCustomerBalanceBadge.setTextColor(ContextCompat.getColor(context, R.color.stat_green_text))
        } else {
            holder.binding.tvCustomerBalanceBadge.setBackgroundResource(R.drawable.bg_stat_blue)
            holder.binding.tvCustomerBalanceBadge.setTextColor(ContextCompat.getColor(context, R.color.stat_blue_text))
        }

        holder.itemView.setOnClickListener { onItemClick(customer) }
    }

    override fun getItemCount(): Int = customers.size

    fun updateData(newCustomers: List<Customer>) {
        this.customers = newCustomers
        notifyDataSetChanged()
    }
}
