package rpt.tool.hybridwalk.utils.services

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.managers.RepositoryManager
import java.time.LocalDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import rpt.tool.hybridwalk.MainActivity
import kotlin.time.Duration.Companion.milliseconds

class StepTrackerService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastSensorValue: Int = -1
    private var lastMovementTime: Long = System.currentTimeMillis()
    private var inactivityJob: Job? = null
    private val CHECK_INTERVAL = 5L * 60L * 1000L

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val notification = createNotification()

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val hasActivityRecognition = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasActivityRecognition) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, notification)
        } else {
            // startForeground is available since API 5, but usually we don't call it if we don't have notification
            startForeground(NOTIFICATION_ID, notification)
        }

        val hasActivityRecognition = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (hasActivityRecognition) {
            stepSensor?.let {
                sensorManager.registerListener(
                    this, it,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAlertNotificationChannel()
        }
        startInactivityTimer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val currentSensorValue = event.values[0].toInt()

            if (lastSensorValue == -1) {
                lastSensorValue = currentSensorValue
                return
            }

            val deltaSteps = currentSensorValue - lastSensorValue

            if (deltaSteps > 0) {

                lastMovementTime = System.currentTimeMillis()

                val todayEpoch = LocalDate.now().toEpochDay()
                serviceScope.launch {
                    RepositoryManager.incrementSteps(todayEpoch, deltaSteps)
                }
            }

            lastSensorValue = currentSensorValue
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun createNotification(): Notification {
        val channelId = "hybridwalk_tracking_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                channelId,
                getString(R.string.tracciamento_passi),
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.hybridwalk))
            .setContentText(getString(R.string.tracciamento_attivo_in_background))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        sensorManager.unregisterListener(this)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MidnightResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
        inactivityJob?.cancel()
    }

    private fun createAlertNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "hybridwalk_alerts_channel"
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                channelId,
                getString(R.string.promemoria_movimento),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(
                    R.string.ti_ricorda_di_alzarti_se_stai_fermo_per_troppo_tempo
                )
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun startInactivityTimer() {
        inactivityJob = serviceScope.launch {
            while (isActive) {
                delay(CHECK_INTERVAL.milliseconds)

                val timeSinceLastMove = System.currentTimeMillis() - lastMovementTime
                val threshold = rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager.inactivityThreshold

                if (timeSinceLastMove >= threshold) {
                    val todayEpoch = LocalDate.now().toEpochDay()
                    val todayRecord = RepositoryManager
                        .getRecordByDate(todayEpoch).firstOrNull()

                    val isGymDay = todayRecord?.isGymDay ?: false

                    if (!isGymDay) {
                        sendStandUpNotification()
                    }

                    lastMovementTime = System.currentTimeMillis()
                }
            }
        }
    }

    private fun sendStandUpNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this,
            "hybridwalk_alerts_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.ora_di_sgranchirsi_le_gambe))
            .setContentText(getString(
                R.string.sei_seduto_da_60_minuti_alzati_per_bere_un_bicchiere_d_acqua))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(2, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}