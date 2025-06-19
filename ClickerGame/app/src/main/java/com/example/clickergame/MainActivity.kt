package com.example.clickergame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.clickergame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //Для доступу до інтерфйсу
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Вибір активності
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Підключення навігації
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        //Поєднання навігації з контролером
        binding.bottomNav.setupWithNavController(navController)

        //Вимикаємо залиття іконок (без цього іконки фіолетові)
        binding.bottomNav.itemIconTintList = null

        //Плаваюча кнопка для клікера
        binding.fabClicker.setOnClickListener {
            navController.navigate(R.id.clickerFragment) //Перехід до фрагменту

            //Вимкнення обраного елементу навігаційного меню
            binding.bottomNav.menu.setGroupCheckable(0, true, false)
            for (i in 0 until binding.bottomNav.menu.size()) {
                binding.bottomNav.menu.getItem(i).isChecked = false
            }
            //Вмикаємо перевірку вибору пункта меню
            binding.bottomNav.menu.setGroupCheckable(0, true, true)
        }
    }
}
