package com.repairshop.tracker.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.repairshop.tracker.R
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.FragmentHistoryBinding
import com.repairshop.tracker.model.TransactionEntry

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HistoryAdapter
    private var allTransactions: List<TransactionEntry> = emptyList()
    private var currentFilter: String = "ALL" // "ALL", "IN", "OUT"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilters()
        loadHistory()
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(emptyList()) { entry ->
            showTransactionDetails(entry)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }

    private fun setupFilters() {
        binding.chipGroupHistory.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when {
                checkedIds.contains(R.id.chipHistoryIn) -> "IN"
                checkedIds.contains(R.id.chipHistoryOut) -> "OUT"
                else -> "ALL"
            }
            applyFilters()
        }
    }

    private fun loadHistory() {
        allTransactions = RepairShopApp.database.getAllTransactions()
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = when (currentFilter) {
            "IN" -> allTransactions.filter { it.type.equals("IN", ignoreCase = true) }
            "OUT" -> allTransactions.filter { it.type.equals("OUT", ignoreCase = true) }
            else -> allTransactions
        }
        adapter.updateData(filtered)
    }

    private fun showTransactionDetails(entry: TransactionEntry) {
        val details = """
            Customer: ${entry.customerName}
            Phone: ${entry.customerPhone}
            Item: ${entry.itemName} (${entry.itemCategory})
            Model: ${entry.itemModel}
            Entry Type: ${entry.type}
            Status: ${entry.status}
            Date IN: ${entry.dateIn} ${entry.timeIn}
            Date OUT: ${entry.dateOut ?: "Not yet returned"}
            Repair Charge: ${com.repairshop.tracker.RepairShopApp.prefManager.formatCurrency(entry.repairCharge)}
            Advance Paid: ${com.repairshop.tracker.RepairShopApp.prefManager.formatCurrency(entry.advancePaid)}
            Remarks: ${entry.remarks.ifEmpty { "None" }}
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Transaction Details")
            .setMessage(details)
            .setPositiveButton("Close", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }
}
