package com.example.lr1 //package цього класу

//Імпорт бібліотек
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

//Оголошення класу як фрагмент (частина UI)
class FragmentResult : Fragment() {

    //Оголошення змінних для елементів UI
    private lateinit var resultText: TextView
    private lateinit var buttonCancel: Button

    //Інтерфейс для передачі даних до головної активності
    interface OnCancelListener {
        fun onCancelResult()
    }

    //Змінна для посилання на слухача
    private var cancelListener: OnCancelListener? = null

    //При прикріпленні до активності:
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Перевірка на наявність інтерфейсу та його прикріплення
        if (context is OnCancelListener) {
            cancelListener = context
        }
    }

    //Створення UI
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    //Відображення UI
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Пошук відповідних елементів
        resultText = view.findViewById(R.id.txtResult)
        buttonCancel = view.findViewById(R.id.btnCancel)
        //обробник подій для кнопки
        buttonCancel.setOnClickListener {
            cancelListener?.onCancelResult()
        }
    }

    //заповнення тексту
    fun setResultText(text: String) {
        resultText.text = text
    }
}
