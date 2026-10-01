package com.repairshop.tracker.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.repairshop.tracker.R
import com.repairshop.tracker.RepairShopApp
import com.repairshop.tracker.databinding.ActivityMainBinding
import com.repairshop.tracker.ui.auth.LoginActivity
import com.repairshop.tracker.ui.customers.CustomersFragment
import com.repairshop.tracker.ui.dashboard.DashboardFragment
import com.repairshop.tracker.ui.dialogs.OfflineInfoBottomSheet
import com.repairshop.tracker.ui.dialogs.SettingsBottomSheet
import com.repairshop.tracker.ui.history.HistoryFragment
import com.repairshop.tracker.ui.items.ItemsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbarAndDrawer()
        setupBottomNavigation()
        setupOfflineBadge()

        if (savedInstanceState == null) {
            selectNavigationTab(TAB_DASHBOARD)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshDynamicUI()
    }

    private fun setupToolbarAndDrawer() {
        binding.topToolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.drawer_dashboard -> {
                    selectNavigationTab(TAB_DASHBOARD)
                    true
                }
                R.id.drawer_items -> {
                    selectNavigationTab(TAB_ITEMS)
                    true
                }
                R.id.drawer_customers -> {
                    selectNavigationTab(TAB_CUSTOMERS)
                    true
                }
                R.id.drawer_history -> {
                    selectNavigationTab(TAB_HISTORY)
                    true
                }
                R.id.drawer_offline_info -> {
                    showOfflineInfoDialog()
                    true
                }
                R.id.drawer_settings -> {
                    showSettingsDialog()
                    true
                }
                R.id.drawer_logout -> {
                    confirmLogout()
                    true
                }
                else -> false
            }
        }

        refreshDrawerHeader()
    }

    private fun refreshDrawerHeader() {
        val headerView = binding.navigationView.getHeaderView(0) ?: return
        val tvUserName = headerView.findViewById<TextView>(R.id.tvDrawerUserName)
        val tvShopName = headerView.findViewById<TextView>(R.id.tvDrawerShopName)
        val btnEdit = headerView.findViewById<android.widget.ImageView>(R.id.btnEditProfileHeader)

        val pref = RepairShopApp.prefManager
        tvUserName?.text = pref.userName
        tvShopName?.text = pref.shopName

        btnEdit?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            showSettingsDialog()
        }
    }

    private fun refreshOfflineBadge() {
        val pref = RepairShopApp.prefManager
        if (pref.isOfflineMode) {
            binding.btnOfflineBadge.setBackgroundResource(R.drawable.bg_badge_offline)
            binding.ivOfflineBadgeIcon.setImageResource(R.drawable.ic_offline)
            binding.ivOfflineBadgeIcon.setColorFilter(ContextCompat.getColor(this, R.color.badge_offline_text))
            binding.tvOfflineBadgeText.text = getString(R.string.badge_offline)
            binding.tvOfflineBadgeText.setTextColor(ContextCompat.getColor(this, R.color.badge_offline_text))
        } else {
            binding.btnOfflineBadge.setBackgroundResource(R.drawable.bg_badge_in)
            binding.ivOfflineBadgeIcon.setImageResource(R.drawable.ic_check_circle)
            binding.ivOfflineBadgeIcon.setColorFilter(ContextCompat.getColor(this, R.color.badge_in_text))
            binding.tvOfflineBadgeText.text = "Online"
            binding.tvOfflineBadgeText.setTextColor(ContextCompat.getColor(this, R.color.badge_in_text))
        }
    }

    fun refreshDynamicUI() {
        refreshDrawerHeader()
        refreshOfflineBadge()

        // Also refresh active fragment if it's Dashboard
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is DashboardFragment) {
            currentFragment.onResume()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    replaceFragment(DashboardFragment.newInstance(), getString(R.string.title_dashboard))
                    true
                }
                R.id.nav_items -> {
                    replaceFragment(ItemsFragment.newInstance(), getString(R.string.title_items))
                    true
                }
                R.id.nav_customers -> {
                    replaceFragment(CustomersFragment.newInstance(), getString(R.string.title_customers))
                    true
                }
                R.id.nav_history -> {
                    replaceFragment(HistoryFragment.newInstance(), getString(R.string.title_history))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupOfflineBadge() {
        binding.btnOfflineBadge.setOnClickListener {
            showOfflineInfoDialog()
        }
        refreshOfflineBadge()
    }

    private fun showOfflineInfoDialog() {
        OfflineInfoBottomSheet.newInstance().show(supportFragmentManager, OfflineInfoBottomSheet.TAG)
    }

    private fun showSettingsDialog() {
        SettingsBottomSheet.newInstance {
            refreshDynamicUI()
        }.show(supportFragmentManager, SettingsBottomSheet.TAG)
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_logout)
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Logout") { _, _ ->
                RepairShopApp.prefManager.logout()
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun selectNavigationTab(tabIndex: Int) {
        when (tabIndex) {
            TAB_DASHBOARD -> binding.bottomNav.selectedItemId = R.id.nav_dashboard
            TAB_ITEMS -> binding.bottomNav.selectedItemId = R.id.nav_items
            TAB_CUSTOMERS -> binding.bottomNav.selectedItemId = R.id.nav_customers
            TAB_HISTORY -> binding.bottomNav.selectedItemId = R.id.nav_history
        }
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        binding.topToolbar.title = title
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if (binding.bottomNav.selectedItemId != R.id.nav_dashboard) {
            selectNavigationTab(TAB_DASHBOARD)
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val TAB_DASHBOARD = 0
        const val TAB_ITEMS = 1
        const val TAB_CUSTOMERS = 2
        const val TAB_HISTORY = 3
    }
}
