package com.example.lr1 //package цього класу

//Імпорт бібліотек
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.lr1.data.*

//Створення класу з елементами AppCompatActivity()
class StorageActivity : AppCompatActivity() {
    //Змінна для збереження фрагменту
    private lateinit var listView: ListView
    private lateinit var buttonClearAll: Button
    private lateinit var buttonBack: Button
    private lateinit var db: BookDatabase
    private lateinit var books: List<Book>

    //Метод при створенні активності
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        //Оголошення змінних для елементів UI
        listView = findViewById(R.id.listViewBooks)
        buttonClearAll = findViewById(R.id.btnClearAll)
        buttonBack = findViewById(R.id.btnBack)

        //Оголошення бази даних
        db = BookDatabase.getDatabase(this)

        //Виклик завантаження даних
        loadBooks()

        //Обробник подій для функції очищення дб
        buttonClearAll.setOnClickListener {
            lifecycleScope.launch {
                //Видалення записів
                db.bookDao().deleteAll()
                //Виведення повідомлення
                Toast.makeText(this@StorageActivity, "Сховище очищено", Toast.LENGTH_SHORT).show()
                //Оновлення списку
                loadBooks()
            }
        }
        buttonBack.setOnClickListener{
            finish() //Закриття активності
        }

    }
    private fun loadBooks() {
        lifecycleScope.launch {
            books = db.bookDao().getAll() //Отримання даних
            val displayList = if (books.isEmpty()) {
                listOf("Сховище порожнє") //Якщо записів немає
            } else {
                books.map { "Автор: ${it.author}, Рік: ${it.year}" } //Якщо є виводимо за форматом
            }
            listView.adapter = ArrayAdapter(this@StorageActivity, android.R.layout.simple_list_item_1, displayList) //Коректне відображення
        }
    }
}
