package com.example.clickergame.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.clickergame.R
import com.example.clickergame.viewmodels.ClickerViewModel
import kotlin.random.Random
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout

class ClickerFragment : Fragment() {

    //UI
    private lateinit var cookieImage: ImageView //Печиво
    private lateinit var counterText: TextView //Кількість печива
    private lateinit var cpsText: TextView //КПС
    private lateinit var rootLayout: ConstraintLayout //Для анімації

    //Вьюв модель для відображення
    private val viewModel: ClickerViewModel by activityViewModels()

    //Список для анімації падіння печива
    private val cookieImages = listOf(
        R.drawable.cookie_p1,
        R.drawable.cookie_p2,
        R.drawable.cookie_p3,
        R.drawable.cookie_p4
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Підключаємо лайаут
        val view = inflater.inflate(R.layout.fragment_clicker, container, false)

        //Інсталяція UI
        cookieImage = view.findViewById(R.id.cookieImage)
        counterText = view.findViewById(R.id.cookieCounterText)
        cpsText = view.findViewById(R.id.cpsText)
        rootLayout = view.findViewById(R.id.clickerRoot)

        //Оновлення кількості печива
        viewModel.cookieCount.observe(viewLifecycleOwner) {
            counterText.text = it.toString()
        }

        //Оновлення кпс
        viewModel.shopItems.observe(viewLifecycleOwner) {
            val cps = viewModel.getTotalCps()
            cpsText.text = "CPS: $cps"
        }

        //Обробник кліку по печиву
        cookieImage.setOnClickListener {
            viewModel.click()
            animateClick(cookieImage)
            spawnFallingCookie()
        }

        return view
    }

    //Анімація натискання на печиво
    private fun animateClick(view: ImageView) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }.start()
    }

    //Генерація падаючих печив
    private fun spawnFallingCookie() {
        val cookie = ImageView(requireContext()) //Окремий імедж вьюв
        val size = 100
        cookie.layoutParams = ViewGroup.LayoutParams(size, size)

        cookie.setImageResource(cookieImages.random())   //Випадкове зображення
        cookie.rotation = Random.nextInt(-45, 45).toFloat() //випадковий кут

        val screenWidth = rootLayout.width
        val randomX = Random.nextInt(0, screenWidth - size).toFloat()

        cookie.x = randomX
        cookie.y = -size.toFloat()
        rootLayout.addView(cookie) //відображення на екрані

        val screenHeight = rootLayout.height.toFloat()

        cookie.animate()
            .translationY(screenHeight)
            .setInterpolator(LinearInterpolator())
            .setDuration(2500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    rootLayout.removeView(cookie) //після падіння видаляємо
                }
            })
            .start()
    }
}
