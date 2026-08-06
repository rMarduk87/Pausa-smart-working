package rpt.tool.hybridwalk.utils.managers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rpt.tool.hybridwalk.utils.managers.SharedPreferencesManager
import java.time.LocalDate

class WearStepManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _currentSteps = MutableStateFlow(0)
    val currentSteps: StateFlow<Int> = _currentSteps

    fun startTracking() {
        stepSensor?.let {
            sensorManager.registerListener(this, it,
                SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopTracking() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalHardwareSteps = event.values[0].toInt()
            val todayEpoch = LocalDate.now().toEpochDay()


            if (SharedPreferencesManager.lastSavedDateEpochDay == 0L) {
                SharedPreferencesManager.lastSavedDateEpochDay = todayEpoch
                SharedPreferencesManager.stepOffset = totalHardwareSteps
            }


            if (todayEpoch > SharedPreferencesManager.lastSavedDateEpochDay) {

                SharedPreferencesManager.stepOffset = totalHardwareSteps
                SharedPreferencesManager.lastSavedDateEpochDay = todayEpoch


                SharedPreferencesManager.isGoalAlreadyNotified = false
            }


            var dailySteps = totalHardwareSteps - SharedPreferencesManager.stepOffset


            if (dailySteps < 0) {
                SharedPreferencesManager.stepOffset = 0
                dailySteps = totalHardwareSteps
            }

            _currentSteps.value = dailySteps
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}