package com.example.lab5 //package

//Імпорти
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), SensorEventListener {

    //Змінні для сенсорів
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var proximitySensor: Sensor? = null

    //Об'єкти для відображення результатів
    private lateinit var txtLight: TextView
    private lateinit var txtProximity: TextView
    private lateinit var blackoutView: View

    //Змінна для вимкнення екрану
    private var isBlackoutActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Прив'язка до об'єктів
        txtLight = findViewById(R.id.txtLight)
        txtProximity = findViewById(R.id.txtProximity)

        //Отримання інформації з сенсорів
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        //Чорний View на весь екран для "глушіння"
        blackoutView = View(this).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 1f
            visibility = View.GONE
            //Блокуємо дотики
            setOnTouchListener { _, _ -> true }
        }
        //Застосування поверх усього
        addContentView(
            blackoutView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        //Запитуємо дозвіл на зміну системних налаштувань (яскравості)
        if (!Settings.System.canWrite(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    //Якщо повертаємось до активності, то застосовуємо сенсори
    override fun onResume() {
        super.onResume()
        lightSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximitySensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    //Якщо вийшли з додатку, припиняємо замірювання
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


    //Обробник подій сенсорів
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        //Перевірка на наявність сенсору
        when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                val lightLevel = event.values[0]
                txtLight.text = "Освітленість: $lightLevel lx"

                //Обробка освітленості
                if (Settings.System.canWrite(this)) {
                    val minLux = 1f //Мінімум
                    val maxLux = 1000f //Максимум

                    // Масштабуємо освітленість у діапазон з 10 до 255
                    val normalized = ((lightLevel - minLux) / (maxLux - minLux)).coerceIn(0f, 1f)
                    val brightnessValue = (10 + normalized * (255 - 10)).toInt()
                    //Зміна яскравості
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessValue)
                }
            }
            //Обробка датчика наближення
            Sensor.TYPE_PROXIMITY -> {
                val proximityValue = event.values[0]
                txtProximity.text = "Наближення: $proximityValue cm"

                val maxRange = proximitySensor?.maximumRange ?: 0f
                //Якщо близько, вимикаємо екран
                if (proximityValue < maxRange) {
                    activateBlackout(true)
                } else {
                    activateBlackout(false)
                }
            }
        }
    }


    //Показує або ховає чорний екран
    private fun activateBlackout(enable: Boolean) {
        if (enable && !isBlackoutActive) {
            blackoutView.visibility = View.VISIBLE
            isBlackoutActive = true
        } else if (!enable && isBlackoutActive) {
            blackoutView.visibility = View.GONE
            isBlackoutActive = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
