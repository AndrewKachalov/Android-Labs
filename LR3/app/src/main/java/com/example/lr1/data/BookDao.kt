package com.example.lr1.data //package цього класу

//Імпорт бібліотек
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

//Оголошення Data Access Object для доступу до таблиці
@Dao
interface BookDao {
    //Створення методу insert для запису даних у таблицю
    @Insert
    suspend fun insert(book: Book)
    //Створення методу для отримання усіх даних таблиці
    @Query("SELECT * FROM books")
    suspend fun getAll(): List<Book>
    //Створення методу для видалення усіх записів у таблиці
    @Query("DELETE FROM books")
    suspend fun deleteAll()
}