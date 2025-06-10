package com.example.lr1 //package цього класу

//Імпорт бібліотек
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


//Створення класу з елементами AppCompatActivity()
class MainActivity : AppCompatActivity(),
    FragmentInput.OnInputConfirmedListener, //Отримання даних з input та обробник натискання Cancel
    FragmentResult.OnCancelListener {

    //Змінна для збереження фрагменту
    private lateinit var inputFragment: FragmentInput

    //Метод при створенні активності
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)     //Підключення підготовки інтерфейсу
        setContentView(R.layout.activity_main) //Підлючення XML-розмітки UI

        inputFragment = FragmentInput()     //Створення фразменту для виведення даних
        supportFragmentManager.beginTransaction()   //Створення цього фрагменту на екрані
            .replace(R.id.fragmentContainer, inputFragment)
            .commit()
    }
    //Метод при натисканні кнопки OK
    override fun onInputConfirmed(author: String, year: String) {
        val resultFragment = FragmentResult()     //Створення фрагменту результату

        supportFragmentManager.beginTransaction()   //Зміна фрагменту на результат
            .replace(R.id.fragmentContainer, resultFragment)
            .addToBackStack(null)
            .commit()

        //Виведення заміненого фрагменту
        supportFragmentManager.executePendingTransactions()
        //Редагування тексту
        resultFragment.setResultText("Автор: $author\nРік видання: $year")
    }

    //Обробник кнопки
    override fun onCancelResult() {
        supportFragmentManager.popBackStack()
        inputFragment.resetForm()
    }
}