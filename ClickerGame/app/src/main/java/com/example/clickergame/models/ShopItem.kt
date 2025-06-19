package com.example.clickergame.models
//Модель товару

data class ShopItem(
    val id: Int,
    val iconResId: Int,
    val name: String,
    val level: Int,
    val basePrice: Int,
    val powerPerLevel: Int,
    val cpsPerLevel: Int = 0
) {
    //збільшення ціни за рівень
    fun getCurrentPrice(): Int {
        var price = basePrice
        //на 15% для кожноо рівня
        repeat(level) {
            price = (price * 1.15f).toInt()
        }
        return price
    }
    //Розрахунок загальної суми
    fun getPriceForNext(quantity: Int): Int {
        var total = 0
        var currentPrice = getCurrentPrice()
        repeat(quantity) {
            total += currentPrice
            //збільшення ціни
            currentPrice = (currentPrice * 1.15f).toInt()
        }
        return total
    }
    //повернення кпс
    fun getCps(): Int = level * cpsPerLevel
}
