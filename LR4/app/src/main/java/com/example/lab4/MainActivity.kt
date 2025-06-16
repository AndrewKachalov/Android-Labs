package com.example.lab4 //package цього класу

//Імпорт бібліотек
import android.app.AlertDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    // Змінні для омпонентів інтерфейсу
    private lateinit var playerView: com.google.android.exoplayer2.ui.PlayerView
    private lateinit var imageCover: ImageView
    private lateinit var txtFileName: TextView
    private lateinit var seekBarSpeed: SeekBar
    private lateinit var txtSpeedValue: TextView

    // Змінні для плеєрів
    private var mediaPlayer: android.media.MediaPlayer? = null
    private var exoPlayer: ExoPlayer? = null

    //URI (Uniform Resource Identifier) вибраного медіа (посилання на файл або на покликання)
    private var selectedAudioUri: Uri? = null
    private var selectedVideoUri: Uri? = null
    private var isAudio = true

    //Вибір аудіофайлу та попереднє налаштування
    private val audioPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            //Передача у URI зміних
            isAudio = true
            selectedAudioUri = it
            txtFileName.text = getFileName(it) //Отримання імені файлу

            //Демонстрація обкладинки для аудіо
            imageCover.visibility = View.VISIBLE
            playerView.visibility = View.GONE
            imageCover.setImageResource(R.drawable.audio_placeholder)

            //Зупинка відео, для того щоб вони не накладались
            exoPlayer?.stop()
            exoPlayer?.release()
            exoPlayer = null

            //Підготовка плеєра для аудіо
            mediaPlayer?.release()
            mediaPlayer = android.media.MediaPlayer().apply {
                setDataSource(this@MainActivity, it) //Встановлення URI
                prepare() //Підготовка плеєра

            }
            //Встановлення шкали швидкості на значенні 1х
            seekBarSpeed.progress = 67
            mediaPlayer?.seekTo(67)
            mediaPlayer?.start()

        }
    }

    //Вибір відеофайлу та попереднє налаштування
    private val videoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            //Передача у URI зміних
            isAudio = false
            selectedVideoUri = it
            txtFileName.text = getFileName(it) //Отримання імені файлу

            //Демонстрація обкладинки для відео
            imageCover.visibility = View.GONE
            playerView.visibility = View.VISIBLE

            //Зупинка аудіо, для того щоб вони не накладались
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            // Ініціалізуємо та налаштовуємо плеєр ExoPlayer
            exoPlayer?.release()
            exoPlayer = ExoPlayer.Builder(this).build()
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(it)
            exoPlayer!!.setMediaItem(mediaItem)
            exoPlayer!!.prepare()

            //Встановлення шкали швидкості на значенні 1х
            exoPlayer?.seekTo(67)
            seekBarSpeed.progress = 67
            exoPlayer?.play()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Прив'язка до елементів інтерфейсу
        playerView = findViewById(R.id.playerView)
        imageCover = findViewById(R.id.imageCover)
        txtFileName = findViewById(R.id.txtFileName)
        seekBarSpeed = findViewById(R.id.seekBarSpeed)
        txtSpeedValue = findViewById(R.id.txtSpeedValue)

        //Встановлення слухача для відслідковування зміни швидкості
        seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speed = 0.5f + progress / 200f * 1.5f //Діапазон від 0.5 до 2х
                txtSpeedValue.text = String.format("%.2fx", speed) //Зміна тексту відображення

                //В залежності від медіа встановлення швидкості
                if (isAudio && mediaPlayer != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val params = mediaPlayer!!.playbackParams ?: android.media.PlaybackParams()
                        params.speed = speed
                        mediaPlayer!!.playbackParams = params
                    }
                } else if (!isAudio && exoPlayer != null) {
                    exoPlayer!!.setPlaybackParameters(PlaybackParameters(speed))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //Встановлення шкали швидкості на значенні 1х
        seekBarSpeed.progress = 67
        mediaPlayer?.seekTo(67)

        //Обробник кнопки для аудіо
        findViewById<Button>(R.id.btnSelectAudio).setOnClickListener {
            audioPickerLauncher.launch("audio/*")
        }

        //Обробник кнопки для відео
        findViewById<Button>(R.id.btnSelectVideo).setOnClickListener {
            videoPickerLauncher.launch("video/*")
        }

        //Обробник кнопки для покликання
        findViewById<Button>(R.id.btnDownload).setOnClickListener {
            showDownloadDialog()
        }

        //Обробник кнопки Play
        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            //Перевірка для відтворення
            if (isAudio) {
                if (mediaPlayer == null && selectedAudioUri != null) {
                    mediaPlayer = android.media.MediaPlayer().apply {
                        setDataSource(this@MainActivity, selectedAudioUri!!)
                        prepare()
                    }
                }
                mediaPlayer?.start()
            } else {
                exoPlayer?.play()
            }
        }

        //Обробник кнопки Pause
        findViewById<Button>(R.id.btnPause).setOnClickListener {
            //Перевірка для паузи
            if (isAudio && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            } else if (!isAudio && exoPlayer?.isPlaying == true) {
                exoPlayer?.pause()
            }
        }

        //Обробник кнопки Stop
        findViewById<Button>(R.id.btnStop).setOnClickListener {
            //Перевірка для скидання
            if (isAudio && mediaPlayer != null) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            } else if (!isAudio && exoPlayer != null) {
                exoPlayer?.pause()
                exoPlayer?.seekTo(0)
            }
        }
    }

    //Створення діалогового вікна для введення посилання на завантаження
    private fun showDownloadDialog() {
        //Створення та налаштування тайтлу
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введіть URL медіафайлу")

        //Поле для введення посилання та його збереження
        val input = EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_URI
        builder.setView(input)

        //Кнопка завантажити
        builder.setPositiveButton("Завантажити") { dialog, _ ->
            val url = input.text.toString()
            downloadMedia(url)
            dialog.dismiss()
        }
        //Кнопка відміни для закриття без завантаження
        builder.setNegativeButton("Відміна") { dialog, _ -> dialog.cancel() }

        //Відображення
        builder.show()
    }

    //Функція для авантаження за посиланням
    private fun downloadMedia(url: String) {
        Thread {
            try {
                //Встановлення з'єднання
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                //Зчитування потоку та створення тимчасового файлу
                val inputStream = connection.inputStream
                val file = File(cacheDir, "downloadedMedia")
                val outputStream = FileOutputStream(file)

                //Копіювання потоку у тимчасовий файл
                inputStream.copyTo(outputStream)

                //Закриття потоку
                inputStream.close()
                outputStream.close()

                runOnUiThread {
                    val fileUri = Uri.fromFile(file)

                    // Визначимо тип файлу по розширенню (проста перевірка)
                    val isAudioFile = url.endsWith(".mp3") || url.endsWith(".wav") || url.endsWith(".m4a")

                    //Перевірка типу файлу та вибір налаштувань плеєра
                    isAudio = isAudioFile
                    if (isAudio) {
                        selectedAudioUri = fileUri
                        txtFileName.text = "Завантажений аудіофайл"
                        imageCover.visibility = View.VISIBLE
                        playerView.visibility = View.GONE
                        imageCover.setImageResource(R.drawable.audio_placeholder)

                        exoPlayer?.stop()
                        exoPlayer?.release()
                        exoPlayer = null

                        mediaPlayer?.release()
                        mediaPlayer = android.media.MediaPlayer().apply {
                            setDataSource(this@MainActivity, selectedAudioUri!!)
                            prepare()
                        }
                    } else {
                        selectedVideoUri = fileUri
                        txtFileName.text = "Завантажений відеофайл"
                        imageCover.visibility = View.GONE
                        playerView.visibility = View.VISIBLE

                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null

                        exoPlayer?.release()
                        exoPlayer = ExoPlayer.Builder(this).build()
                        playerView.player = exoPlayer
                        val mediaItem = MediaItem.fromUri(selectedVideoUri!!)
                        exoPlayer!!.setMediaItem(mediaItem)
                        exoPlayer!!.prepare()
                    }
                }

                //Обробник помилки
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Не вдалося завантажити файл", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    //Отримання назви файлу
    private fun getFileName(uri: Uri): String {
        var name = "Невідомий файл"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    //Заверешення (очищення ресурсів при закритті)
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        exoPlayer?.release()
        exoPlayer = null
    }
}
