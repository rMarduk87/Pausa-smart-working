package rpt.tool.hybridwalk.utils.services

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
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
import rpt.com.base.log.d
import rpt.tool.hybridwalk.MainActivity
import kotlin.time.Duration.Companion.milliseconds
import kotlin.math.sqrt

class StepTrackerService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private var isUsingAccelerometerFallback = false

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastSensorValue: Int = -1
    private var lastMovementTime: Long = System.currentTimeMillis()

    // Variabili dedicate al filtro dell'accelerometro (fallback emulatore / dispositivi privi di pedometro)
    private var lastStepAccTime: Long = 0
    private val STEP_TIME_THRESHOLD_MS = 300L // Finestra minima di tempo tra un passo e l'altro (debounce)
    private val ACCEL_THRESHOLD = 3.0f//11.5f     // Soglia di accelerazione minima per considerare l'impatto di un passo

    private var inactivityJob: Job? = null
    private val CHECK_INTERVAL = 5L * 60L * 1000L

    override fun onCreate() {
        super.onCreate()

        // 1. Il servizio DEVE dichiararsi Foreground subito
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 2. Tentiamo prima di usare il contapassi hardware nativo
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor != null) {
            isUsingAccelerometerFallback = false
            sensorManager.registerListener(
                this, stepSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            d("HybridWalkDebug", "Registrato sensore hardware: TYPE_STEP_COUNTER")
        } else {
            // 3. Fallback sull'accelerometro se il contapassi hardware non esiste (es. emulatori)
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometerSensor != null) {
                isUsingAccelerometerFallback = true
                sensorManager.registerListener(
                    this, accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                d("HybridWalkDebug", "Fallback attivo: registrato Sensor.TYPE_ACCELEROMETER")
            } else {
                d("HybridWalkDebug", "Nessun sensore compatibile trovato!")
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
        if (event == null) return

        if (!isUsingAccelerometerFallback && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            // --- GESTIONE CONTAPASSI HARDWARE REALE ---
            val currentSensorValue = event.values[0].toInt()
            d("HybridWalkDebug", "Sensore hardware attivo! Valore letto: $currentSensorValue")

            if (lastSensorValue == -1) {
                lastSensorValue = currentSensorValue
                return
            }

            val deltaSteps = currentSensorValue - lastSensorValue

            if (deltaSteps > 0) {
                lastMovementTime = System.currentTimeMillis()
                val todayEpoch = LocalDate.now().toEpochDay()
                serviceScope.launch {
                    RepositoryManager.hybridWalkRepository.incrementSteps(todayEpoch, deltaSteps)
                }
            }

            lastSensorValue = currentSensorValue

        } else if (isUsingAccelerometerFallback && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // --- GESTIONE FALLBACK ACCELEROMETRO (Emulatore / Dispositivi senza pedometro) ---
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calcolo della magnitudo vettoriale sottraendo la forza di gravità (~9.81)
            val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val netAcceleration = kotlin.math.abs(acceleration - 9.80665f)

            if (netAcceleration > ACCEL_THRESHOLD) {
                val currentTime = System.currentTimeMillis()
                // Controllo del debounce temporale per evitare falsi positivi da scuotimenti rapidi
                if (currentTime - lastStepAccTime > STEP_TIME_THRESHOLD_MS) {
                    lastStepAccTime = currentTime
                    lastMovementTime = currentTime

                    d("HybridWalkDebug", "Passo simulato da accelerometro rilevato! Magnitudo: $netAcceleration")

                    val todayEpoch = LocalDate.now().toEpochDay()
                    serviceScope.launch {
                        RepositoryManager.hybridWalkRepository.incrementSteps(todayEpoch, 1)
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

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
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        if (::sensorManager.isInitialized) {
            sensorManager.unregisterListener(this)
        }

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
                    val todayRecord = RepositoryManager.hybridWalkRepository
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
                R.string.sedentario))
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