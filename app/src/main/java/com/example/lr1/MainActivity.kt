package com.example.lr1 //package цього класу

//Імпорт бібліотек
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*

//Створення класу з елементами AppCompatActivity()
class MainActivity : AppCompatActivity() {

    //Оголошення змінних для елементів UI
    private lateinit var spinnerAuthors: Spinner      //Випадаючий список (для авторів)
    private lateinit var radioGroupYears: RadioGroup  //Група RadioButtons (для років)
    private lateinit var btnOk: Button                //Кнопка ОК (для виведення результату)
    private lateinit var txtResult: TextView          //Текстове поле (для виведення результату)

    //Метод при створенні активності
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)      //Підключення підготовки інтерфейсу
        setContentView(R.layout.activity_main)  //Підлючення XML-розмітки UI

        //Підключення елементів до змінних
        spinnerAuthors = findViewById(R.id.spinnerAuthors)
        radioGroupYears = findViewById(R.id.radioGroupYears)
        btnOk = findViewById(R.id.btnOk)
        txtResult = findViewById(R.id.txtResult)


        // Створення масиву для випадаючого списку
        val authors = listOf("— Виберіть автора —", "Леся Українка", "Іван Франко", "Тарас Шевченко")

        // Перетворення масиву у зрозумілий для випадаючого списку формат
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, authors)
        // Налаштовуємо при відкритті
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //встановлюємо у випадаючий список дані
        spinnerAuthors.adapter = adapter

        //обробник подій для натискання кнопки
        btnOk.setOnClickListener {
            //Зчитуємо вибір автора та року з відповідних полів
            val selectedAuthor = spinnerAuthors.selectedItem.toString()
            val selectedRadioId = radioGroupYears.checkedRadioButtonId

            //Перевірка на заповнення полів
            if (selectedAuthor == "— Виберіть автора —" || selectedRadioId == -1) {
                //Виведення повідомлення про помилку
                Toast.makeText(this, "Будь ласка, заповніть усі поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Зчитуємо текст обраної користувачем кнопки
            val selectedYear = findViewById<RadioButton>(selectedRadioId).text.toString()
            //Створюємо формат для виведеного тексту
            val result = "Ви обрали:\nАвтор: $selectedAuthor\nРік видання: $selectedYear"
            //Виведення результату у текстове поле
            txtResult.text = result
        }
    }
}