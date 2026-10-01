package com.repairshop.tracker.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.repairshop.tracker.R
import com.repairshop.tracker.databinding.ItemRecentEntryBinding
import com.repairshop.tracker.model.TransactionEntry

class RecentEntriesAdapter(
    private var entries: List<TransactionEntry>,
    private val onItemClick: (TransactionEntry) -> Unit
) : RecyclerView.Adapter<RecentEntriesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecentEntryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        val context = holder.itemView.context

        holder.binding.tvRecentCustomerName.text = entry.customerName
        holder.binding.tvRecentItemName.text = entry.itemName
        holder.binding.tvRecentDate.text = entry.dateIn

        if (entry.type.equals("IN", ignoreCase = true)) {
            holder.binding.tvRecentBadge.text = "IN"
            holder.binding.tvRecentBadge.setBackgroundResource(R.drawable.bg_badge_in)
            holder.binding.tvRecentBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_in_text))
        } else {
            holder.binding.tvRecentBadge.text = "OUT"
            holder.binding.tvRecentBadge.setBackgroundResource(R.drawable.bg_badge_out)
            holder.binding.tvRecentBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_out_text))
        }

        holder.itemView.setOnClickListener { onItemClick(entry) }
    }

    override fun getItemCount(): Int = entries.size

    fun updateData(newEntries: List<TransactionEntry>) {
        this.entries = newEntries
        notifyDataSetChanged()
    }
}
