package rpt.tool.hybridwalk.utils.managers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.WearMainActivity

class WearNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "wear_reminder_channel"
        const val NOTIFICATION_ID_INACTIVITY = 1001
        const val NOTIFICATION_ID_GOAL = 1002
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Promemoria Movimento"
        val descriptionText = "Notifiche per l'inattività e gli obiettivi"

        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 250, 250)
        }
        notificationManager.createNotificationChannel(channel)
    }


    @SuppressLint("WearRecents")
    fun showInactivityReminder() {

        val intent = Intent(context, WearMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )


        val wearableExtender = NotificationCompat.WearableExtender()
            .setHintHideIcon(true)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Sgranchisciti!")
            .setContentText("Sei fermo da troppo tempo, fai due passi.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .extend(wearableExtender)
            .build()

        notificationManager.notify(NOTIFICATION_ID_INACTIVITY, notification)
    }


    fun showGoalReachedNotification(steps: Int) {
        val wearableExtender = NotificationCompat.WearableExtender()
            .setHintHideIcon(true)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Obiettivo Raggiunto! 🏆")
            .setContentText("Hai completato i tuoi $steps passi giornalieri. Ottimo lavoro!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .extend(wearableExtender)
            .build()

        notificationManager.notify(NOTIFICATION_ID_GOAL, notification)
    }
}