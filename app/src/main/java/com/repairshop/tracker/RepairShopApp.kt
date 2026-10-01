package com.repairshop.tracker

import android.app.Application
import com.repairshop.tracker.db.DatabaseHelper

class RepairShopApp : Application() {

    companion object {
        lateinit var instance: RepairShopApp
            private set

        val database: DatabaseHelper by lazy {
            DatabaseHelper(instance)
        }

        val prefManager: com.repairshop.tracker.prefs.PreferenceManager by lazy {
            com.repairshop.tracker.prefs.PreferenceManager(instance)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
