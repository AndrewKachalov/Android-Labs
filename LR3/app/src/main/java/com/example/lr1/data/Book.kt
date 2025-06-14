package com.example.lr1.data //package цього класу

//Імпорт бібліотек
import androidx.room.Entity
import androidx.room.PrimaryKey

//Оголошення сутності (entity) таблиці
@Entity(tableName = "books")
data class Book(
    //Первинний ключ (id)
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //Поля формату рядку для збереження даних
    val author: String,
    val year: String
)