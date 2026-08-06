package rpt.tool.hybridwalk

import android.app.Application
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager

class WearApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
    }
}