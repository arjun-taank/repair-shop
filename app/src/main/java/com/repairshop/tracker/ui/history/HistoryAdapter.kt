package com.repairshop.tracker.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.repairshop.tracker.R
import com.repairshop.tracker.databinding.ItemHistoryBinding
import com.repairshop.tracker.model.TransactionEntry

class HistoryAdapter(
    private var entries: List<TransactionEntry>,
    private val onItemClick: (TransactionEntry) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        val context = holder.itemView.context

        holder.binding.tvHistoryDateTime.text = "${entry.dateIn} | ${entry.timeIn}"
        val modelSuffix = if (entry.itemModel.isNotEmpty()) " (${entry.itemModel})" else ""
        holder.binding.tvHistoryItemTitle.text = "${entry.itemName}$modelSuffix"
        holder.binding.tvHistoryCustomerSub.text = "${entry.customerName} | ${entry.status}"

        val outDateStr = entry.dateOut ?: "-"
        holder.binding.tvHistoryDatesInOut.text = "IN: ${entry.dateIn} | OUT: $outDateStr"

        // Set device icon based on category
        val iconRes = when (entry.itemCategory.lowercase().trim()) {
            "mobile", "mobile phone", "phone" -> R.drawable.ic_phone
            "laptop", "computer" -> R.drawable.ic_laptop
            "headphone", "headphones", "earbuds" -> R.drawable.ic_headphones
            "smart watch", "watch", "smartwatch" -> R.drawable.ic_watch
            "tablet", "ipad" -> R.drawable.ic_tablet
            else -> R.drawable.ic_inventory
        }
        holder.binding.ivHistoryCategoryIcon.setImageResource(iconRes)

        if (entry.type.equals("IN", ignoreCase = true)) {
            holder.binding.tvHistoryBadge.text = "IN"
            holder.binding.tvHistoryBadge.setBackgroundResource(R.drawable.bg_badge_in)
            holder.binding.tvHistoryBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_in_text))
        } else {
            holder.binding.tvHistoryBadge.text = "OUT"
            holder.binding.tvHistoryBadge.setBackgroundResource(R.drawable.bg_badge_out)
            holder.binding.tvHistoryBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_out_text))
        }

        holder.itemView.setOnClickListener { onItemClick(entry) }
    }

    override fun getItemCount(): Int = entries.size

    fun updateData(newEntries: List<TransactionEntry>) {
        this.entries = newEntries
        notifyDataSetChanged()
    }
}
