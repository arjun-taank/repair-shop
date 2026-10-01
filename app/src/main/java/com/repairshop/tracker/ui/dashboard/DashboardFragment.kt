package com.repairshop.tracker.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.FragmentDashboardBinding
import com.repairshop.tracker.ui.customers.CustomerDetailActivity
import com.repairshop.tracker.ui.entry.AddEntryActivity
import com.repairshop.tracker.ui.main.MainActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecentEntriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = RecentEntriesAdapter(emptyList()) { entry ->
            val intent = Intent(requireContext(), CustomerDetailActivity::class.java).apply {
                putExtra("CUSTOMER_ID", entry.customerId)
            }
            startActivity(intent)
        }
        binding.rvRecentEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentEntries.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnViewAllRecent.setOnClickListener {
            (activity as? MainActivity)?.selectNavigationTab(MainActivity.TAB_HISTORY)
        }

        binding.cardStatTotalItems.setOnClickListener {
            (activity as? MainActivity)?.selectNavigationTab(MainActivity.TAB_ITEMS)
        }

        binding.cardStatTotalCustomers.setOnClickListener {
            (activity as? MainActivity)?.selectNavigationTab(MainActivity.TAB_CUSTOMERS)
        }

        binding.cardStatItemsIn.setOnClickListener {
            (activity as? MainActivity)?.selectNavigationTab(MainActivity.TAB_HISTORY)
        }

        binding.cardStatItemsOut.setOnClickListener {
            (activity as? MainActivity)?.selectNavigationTab(MainActivity.TAB_HISTORY)
        }

        binding.fabAddEntry.setOnClickListener {
            startActivity(Intent(requireContext(), AddEntryActivity::class.java))
        }
    }

    private fun loadData() {
        val db = RepairShopApp.database
        val pref = RepairShopApp.prefManager

        // Dynamic greeting based on time of day & SharedPreferences username
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val timeGreeting = when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
        binding.tvDashboardGreeting.text = "$timeGreeting, ${pref.userName} 👋"

        // Dynamic subtitle with shop name and mode
        val modeText = if (pref.isOfflineMode) "Offline Mode" else "Online Mode"
        binding.tvDashboardGreetingSub.text = "${pref.shopName} • $modeText"

        // Load aggregate counts
        val stats = db.getDashboardStats()
        binding.tvStatTotalItemsValue.text = stats.totalItems.toString()
        binding.tvStatTotalCustomersValue.text = stats.totalCustomers.toString()
        binding.tvStatItemsInValue.text = stats.itemsIn.toString()
        binding.tvStatItemsOutValue.text = stats.itemsOut.toString()

        // Load recent entries
        val recentList = db.getRecentTransactions(5)
        adapter.updateData(recentList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DashboardFragment()
    }
}
