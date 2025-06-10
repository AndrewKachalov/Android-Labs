package com.example.lr1 //package цього класу

//Імпорт бібліотек
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

//Оголошення класу як фрагмент (частина UI)
class FragmentInput : Fragment() {

    //Оголошення змінних для елементів UI
    private lateinit var spinner: Spinner
    private lateinit var radioGroup: RadioGroup
    private lateinit var buttonOk: Button

    //Інтерфейс для передачі даних до головної активності
    interface OnInputConfirmedListener {
        fun onInputConfirmed(author: String, year: String)
    }

    //Змінна для посилання на слухача
    private var listener: OnInputConfirmedListener? = null

    //При прикріпленні до активності:
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Перевірка на наявність інтерфейсу та його прикріплення
        if (context is OnInputConfirmedListener) {
            listener = context
        }
    }

    //Створення UI
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_input, container, false)
    }
    //Відображення UI
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Пошук відповідних елементів
        spinner = view.findViewById(R.id.spinnerAuthors)
        radioGroup = view.findViewById(R.id.radioGroupYears)
        buttonOk = view.findViewById(R.id.btnOk)

        // Створення масиву для випадаючого списку
        val authors = listOf("— Виберіть автора —", "Леся Українка", "Іван Франко", "Тарас Шевченко")
        // Перетворення масиву у зрозумілий для випадаючого списку формат
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, authors)
        // Налаштовуємо при відкритті
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //встановлюємо у випадаючий список дані
        spinner.adapter = adapter
        //обробник подій для натискання кнопки
        buttonOk.setOnClickListener {
            //Зчитуємо вибір автора та року з відповідних полів
            val author = spinner.selectedItem.toString()
            val selectedId = radioGroup.checkedRadioButtonId
            //Перевірка на заповнення полів
            if (author == "— Виберіть автора —" || selectedId == -1) {
                Toast.makeText(requireContext(), "Будь ласка, оберіть автора і рік", Toast.LENGTH_SHORT).show()
            } else {
                // Якщо все заповнено, то викликаємо метод слухача
                val year = view.findViewById<RadioButton>(selectedId).text.toString()
                listener?.onInputConfirmed(author, year)
            }
        }
    }
    //очищення форми
    fun resetForm() {
        spinner.setSelection(0)
        radioGroup.clearCheck()
    }
}
