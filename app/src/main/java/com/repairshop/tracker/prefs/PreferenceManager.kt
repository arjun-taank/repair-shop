package com.repairshop.tracker.prefs

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, DEFAULT_USER_NAME) ?: DEFAULT_USER_NAME
        set(value) = prefs.edit().putString(KEY_USER_NAME, value.trim()).apply()

    var shopName: String
        get() = prefs.getString(KEY_SHOP_NAME, DEFAULT_SHOP_NAME) ?: DEFAULT_SHOP_NAME
        set(value) = prefs.edit().putString(KEY_SHOP_NAME, value.trim()).apply()

    var phone: String
        get() = prefs.getString(KEY_PHONE, DEFAULT_PHONE) ?: DEFAULT_PHONE
        set(value) = prefs.edit().putString(KEY_PHONE, value.trim()).apply()

    var email: String
        get() = prefs.getString(KEY_EMAIL, DEFAULT_EMAIL) ?: DEFAULT_EMAIL
        set(value) = prefs.edit().putString(KEY_EMAIL, value.trim()).apply()

    var shopAddress: String
        get() = prefs.getString(KEY_SHOP_ADDRESS, DEFAULT_ADDRESS) ?: DEFAULT_ADDRESS
        set(value) = prefs.edit().putString(KEY_SHOP_ADDRESS, value.trim()).apply()

    var isOfflineMode: Boolean
        get() = prefs.getBoolean(KEY_IS_OFFLINE_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_OFFLINE_MODE, value).apply()

    var currencySymbol: String
        get() = prefs.getString(KEY_CURRENCY_SYMBOL, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
        set(value) = prefs.edit().putString(KEY_CURRENCY_SYMBOL, value.trim()).apply()

    var lastLoginTime: Long
        get() = prefs.getLong(KEY_LAST_LOGIN, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_LOGIN, value).apply()

    fun login(name: String, isOffline: Boolean = false) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            if (name.isNotBlank()) {
                putString(KEY_USER_NAME, name.trim())
            }
            putBoolean(KEY_IS_OFFLINE_MODE, isOffline)
            putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
            apply()
        }
    }

    fun logout() {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    fun updateProfile(userName: String, shopName: String, phone: String, currency: String, isOffline: Boolean) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, userName.trim())
            putString(KEY_SHOP_NAME, shopName.trim())
            putString(KEY_PHONE, phone.trim())
            putString(KEY_CURRENCY_SYMBOL, currency.trim())
            putBoolean(KEY_IS_OFFLINE_MODE, isOffline)
            apply()
        }
    }

    fun formatCurrency(amount: Double): String {
        return "$currencySymbol%,d".format(amount.toInt())
    }

    fun formatCurrency(amount: Int): String {
        return "$currencySymbol%,d".format(amount)
    }

    fun formatCurrency(amount: Long): String {
        return "$currencySymbol%,d".format(amount)
    }

    companion object {
        private const val PREF_NAME = "repair_shop_prefs"

        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_SHOP_NAME = "shop_name"
        private const val KEY_PHONE = "user_phone"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_SHOP_ADDRESS = "shop_address"
        private const val KEY_IS_OFFLINE_MODE = "is_offline_mode"
        private const val KEY_CURRENCY_SYMBOL = "currency_symbol"
        private const val KEY_LAST_LOGIN = "last_login"

        const val DEFAULT_USER_NAME = "Rajat Kumar"
        const val DEFAULT_SHOP_NAME = "Repairing Shop"
        const val DEFAULT_PHONE = "+91 98765 43210"
        const val DEFAULT_EMAIL = "repair@tracker.com"
        const val DEFAULT_ADDRESS = "Main Market, Shop #12"
        const val DEFAULT_CURRENCY = "₹"
    }
}
