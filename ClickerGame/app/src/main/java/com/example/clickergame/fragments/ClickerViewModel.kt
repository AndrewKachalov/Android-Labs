package com.example.clickergame.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clickergame.R
import com.example.clickergame.models.ShopItem
import java.util.*

class ClickerViewModel : ViewModel() {

    //Загальна кількість печива
    private val _cookieCount = MutableLiveData(0)
    val cookieCount: LiveData<Int> = _cookieCount

    //система розрахунку покращення
    var ascendPower = 0
    var shopPower = 0

    //система додавання у магазин предметів
    private val _shopItems = MutableLiveData<List<ShopItem>>().apply {
        value = listOf(
            ShopItem(
                id = 1,
                iconResId = R.drawable.ic_cursor,
                name = "Cursor",
                level = 0,
                basePrice = 10,
                powerPerLevel = 1,
                cpsPerLevel = 0
            ),
            ShopItem(
                id = 2,
                iconResId = R.drawable.ic_grandma,
                name = "Grandma",
                level = 0,
                basePrice = 100,
                powerPerLevel = 0,
                cpsPerLevel = 1
            )
        )
    }
    val shopItems: LiveData<List<ShopItem>> = _shopItems

    //Таймер для кпс
    private var cpsTimer: Timer? = null

    init {
        startCpsTimer() //сам кпс
    }

    //натискання на печиво дохід
    fun getClickPower(): Int {
        return 1 + ascendPower + shopPower
    }
    //обробник
    fun click() {
        _cookieCount.value = (_cookieCount.value ?: 0) + getClickPower()
    }
    //витрачаємо
    fun spendCookies(amount: Int): Boolean {
        val current = _cookieCount.value ?: 0
        return if (current >= amount) {
            _cookieCount.value = current - amount
            true
        } else {
            false
        }
    }
    //покращення товару
    fun upgradeItem(itemId: Int, quantity: Int) {
        val updated = _shopItems.value?.map {
            if (it.id == itemId) {
                it.copy(level = it.level + quantity)
            } else it
        }
        _shopItems.value = updated

        //збільшення сили кліку
        shopPower = _shopItems.value?.sumOf { it.powerPerLevel * it.level } ?: 0
    }
    //загальний кпс
    fun getTotalCps(): Int {
        return _shopItems.value?.sumOf { it.cpsPerLevel * it.level } ?: 0
    }
    //таймер
    private fun startCpsTimer() {
        cpsTimer = Timer()
        cpsTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val cps = getTotalCps()
                if (cps > 0) {
                    _cookieCount.postValue((_cookieCount.value ?: 0) + cps)
                }
            }
        }, 1000, 1000)
    }
    //очистка
    override fun onCleared() {
        super.onCleared()
        cpsTimer?.cancel()
    }
}
