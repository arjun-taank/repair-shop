package com.repairshop.tracker.ui.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.repairshop.tracker.R
import com.repairshop.tracker.databinding.ItemInventoryBinding
import com.repairshop.tracker.model.Item

class ItemsAdapter(
    private var items: List<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.binding.tvItemName.text = item.name
        holder.binding.tvItemCountSummary.text = "Total: ${item.totalCount} | In: ${item.inCount} | Out: ${item.outCount}"

        // Set device icon based on category
        val iconRes = when (item.category.lowercase().trim()) {
            "mobile", "mobile phone", "phone" -> R.drawable.ic_phone
            "laptop", "computer" -> R.drawable.ic_laptop
            "headphone", "headphones", "earbuds" -> R.drawable.ic_headphones
            "smart watch", "watch", "smartwatch" -> R.drawable.ic_watch
            "tablet", "ipad" -> R.drawable.ic_tablet
            else -> R.drawable.ic_inventory
        }
        holder.binding.ivItemCategoryIcon.setImageResource(iconRes)

        // Status badge
        if (item.isInStock) {
            holder.binding.tvItemStockBadge.text = "In Stock"
            holder.binding.tvItemStockBadge.setBackgroundResource(R.drawable.bg_badge_in)
            holder.binding.tvItemStockBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_in_text))
        } else {
            holder.binding.tvItemStockBadge.text = "Out Stock"
            holder.binding.tvItemStockBadge.setBackgroundResource(R.drawable.bg_badge_out)
            holder.binding.tvItemStockBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_out_text))
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Item>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
