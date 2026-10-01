package com.repairshop.tracker.ui.customers

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.FragmentCustomersBinding
import com.repairshop.tracker.model.Customer

class CustomersFragment : Fragment() {

    private var _binding: FragmentCustomersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CustomersAdapter
    private var allCustomers: List<Customer> = emptyList()
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        loadCustomers()

        binding.fabAddCustomer.setOnClickListener {
            startActivity(Intent(requireContext(), AddCustomerActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadCustomers()
    }

    private fun setupRecyclerView() {
        adapter = CustomersAdapter(emptyList()) { customer ->
            val intent = Intent(requireContext(), CustomerDetailActivity::class.java).apply {
                putExtra("CUSTOMER_ID", customer.id)
            }
            startActivity(intent)
        }
        binding.rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomers.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearchCustomers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                filterCustomers()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadCustomers() {
        allCustomers = RepairShopApp.database.getAllCustomers()
        filterCustomers()
    }

    private fun filterCustomers() {
        val filtered = if (searchQuery.isEmpty()) {
            allCustomers
        } else {
            allCustomers.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true) ||
                it.address.contains(searchQuery, ignoreCase = true)
            }
        }
        adapter.updateData(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CustomersFragment()
    }
}
