package rpt.tool.hybridwalk.utils.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import rpt.tool.hybridwalk.utils.services.MidnightResetReceiver
import rpt.tool.hybridwalk.utils.services.StepTrackerService
import java.util.Calendar
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings

fun Context.startStepTrackerService() {
    val intent = Intent(this, StepTrackerService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

fun Context.stopStepTrackerService() {
    val intent = Intent(this, StepTrackerService::class.java)
    stopService(intent)
}

fun Context.scheduleMidnightReset() {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, MidnightResetReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
        this,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val windowLengthMillis = 15L * 60L * 1000L

    alarmManager.setWindow(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        windowLengthMillis,
        pendingIntent
    )
}

fun Context.isIgnoringBatteryOptimizations(): Boolean {
    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(packageName)
}

fun Context.createSafeBatterySettingsIntent(): Intent {
    return Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
}